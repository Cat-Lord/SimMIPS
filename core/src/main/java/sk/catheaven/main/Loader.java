package sk.catheaven.main;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.Connector;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.cpu.components.InstructionMemory;
import sk.catheaven.model.cpu.components.RegBank;
import sk.catheaven.model.instructions.Field;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.model.instructions.InstructionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads required json files and creates json objects from their contents.
 * @author catlord
 */
public class Loader {
	private static final Logger log = LogManager.getLogger();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String cpuResource = "/design/cpu.json";
	private static final String instructionsResource = "/design/instructions.json";

	private static Instruction[] instructionSet;
	private static CPU cpu;

	public static CPU getCPU() throws Exception {
		if (cpu == null)
			cpu = Loader.getCPU(Loader.class.getResourceAsStream(cpuResource));
		return cpu;
	}
	
	public static Instruction[] getInstructionSet() {
		if (instructionSet == null) {
			try {
				instructionSet = Loader.getInstructionSet(Loader.class.getResourceAsStream(instructionsResource));
			} catch (Exception exception) {
				log.error(exception.getMessage());
				instructionSet = new Instruction[0];
			}
		}
		return instructionSet;
	}
	
	public static CPU getCPU(InputStream jsonResource) throws Exception {

		if (cpu == null) {
			JsonNode root = objectMapper.readTree(jsonResource);

			int bitSize = root.get("CPU").get("BIT_SIZE").asInt();

			Map<String, Component> componentMap = new LinkedHashMap<>();
			InstructionMemory instructionMemory = null;
			RegBank regBank = null;
			for (JsonNode node : root.path("CPU").path("components")) {
				Component component = objectMapper.treeToValue(node, Component.class);
				componentMap.put(component.getLabel(), component);

				if (component instanceof InstructionMemory)
					instructionMemory = (InstructionMemory) component;

				if (component instanceof RegBank)
					regBank = (RegBank) component;
			}

			Map<String, List<Connector>> connectorsMap = getConnectorsMap(root.path("CPU").path("connectors"));

			CPU loadedCPU = new CPU();
			loadedCPU.setBitSize(bitSize);
			loadedCPU.setComponents(componentMap);
			loadedCPU.setConnectors(connectorsMap);
			loadedCPU.setRegBank(regBank);
			loadedCPU.setInstructionMemory(instructionMemory);
			cpu = loadedCPU;
		}

		return cpu;
	}
	
	private static Map<String, List<Connector>> getConnectorsMap(JsonNode connectors) throws JsonProcessingException {
		List<Connector> connectorList = objectMapper.readValue( connectors.toString(), new TypeReference<>() {} );
		
		Map<String, List<Connector>> sourceToTargetConnectors = new HashMap<>();
		
		for (Connector connector : connectorList) {
			List<Connector> connectorsToTargetComponents = sourceToTargetConnectors.get(connector.getFrom());
			
			if (connectorsToTargetComponents == null) {
				List<Connector> connectedTo = new ArrayList<>();
				connectedTo.add(connector);
				sourceToTargetConnectors.put(connector.getFrom(), connectedTo);
				continue;
			}
			
			connectorsToTargetComponents.add(connector);
		}
		
		return sourceToTargetConnectors;
	}
	
	public static Instruction[] getInstructionSet(InputStream jsonResource)  {
		try {
			JsonNode root = objectMapper.readTree(jsonResource);
			JsonNode instructionsRootNode = root.get("instructions");
			final InstructionType[] instructionTypes = getInstructionTypes(root.get("instructionTypes"));
			final Instruction[] instructions = objectMapper.treeToValue(instructionsRootNode, Instruction[].class);

			assignInstructionTypesToInstructions(instructionsRootNode, instructions, instructionTypes);
			Arrays.stream(instructions).forEach(Loader::isValidInstruction);

			return instructions;
		} catch (Exception exception) { log.error(exception.getMessage()); }

		return new Instruction[0];
	}
	
	private static InstructionType[] getInstructionTypes(JsonNode root) throws JsonProcessingException {
		final InstructionType[] instructionTypes
				= objectMapper.treeToValue(root, InstructionType[].class);
		
		for(InstructionType type : instructionTypes)
			log.debug(type.getLabel() + ": " + type.getFields().size() + " field(s)");
		
		return instructionTypes;
	}
	
	private static void assignInstructionTypesToInstructions(JsonNode node, Instruction[] instructions, InstructionType[] instructionTypes) {
		for (int i = 0; i < instructions.length; i++) {
			String type = node.get(i).get("type").asText();
			Instruction instruction = instructions[i];
			
			try {
				instruction.setType(getInstructionType(instructionTypes, type));
			} catch (TypeNotPresentException e) { e.getMessage(); }
		}
	}
	
	private static InstructionType getInstructionType(InstructionType[] instructionTypes, String type) {
		for (InstructionType instructionType : instructionTypes) {
			if (instructionType.getLabel().equalsIgnoreCase(type))
				return instructionType;
		}
		throw new TypeNotPresentException(type, null);
	}
	
	/**
	 * Instruction should have all the fields that are specified of its instruction type. If a field is missing,
	 * returns false, otherwise returns true.
	 * @param instruction Instruction to be checked.
	 */
	private static boolean isValidInstruction(Instruction instruction) {
		InstructionType type = instruction.getType();
		Map<?, ?> fields = instruction.getFields();
		
		for (Field expectedField : type.getFields()) {
			if ( ! fields.containsKey(expectedField.getLabel().toLowerCase())) {
				log.error("""
                        Invalid instruction: Instruction is missing an expected Field.\s
                        \tField "{}"
                        \tInstruction {} of type {}""", expectedField.getLabel(), instruction.getMnemo(), instruction.getType());
				return false;
			}
		}
		
		return true;
	}
}