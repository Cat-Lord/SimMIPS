package sk.catheaven.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.SyntaxErrorsContainer;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.instructions.ArgumentType;
import sk.catheaven.model.instructions.Field;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.model.instructions.argumentTypes.DataArgumentType;
import sk.catheaven.model.instructions.argumentTypes.LabelArgumentType;
import sk.catheaven.utils.DataFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembles user code, which is not formatted. Removes comments and trims arguments as well as
 * instruction mnemos. Assembler needs to know the instruction set to be able to correctly
 * parse instructions provided in user code.
 * Also knows how to calculate address from index in array, list or any other similar collection
 * or an index from a given address.
 * @author catlord
 */
public class Assembler {
    public final static String LABEL_TRAILING_CHAR = ":";   // represents the last character that ends a label
    public final static String DATA_CHAR = ".";				// if present, the string containing this symbol is a data reference (.base or .offset)
    public final static String COMMENT_CHAR = ";";			// comment in user code
    public final static String POSITIONAL_CHAR = "#";		// denotes position (can be found in field values of instruction).
    public final static String REGISTER_SYMBOL = "r";
    
    private final static Logger log = LogManager.getLogger();
    private final Map<String, Instruction> instructionSet;
    private final Map<String, Data> labels = new HashMap<>();
    private final SyntaxErrorsContainer syntaxErrors = new SyntaxErrorsContainer();
    
    
    public Assembler(Map<String, Instruction> instructionSet) {
        this.instructionSet = instructionSet;
    }
    
    /**
     * Tries to find instructions and assigns every instruction an address if that instruction is valid. Invalid
     * instruction produce errors, which are stored in <code>SyntaxErrorContainer</code>, which are cleared every
     * time this method gets called.
     * @param code Complete code obtained from user.
     * @return List of assembled instructions.
     */
    public List<AssembledInstruction> assembleCode(String code) {
        String[] codeLines = adjustCode(code);					// remove redundancies
        syntaxErrors.clear();
        
        extractLabels(codeLines);		// get all labels with their respective addresses
        
        List<AssembledInstruction> assembled = new ArrayList<>();
        // since we are skipping empty lines, we need to keep track of current (instruction) line
        int instructionIndex = 0;
    
        for (int lineNumber = 0; lineNumber < codeLines.length; lineNumber++) {
            codeLines[lineNumber] = codeLines[lineNumber].trim();
            
            if (! codeLines[lineNumber].isEmpty()) {
                AssembledInstruction assembledInstruction = assembleInstruction(lineNumber, codeLines[lineNumber], instructionIndex);
            
                if (assembledInstruction != null)
                    assembled.add(assembledInstruction);
                instructionIndex++;     // even if the instruction wasn't valid, we still evaluated a line with instruction
            }
        }
        
        return assembled;
    }
    
    /**
     * Assembles the instruction and creates an instance of <code>AssembledInstruction</code>.
     * <bold>Note: </bold> This method is necessary to be private to ensure proper code formatting before actually
     * trying to assemble line of code. Whole code must be cleaned up before trying to use its lines for assembling.
     *
     * @param lineIndex The index (number) of line in code this instruction is from.
     * @param instruction Instruction to parse (as entered by user).
     * @param instructionIndex Number representing the order of instruction to parse. Used to calculate instruction address.
     * @return Assembled instruction
     */
    private AssembledInstruction assembleInstruction(int lineIndex, String instruction, int instructionIndex) {
        log.debug("Assembling `{}`", instruction);
        
        String[] args = getInstructionArguments(instruction);
        
        // example: "mul"
        String mnemo = instruction.split(" ")[0];
        mnemo = mnemo.toLowerCase().trim();
        
        if (isValidInstruction(mnemo, args) == false) {
            log.debug("Failed: Invalid instruction or arguments");
            return null;
        }
        
        Data address = computeAddress(instructionIndex);
        Data iCode = createICode(mnemo, args, address);
        
        log.debug("..Done ! Errors: " + syntaxErrors.size());
        
        return new AssembledInstruction(lineIndex, this.instructionSet.get(mnemo), instruction, iCode, address);
    }
    
    /**
     * Only checks instruction by the Instruction class, which dictates its format.
     * @param instructionMnemo Instruction mnemo.
     * @param args Array of arguments provided for the instruction.
     */
    private boolean isValidInstruction(String instructionMnemo, String[] args) {
        Instruction instruction = instructionSet.get(instructionMnemo);
        
        if (instruction == null) {
            syntaxErrors.addError("Unknown instruction `" + instructionMnemo + "` !");
            return false;
        }
        
        if (args.length < instruction.getArguments().size()) {
            syntaxErrors.addError("Not enough arguments for instruction " + instruction.getMnemo() + " (" + instruction.getArguments().size() + " required)");
            return false;
        }
        
        if (args.length > instruction.getArguments().size()) {
            syntaxErrors.addError("Too many arguments for instruction " + instruction.getMnemo() + " (" + instruction.getArguments().size() + " required)");
            return false;
        }
        
        // check each argument against arguments in instruction (if are correctly formatted)
        for (int i = 0; i < args.length; i++) {
            ArgumentType type = instruction.getArguments().get(i);
            String currentArgument = args[i].trim();
            if (type.isValidArgument(currentArgument.trim()) == false) {
                syntaxErrors.addError("Invalid argument:" +
                                                  type.getClass().getSimpleName() +
                                                  " returned error for `" +
                                                  currentArgument + "`"
                                              );
                return false;
            }
            
            // check if the label argument exists in labels
            if (instruction.getArguments().get(i) instanceof LabelArgumentType)
                if (labels.get(args[i]) == null) {
                    syntaxErrors.addError("Argument `" + args[i] + "` branches to an undefined label");
                    return false;
                }
        }
        
        return true;
    }
    
    /**
     * From the instruction mnemo and provided arguments creates instruction code.
     * @param instructionMnemo Instruction Mnemo
     * @param args Array of instruction argument labels
     */
    private Data createICode(String instructionMnemo, String[] args, Data address) {
        Data iCode = new Data();
        
        Instruction instruction = instructionSet.get(instructionMnemo);
        List<Field> fields = instruction.getType().getFields();
        
        int shiftBy;
        for (Field field : fields) {
            String fieldValue = instruction.getFields().get(field.getLabel());
            int tempCode = iCode.getData();
            shiftBy = field.getBitSize();        // remember, by ho many bits we need to shift in next iteration
            tempCode <<= shiftBy;
        
            // there is no positional character
            if (!fieldValue.contains(POSITIONAL_CHAR)) {
                int value = Integer.parseInt(fieldValue);
                tempCode |= value;
            } else {
                // there is a positional character
            
                // first check, if the field is a data field
                if (fieldValue.contains(DATA_CHAR)) {
                    // get the argument type, because it knows how to parse the argument to get specific parts of that argument
                    // For example: Ask the data argument to get ".base" and it knows, how to get it.
                    Data value = DataArgumentType.getPart(
                                    args[extractPositionalNumber(fieldValue)],
                                    DataArgumentType.Part.of(fieldValue)
                                 );
                    
                    if (value == null)
                        log.warn("Data value for `{}` is NULL", fieldValue);
                    else {
                        log.info("Data value for `{}` is {}", fieldValue, value.getData());
                        tempCode |= value.getData();
                    }
                    
                } else {
                    int seq = extractPositionalNumber(fieldValue);
                
                    // if this arguments is a label argument, calculate offset (this address - target address)
                    if (seq < instruction.getArguments().size() && instruction.getArguments().get(seq) instanceof LabelArgumentType) {
                        Data offset = new Data(shiftBy);        // we calculate offset that should be only this size wide
                        offset.setData(labels.get(args[seq]).getData() - address.getData() - CPU.getByteSize());
                        tempCode |= offset.getData();
                    } else {
                        try {
                            // get the data from argument according to its type
                            Data value = instruction.getArguments().get(seq).getData(args[seq]);
                            log.info("Positional value for `{}` is {}", fieldValue, value.getData());
                            tempCode |= value.getData();
                        } catch (IndexOutOfBoundsException e) {
                            log.warn("Argument data char: Index out of bounds ! Index: {}, number of args: {}",
                                    seq, args.length);
                        }
                    }
                }
            }
            iCode.setData(tempCode);
        }
        
        log.info("Assembled {}: iCode `{}`", String.format("%4s", instructionMnemo), DataFormatter.getBinary(iCode));
        return iCode;
    }
    
    /**
     * When there is a label ending symbol in code line, we remember address
     * of instruction, that follows the label. Label format is checked as
     * well as it's originality (duplicates are not allowed) and if there is
     * an instruction following the label.
     * @param codeLines Instructions array with possible syntactical errors.
     */
    private void extractLabels(String[] codeLines) {
        labels.clear();
        
        log.debug("Extracting labels..");
        
        int instructionIndex = 0;
        for (int lineIndex = 0; lineIndex < codeLines.length; lineIndex++) {
            
            if (codeLines[lineIndex].isEmpty())
                continue;
            
            // if there is a label, remember it along with the address
            // of that instruction to allow assembling of the code.
            if (codeLines[lineIndex].contains(LABEL_TRAILING_CHAR)) {
                int labelEndPosition = codeLines[lineIndex].indexOf(LABEL_TRAILING_CHAR);
                String label = codeLines[lineIndex].substring(0, labelEndPosition);			// extract the label
                
                LabelArgumentType.isValidLabel(label);
                
                // throw an exception if there is a label without any instruction following it
                if (labelEndPosition + 1  >=  codeLines[lineIndex].length()) {
                    syntaxErrors.addError(lineIndex, "Label `" + label + "` doesn't bind to any instruction");
                    continue;
                }
                
                // extract the label from the code line
                codeLines[lineIndex] = codeLines[lineIndex].substring(labelEndPosition+1);
                
                if (labels.get(label) == null) {
                    labels.put(label, Assembler.computeAddress(instructionIndex));
                    log.debug("\tCreated label `{}` (address {})",
                            label, DataFormatter.getHex(Assembler.computeAddress(instructionIndex - 1)));
                }
                else {
                    syntaxErrors.addError(lineIndex, "Duplicate label declaration `" + label + "` !");
                    continue;
                }
            }
            
            instructionIndex++;		// increase address with each instruction !
        }
        
        log.debug("..Done ! Errors: " + syntaxErrors.size());
    }
    
    /**
     * Form user-entered instruction remove instruction mnemo
     * and return array of arguments after the mnemo.
     * @param instruction String representation of one instruction with arguments entered by user
     * @return If there are no arguments after mnemo, returns empty array. Otherwise returns array of strings representing individual arguments.
     */
    private String[] getInstructionArguments(String instruction) {
        instruction = instruction.trim();					// in case there are spaces after the mnemo, dont confuse it as arguments
        int indexOfFirstSpace = instruction.indexOf(" ");
        if (indexOfFirstSpace < 0)
            return new String[0];							// return empty array, there are no arguments
        
        // remove mnemo and get arguments as one string (remove any spaces, they won't be needed)
        String[] argArr = instruction.substring(indexOfFirstSpace+1, instruction.length())
                                     .replaceAll(" ", "")
                                     .split(",");
        return argArr;
    }
    
    /**
     * Extracts positional number from field value. Example:
     *   Field value: "#2"
     *       returns: 2
     *   Field value: "#3.base"
     *       returns: 3
     *
     * @param fieldValue Field value as defined in layout.
     * @return From field value gets the actual position as integer.
     */
    private int extractPositionalNumber(String fieldValue) {
        String seqString = "";
        
        // cut off the positional part, so from "#3.offset" get just "#3"
        if (fieldValue.contains(DATA_CHAR))
            seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.indexOf(DATA_CHAR));
            // only positional value, line.e. "#3"
        else if (fieldValue.contains(POSITIONAL_CHAR))
            seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.length());
        
        return (Integer.parseInt(seqString) - 1); // numbering in instructions.json starts from 1, so subtract one
    }
    
    /**
     * Prepares code for assembling by removing redundant spaces, empty lines and comments.
     * @param code Input code as entered by user.
     * @return Code without redundant spaces and comments.
     */
    private String[] adjustCode(String code) {
        final String singleSpace = " ";
        final String emptyReplacement = "";
        
        // add newline at the end of code, to allow commentary regex matching at the end of code
        code = code + System.lineSeparator();
        
        code = code.replaceAll(COMMENT_CHAR + ".*", emptyReplacement);      // erase comments
        code = code.replaceAll("[ \t]+", singleSpace);                      // and merge multiple tabs and spaces
        code = code.replaceAll("^[ \t]+", emptyReplacement);                // remove empty characters at the beginning of each line
        code = code.replaceAll("^\\s*\\R*$", emptyReplacement);             // remove empty lines
        code = code.replaceAll("\\R+", System.lineSeparator());             // remove empty lines
        code = code.replaceAll("[ \t]*:\\s*", LABEL_TRAILING_CHAR);         // connect label with instruction closest to it (\s  is whitespace character)
        code = code.trim();											              // and finally trim any leading/trailing newlines/spaces in code
        
        log.info("CODE after adjustment:\n{}\n", code);
        return code.split(System.lineSeparator());
    }
    
    /**
     * From a given index computes address. Addressing is defined by
     * maximal bits amount of data.
     * @param index Nth address to compute.
     * @return Integer representation of computed address.
     */
    public static Data computeAddress(int index) {
        Data d = new Data();
        d.setData(( (CPU.getByteSize() * index) << 2) >>> 2);        // todo - 2 is derived from the amount of data bytes... it should be somehow calculated
        return d;
    }
    
    /**
     * From an address compute an index in list/array or other collection. Reverse
     * to the <code>computeAddress</code>. Address is calculated with respect to CPU architecture.
     * @param address Data specifying address to compute index from.
     * @return Index of instruction with specified address.
     */
    public static int computeIndex(Data address) {
        return computeIndex(address.getData());
    }
    
    /**
     * Allows computation of address by a given integer value. Index is calculated with respect to CPU architecture.
     * @param address Address of virtually anything
     * @return Index, that would normally correspond to that address value
     */
    public static int computeIndex(int address) {
        return address / CPU.getByteSize();
    }
    
    public Map<String, Data> getLabels() {
        return labels;
    }
    
    public SyntaxErrorsContainer getSyntaxErrors() {
        return syntaxErrors;
    }
}
