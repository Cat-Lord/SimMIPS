package sk.catheaven.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import sk.catheaven.core.instructions.Field;
import sk.catheaven.core.instructions.Instruction;
import sk.catheaven.core.instructions.InstructionType;
import sk.catheaven.model.instructions.InstructionImpl;
import sk.catheaven.model.instructions.InstructionTypeImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Configuration
public class InstructionsConfig {
    private static final Logger log = LogManager.getLogger();

    @Bean
    @Primary
    public Map<String, InstructionType> instructionTypesSet(ObjectMapper objectMapper,
                                        @Qualifier("instructionsRootNode") JsonNode instructionsRootNode
    ) throws JsonProcessingException {
        InstructionType[] instructionTypes = objectMapper.treeToValue(instructionsRootNode.get("instructionTypes"), InstructionTypeImpl[].class);
        Map<String, InstructionType> instructionTypesMap = new HashMap<>();
        for (InstructionType type : instructionTypes)
            instructionTypesMap.put(type.getLabel(), type);
        return instructionTypesMap;
    }

    @Bean
    @Primary
    public Instruction[] instructionSet(ObjectMapper objectMapper,
                                        Map<String, InstructionType> instructionTypesSet,
                                        @Qualifier("instructionsRootNode") JsonNode instructionsRootNode
    ) throws JsonProcessingException {
        JsonNode instructionsNode = instructionsRootNode.get("instructions");
        final InstructionImpl[] instructions = objectMapper.treeToValue(instructionsNode, InstructionImpl[].class);

        assignInstructionTypesToInstructions(instructionsNode, instructions, instructionTypesSet);
        List<Instruction> allInstructions = new LinkedList<>();

        for (Instruction instruction : instructions)
            if (isValidInstruction(instruction))
                allInstructions.add(instruction);

        return allInstructions.toArray(new Instruction[0]);
    }

    // we need to use InstructionImpl to adjust the Instruction (set its corresponding type) otherwise
    // we would need to change the interface to allow setting (and this is not ideal in this case)
    private void assignInstructionTypesToInstructions(JsonNode currentInstruction,
                                                      InstructionImpl[] instructions,
                                                      Map<String, InstructionType> instructionTypes) {
        for (int i = 0; i < instructions.length; i++) {
            String type = currentInstruction.get(i).get("type").asText();
            InstructionImpl instruction = instructions[i];

            try {
                instruction.setType(getTypeForInstruction(instructionTypes, type));
            } catch (TypeNotPresentException e) { e.getMessage(); }
        }
    }

    private InstructionType getTypeForInstruction(Map<String, InstructionType> instructionTypes, String expectedType) {
        for (InstructionType instructionType : instructionTypes.values()) {
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
