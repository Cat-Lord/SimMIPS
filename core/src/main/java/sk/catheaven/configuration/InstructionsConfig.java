package sk.catheaven.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.catheaven.model.instructions.Field;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.model.instructions.InstructionType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Configuration
public class InstructionsConfig {
    private static final Logger log = LogManager.getLogger();

    @Bean
    public Instruction[] instructionSet(ObjectMapper objectMapper,
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
