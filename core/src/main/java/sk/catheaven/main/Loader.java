/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.main;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Connector;
import sk.catheaven.model.components.CPU;
import sk.catheaven.model.components.Component;
import sk.catheaven.model.instructions.Field;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.model.instructions.InstructionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads required json files and creates json objects from their contents.
 * @author catlord
 */
public class Loader {
	private static Logger log = LogManager.getLogger();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static CPU getCPU(InputStream jsonResource) throws Exception {
		JsonNode root = objectMapper.readTree(jsonResource);
		
		int bitSize = root.get("CPU").get("BIT_SIZE").asInt();
		
		Map<String, Component> componentMap = new LinkedHashMap<>();
		for (JsonNode node : root.path("CPU").path("components")) {
			Component component = objectMapper.treeToValue(node, Component.class);
			componentMap.put(component.getLabel(), component);
		}
		
		List<Connector> connectorList = objectMapper.readValue(
				root.path("CPU").path("connectors").toString(),
				new TypeReference<List<Connector>>() {}
		);
		
		CPU cpu = new CPU();
		cpu.setBitSize(bitSize);
		cpu.setComponents(componentMap);
		cpu.setConnectors(connectorList);
		return cpu;
	}
	
	public static Instruction[] getInstructionSet(InputStream jsonResource) throws IOException {
		JsonNode root = objectMapper.readTree(jsonResource);
		JsonNode instructionsRootNode = root.get("instructions");
		final InstructionType[] instructionTypes = getInstructionTypes(root.get("instructionTypes"));
		final Instruction[] instructions = objectMapper.treeToValue(instructionsRootNode, Instruction[].class);
		
		assignInstructionTypesToInstructions(instructionsRootNode, instructions, instructionTypes);
		Arrays.stream(instructions).forEach(Loader::isValidInstruction);
		
		return instructions;
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