/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.argumentTypes.DataArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.LabelArgumentType;

/**
 * Assembles user code, which is not formatted. Removes comments and trims arguments as well as 
 * instruction mnemos. Assembler needs to know the instruction set to be able to correctly
 * parse instructions provided in user code.
 * Also knows how to calculate address from index in array, list or any other similar collection
 * or an index from a given address.
 * @author catlord
 */
public class Assembler {
	public final static String DATA_CHAR = ".";				// if present, the string containing this symbol is a data reference (.base or .offset)
	public final static char COMMENT_CHAR = ';';				// comment in user code
	public final static String POSITIONAL_CHAR = "#";			// denotes position (can be found in field values of instruction).
														// Is of String type just to allow usage of contains(POSITIONAL_CHAR)
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final Map<String, Instruction> instructionSet;
	
	// utility declarations
	private final DataArgumentType dat;
	private final LabelArgumentType lat;
	
	private Map<String, Data> labelsToRemove;			// TODO: remove this list of labels (only for debugging purposes)
	
	public Assembler(Map<String, Instruction> instructionSet){
		lat = new LabelArgumentType();			// if we find a label, we need to test its format, if it's correctly written
		dat = new DataArgumentType();			// when extracting data values from arguments from user, we have to use object, that knows how to do that
		this.instructionSet = instructionSet;
	}
	
	/**
	 * Tries to find instructions and assigns every instruction an address if that instruction is valid.
	 * @param code Complete code obtained from user.
	 * @return List of assembled instructions.
	 * @throws SyntaxException
	 */
	public List<AssembledInstruction> assembleCode(String code) throws SyntaxException {
		String codeLines[] = adjustCode(code);					// remove redundancies
		
		Map<String, Data> labels = getLabels(codeLines);						// get all labels with their respective addresses
		
		String debugString = "";
		for(String key: labels.keySet())
			debugString += "Label `" + key + "` | Address: " + labels.get(key).getHex() + "\n";
		
		logger.log(Level.INFO, debugString);
		
		this.labelsToRemove = labels;		// TODO: remove
		
		SyntaxException errors = new SyntaxException();
		
		List<AssembledInstruction> assembled = new ArrayList<>();
		int addressIndex = 0;		// since we are skipping line, we need to keep track of current line
		for(int lineNO = 0; lineNO < codeLines.length; lineNO++){
			if(! codeLines[lineNO].isEmpty()){
				try {
					assembled.add(assembleInstruction(addressIndex, codeLines[lineNO], computeAddress(addressIndex), labels));
					addressIndex++;
				} catch(SyntaxException e){ 
					errors.addError(lineNO + 1, e.getMessage());
				} 
			}
		}
		
		if( ! errors.getErrors().isEmpty())
			throw errors;
		
		return assembled;
	}
	
	/**
	 * Assembles the instruction to machine code.
	 * @param lineIndex The index (number) of line this instruction is from.
	 * @param instruction Instruction to parse (as entered by user).
	 * @param address Instruction of that address calculated beforehand.
	 * @param labels List of labels and their respective instruction addresses. 
	 * @return Assembled instruction in the form AssembledInstruction class.
	 * @throws SyntaxException In case of user error.
	 */
	public AssembledInstruction assembleInstruction(int lineIndex, String instruction, Data address, Map<String, Data> labels) throws SyntaxException {
		logger.log(Level.INFO, "Assembling `{0}`", instruction);
		
		String args[] = getInstructionArguments(instruction);
		
		// if there are arguments, there is a space after mnemo, otherwise there is only mnemo (which is acceptable)
		String mnemo = instruction;
		if(args.length > 0) mnemo = instruction.substring(0, instruction.indexOf(" "));
		mnemo = mnemo.toLowerCase().trim();
		
		checkInstruction(mnemo, args, labels);
		Data iCode = createICode(mnemo, args, address, labels);
		logger.log(Level.INFO, "{0}-- Icode created: {1}", new Object[]{String.format("%4s", mnemo), iCode.getBinary()});
		
		return new AssembledInstruction(lineIndex, this.instructionSet.get(mnemo), instruction, iCode, address);
	}
	
	/**
	 * Only checks instruction by the Instruction class, which dictates its format.
	 * @param instructionMnemo Instruction mnemo.
	 * @param args Array of arguments provided for the instruction.
	 * @throws SyntaxException
	 */
	private void checkInstruction(String instructionMnemo, String args[], Map<String, Data> labels) throws SyntaxException {
		Instruction instruction = instructionSet.get(instructionMnemo);
		
		if(instruction == null) throw new SyntaxException("Unknown instruction `" + instructionMnemo + "` !");
		
		if(args.length < instruction.getAllArguments().size()) 
			throw new SyntaxException("Not enough arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		else if(args.length > instruction.getAllArguments().size()) 
			throw new SyntaxException("Too many arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		
		// check each argument against arguments in instruction (if are correcty formatted)
		for(int i = 0; i < args.length; i++){
			instruction.getArgument(i).parse(args[i]);
			
			// check if the label argument exists in labels
			if(instruction.getArgument(i) instanceof LabelArgumentType)
				if(labels.get(args[i]) == null)
					throw new SyntaxException("Label `" + args[i] + "` branches to an undefined label");
		}
	}

	/**
	 * From the instruction mnemo and provided arguments creates instruction code.
	 * @param instructionMnemo
	 * @param args
	 * @throws SyntaxException 
	 */
	private Data createICode(String instructionMnemo, String args[], Data address, Map<String, Data> labels) throws SyntaxException {
		Data iCode = new Data();
		
		Instruction instruction = instructionSet.get(instructionMnemo);
		List<Field> fields = instruction.getInstructionType().getFields();
			
		int shiftBy = 0;
		for(int i = 0; i < fields.size(); i++){
			String fieldValue = instruction.getFieldValue(fields.get(i).getLabel());
			int tempCode = iCode.getData();
			shiftBy = fields.get(i).getBitSize();		// remember, by ho many bits we need to shift in next iteration			
			tempCode <<= shiftBy;
			
			// there is no positional character
			if( ! fieldValue.contains(POSITIONAL_CHAR)){	
				int value = Integer.parseInt(fieldValue);
				logger.log(Level.INFO, "Int value for `{0}` is {1}", new Object[]{fieldValue, value});
				tempCode |= value;
			}
			else{
				// there is a positional character
				
				// first check, if the field is a data field
				if(fieldValue.contains(DATA_CHAR)) {
					// get the argument type, because it knows how to parse the argument to get specific parts of that argument
					// For example: Ask the data argument to get ".base" and it knows, how to get it.					
					int value = dat.getPart(args[extractPositionalNumber(fieldValue)], fieldValue);
					logger.log(Level.INFO, "Data value for `{0}` is {1}", new Object[]{fieldValue, value});
					
					tempCode |= value;
				}
				else {
					int seq = extractPositionalNumber(fieldValue);
					
					// if this arguments is a label argument, calclulate offset (this address - target address)
					if(seq < instruction.getAllArguments().size()  &&  instruction.getArgument(seq) instanceof LabelArgumentType){
						Data offset = new Data(shiftBy);		// we calculate offset that should be only this size wide
						offset.setData(labels.get(args[seq]).getData() - address.getData() - Data.MAX_BIT_SIZE/8);
						tempCode |= offset.getData();
					}					
					else {
						try{
							// get the data from argument according to its type
							int value = instruction.getArgument(seq).getData(args[seq]);
							logger.log(Level.INFO, "Positional value for `{0}` is {1}", new Object[]{fieldValue, value});
							tempCode |= value;
						} catch(IndexOutOfBoundsException e){
							logger.log(Level.WARNING, "Argument data car: Index out of bounds ! Index: {0}, number of args: {1}", new Object[]{seq, args.length}); 
						}
					}
				}
			}
			iCode.setData(tempCode);
		}
		
		return iCode;
	}
	
	/**
	 * When there is a column symbol (:) in code line, we remember address of
	 * instruction, that follows the label. Label format is checked, so this
	 * method can result in an exception.
	 * @param codeLines Instructions array with possible syntactical errors.
	 * @return Map of label names tied to address represented as Data.
	 * @throws SyntaxException 
	 */
	private Map<String, Data> getLabels(String codeLines[]) throws SyntaxException {
		Map<String, Data> labels = new HashMap<>();
		SyntaxException e = new SyntaxException();
		
		int addressIndex = 0;
		for(int i = 0; i < codeLines.length; i++){
			if(codeLines[i].isEmpty())
				continue;
				
			logger.log(Level.INFO, "{0}: <{1}>", new Object[]{i, codeLines[i]});
			
			// if there is a label, rememeber it along with the address
			// of that instruction to allow assembling of the code.
			if(codeLines[i].contains(":")){
				int coli = codeLines[i].indexOf(":");
				String label = codeLines[i].substring(0, coli);			// extract the label
				
				lat.parse(label);										// check if the label is correctly formated
				
				// throw an exception if there is a label without any instruction following it
				if(coli+1 >= codeLines[i].length()){
					e.addError(i, "Label `" + label + "` doesn't bind to any instruction");
					continue;
				}
				
				// extract the label from the code line
				codeLines[i] = codeLines[i].substring(coli+1);
				
				if(labels.get(label) == null){
					labels.put(label, Assembler.computeAddress(addressIndex));
					logger.log(Level.INFO, "Created label `{0}` (address {1}) from codeline `{2}`", new Object[]{label, Assembler.computeAddress(addressIndex - 1).getHex(), codeLines[i]});
				}
				else{
					e.addError(i, "Duplicate label declaration `" + label + "` !");
					continue;
				}
			}
			
			addressIndex++;		// increase address with each instruction !
		}
		
		// if we found errors, throw exception
		if( ! e.getErrors().isEmpty())
			throw e;
		
		return labels;
	}
	
	/**
	 * Form user-entered instruction remove instruction mnemo
	 * and return array of arguments after the mnemo.
	 * @param instruction String representation of one instruction with arguments entered by user
	 * @return If there are no arguments after mnemo, returns empty array. Otherwise returns array of strings representing individual arguments.
	 */
	private String[] getInstructionArguments(String instruction){
		instruction = instruction.trim();					// in case there are spaces after the mnemo, dont confuse it as arguments
		int indexOfFirstSpace = instruction.indexOf(" ");
		if(indexOfFirstSpace < 0)
			return new String[0];							// return empty array, there are no arguments
		
		// remove mnemo and get arguments as one string (remove any spaces, they won't be needed)
		String argArr[] = instruction.substring(indexOfFirstSpace+1, instruction.length())
									 .replaceAll(" ", "")
									 .split(",");
		return argArr;
	}
	
	/**
	 * Prepares code for assembling by removing redundant spaces, empty lines and comments.
	 * @param code Input code as entered by user.
	 * @return Code without redundant spaces and comments.
	 */
	private String[] adjustCode(String code){
		// add newline at the end of code, to allow commentary regex matching at the end of code
		code = code + "\n";
		
		code = code.replaceAll(COMMENT_CHAR + ".*\\R", System.lineSeparator()); // erase comments ('\R' matches multiple versions of newline characters)
		code = code.replaceAll("[ \t]+", " ");							// and merge multiple tabs and spaces
		code = code.replaceAll("\n +", System.lineSeparator());					// remove emty characters at the begining of each line
		code = code.replaceAll("[ \t]*:\\s*", ":");					// connect label with instruction closest to it (\s  is whitespace character)
		code = code.trim();											// and finally trim any leading/traling newlines/spaces in code

		logger.log(Level.INFO, "CODE after adjusment:\n{0}\n", code);
		return code.split(System.lineSeparator());
	}
	
	/**
	 * From a given index computes address. Addressing is defined by 
	 * maximal bits amount of data.
	 * @param index Nth address to compute.
	 * @return Integer representation of computed address.
	 */
	public static Data computeAddress(int index){
		Data d = new Data();
		d.setData((( (Data.MAX_BIT_SIZE/8) * index) << 2) >>> 2);
		return d;
	}
	
	/**
	 * From an address compute an index in list/array or other collection. Reverse
	 * to the <code>computeAddress</code>.
	 * @param address Data specifying address to compute index from.
	 * @return Index of instruction with specified address.
	 */
	public static int computeIndex(Data address){
		return address.getData() / (Data.MAX_BIT_SIZE/8);
	}
	
	/**
	 * Allows computation of address by a given integer value.
	 * @param address
	 * @return 
	 */
	public static int computeIndex(int address){
		return address / (Data.MAX_BIT_SIZE/8);
	}
	
	/**
	 * Extracts positional number from field value.
	 * @param fieldValue Field value as defined in layout, for example "#2" or "#3.base".
	 * @return 
	 */
	private int extractPositionalNumber(String fieldValue){
		String seqString = "";
		
		// cut off the positional part, so from "#3.offset" get just "#3"
		if(fieldValue.contains(DATA_CHAR))
			seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.indexOf(DATA_CHAR)); 
		// only positional value, line.e. "#3"
		else if(fieldValue.contains(POSITIONAL_CHAR))
			seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.length());

		return (Integer.parseInt(seqString) - 1); // numbering in layout.json starts from 1, so substract one
	}
	
	// TODO: REMOVE THIS METHOD -- USED ONLY FOR DEBUGGING
	public Map<String, Data> getLabels(){
		return this.labelsToRemove;
	}
	
}
