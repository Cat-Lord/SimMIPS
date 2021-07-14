package sk.catheaven.main;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.instruction.Field;
import sk.catheaven.instruction.Instruction;
import sk.catheaven.instruction.InstructionType;
import sk.catheaven.utils.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

public class Loader {
    private static Logger log = LogManager.getLogger();
    private static ObjectMapper objectMapper = getObjectMapper();
    
    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
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
            log.debug(type.getType() + ": " + type.getFields().size() + " field(s)");
    
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
            if (instructionType.getType().equalsIgnoreCase(type))
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
                        \tInstruction {}""", expectedField.getLabel(), instruction);
                return false;
            }
        }
        
        return true;
    }
}