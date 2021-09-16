package sk.catheaven.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.Connector;
import sk.catheaven.model.instructions.Field;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.model.instructions.InstructionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SpringBootConfiguration
public class AppConfig {
    private static final Logger log = LogManager.getLogger();

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean(name="cpuRootNode")
    public JsonNode getCpuRootNode(@Value("classpath:design/cpu.json") InputStream jsonResource,
                                                                        ObjectMapper objectMapper) throws IOException {
        return objectMapper.readTree(jsonResource);
    }

    @Bean(name="instructionsRootNode")
    public JsonNode getInstructionsRootNode(@Value("classpath:design/instructions.json") InputStream jsonResource,
                                           ObjectMapper objectMapper) throws IOException {
        return objectMapper.readTree(jsonResource);
    }

    // -------------------------------
    // ---------------- CPU
    // -------------------------------

    @Bean
    public int getBitSize(@Qualifier("cpuRootNode") JsonNode cpuRootNode) {
        return cpuRootNode.get("CPU").get("BIT_SIZE").asInt();
    }

    @Bean
    public Map<String, List<Connector>> getConnectorsMap(ObjectMapper objectMapper,
                                                         @Qualifier("cpuRootNode") JsonNode cpuRootNode) throws JsonProcessingException {
        JsonNode connectors = cpuRootNode.path("CPU").path("connectors");
        List<Connector> connectorList = objectMapper.readValue(connectors.toString(), new TypeReference<>() {});

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

    @Bean
    public Map<String, Component> getComponentMap(ObjectMapper objectMapper,
                                                  @Qualifier("cpuRootNode")  JsonNode cpuRootNode) throws JsonProcessingException {
        Map<String, Component> componentMap = new LinkedHashMap<>();
        for (JsonNode node : cpuRootNode.path("CPU").path("components")) {
            Component component = objectMapper.treeToValue(node, Component.class);
            componentMap.put(component.getLabel(), component);
        }
        return componentMap;
    }


    // -------------------------------
    // ---------------- INSTRUCTIONS
    // -------------------------------

    @Bean(name = "instructions;instructionsSet")
    public Instruction[] getInstructionSet(ObjectMapper objectMapper,
                                           @Qualifier("instructionsRootNode") JsonNode instructionsRootNode
    ) throws JsonProcessingException {
        final InstructionType[] instructionTypes = objectMapper.treeToValue(instructionsRootNode.get("instructionTypes"),
                                                                            InstructionType[].class);

        JsonNode instructionsNode = instructionsRootNode.get("instructions");
        final Instruction[] instructions = objectMapper.treeToValue(instructionsNode, Instruction[].class);

        assignInstructionTypesToInstructions(instructionsNode, instructions, instructionTypes);
        List<Instruction> allInstructions = new LinkedList<>();

        for (Instruction instruction : instructions)
            if (isValidInstruction(instruction))
                allInstructions.add(instruction);

        return allInstructions.toArray(new Instruction[0]);
    }

    private void assignInstructionTypesToInstructions(JsonNode currentInstruction,
                                                      Instruction[] instructions, 
                                                      InstructionType[] instructionTypes) {
        for (int i = 0; i < instructions.length; i++) {
            String type = currentInstruction.get(i).get("type").asText();
            Instruction instruction = instructions[i];

            try {
                instruction.setType(getTypeForInstruction(instructionTypes, type));
            } catch (TypeNotPresentException e) { e.getMessage(); }
        }
    }

    private InstructionType getTypeForInstruction(InstructionType[] instructionTypes, String expectedType) {
        for (InstructionType instructionType : instructionTypes) {
            if (instructionType.getLabel().equalsIgnoreCase(expectedType))
                return instructionType;
        }
        throw new TypeNotPresentException(expectedType, null);
    }

    /**
     * Instruction should have all the fields that are specified of its instruction type. If a field is missing,
     * returns false, otherwise returns true.
     * @param instruction Instruction to be checked.
     */
    private boolean isValidInstruction(Instruction instruction) {
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
