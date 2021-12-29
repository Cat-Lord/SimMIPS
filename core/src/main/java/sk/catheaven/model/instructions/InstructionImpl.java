package sk.catheaven.model.instructions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.core.instructions.Instruction;
import sk.catheaven.core.instructions.InstructionType;
import sk.catheaven.model.instructions.argumentTypes.DataArgumentType;
import sk.catheaven.model.instructions.argumentTypes.IntArgumentType;
import sk.catheaven.model.instructions.argumentTypes.LabelArgumentType;
import sk.catheaven.model.instructions.argumentTypes.RegisterArgumentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instruction dictates a format of written instruction and its representation in instruction code.
 * @author catlord
 */
@JsonIgnoreProperties(value = { "type" })
public class InstructionImpl implements Instruction {
    private final static Logger log = LogManager.getLogger();
    
    private InstructionType type;				// needs to be stored because we have to know how many bits should each field have
    private String mnemo;
    private List<ArgumentType> arguments;
    private Map<String, String> fields;	        // mapping each instruction field to a value (constant, argument or offset(base))
    private String description;
    
    public InstructionImpl() {
        arguments = new ArrayList<>();
        fields = new HashMap<>();
    }
    
    @Override
    public InstructionType getType() {
        return type;
    }
    
    public void setType(InstructionType type) {
        this.type = type;
    }
    
    @Override
    public String getMnemo() {
        return mnemo;
    }
    
    public void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }
    
    @Override
    public List<ArgumentType> getArguments() {
        return arguments;
    }
    
    public void setArguments(String[] arguments) {
        for (String argument : arguments)
            switch (argument) {
                case "reg"   -> this.arguments.add(new RegisterArgumentType());
                case "int"   -> this.arguments.add(new IntArgumentType());
                case "label" -> this.arguments.add(new LabelArgumentType());
                case "data"  -> this.arguments.add(new DataArgumentType());
                default -> log.warn("Unknown argument {}", argument);
            }
    }
    
    @Override
    public Map<String, String> getFields() {
        return fields;
    }
    
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
