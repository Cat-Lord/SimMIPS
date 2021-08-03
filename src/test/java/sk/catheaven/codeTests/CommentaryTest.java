/*
 * Copyright (C) 2020 catlord
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sk.catheaven.codeTests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.main.Loader;
import sk.catheaven.utils.Tuple;

/**
 * This class tests for different comment positions to validate commentary trimming 
 * and code adjustment on compilation.
 * @author catlord
 */
public class CommentaryTest {
	private List<String> codeLines;
	private Assembler assembler;
	
	public CommentaryTest() throws IOException, URISyntaxException {
		codeLines = createCodeLines();
		Loader l = new Loader("sk/catheaven/design/layout.json", "sk/catheaven/design/cpu.json");
		CPU cpu = l.getCPU();
		assembler = cpu.getAssembler();
	}
	
	@Test
	public void test(){
		for(int i = 0; i < 50; i++){
			try{
				assembler.assembleCode(modifyCode());
			}
			catch(SyntaxException se){
				for(Tuple<Integer, String> t : se.getErrors())
					System.out.println("Line " + t.getLeft() + ": " + t.getRight());
				
				fail("Syntax excpetion cought !");
			}
			catch(Exception e){ System.err.println("EXCEPTION: " + e.getMessage()); e.printStackTrace(); fail("Exception cought !");  }
		}
		System.out.println("SUCCESSFULLY RUN COMMENTARY TESTS");
	}

	/** 
	 * Traverse through the sample code and either add a comment line
	 * or append comment at the end of the line.
	*/
	private String modifyCode(){
		boolean modified = false;
		String comment = "".concat(Assembler.COMMENT_CHAR + "This is comment");
		String wholeCode = "";
		
		for(int i = 0; i < codeLines.size(); i++){
			String lineOfCode = codeLines.get(i);
			String modifiedLine = requestChange(lineOfCode, comment);
			
			if( ! modifiedLine.isEmpty()){
				wholeCode = wholeCode.concat(modifiedLine);
				modifiedLine = "";
				modified = true;
			}
			else
				wholeCode = wholeCode.concat(lineOfCode + "\n");
		}
		
		// if there were no modified line, force modification on a random amount of lines (at least 3)
		if( ! modified)
			wholeCode = comment.concat("\n").concat(wholeCode);
		
		System.out.println("CODE AFTER MODIFICATION");
		System.out.println("$" + wholeCode + "$\n--------------------------------------------------------\n");
		
		return wholeCode;
	}
	
	/**
	 * This method directly tries to change the given line of code. Modification is random-based. Chance 
	 * of modification is around 10%.
	 * @param lineOfCode The line of code, that should be altered.
	 * @param comment Comment, that should be added.
	 * @return String, that contains modified line of code, if modification happened. Returns empty string otherwise.
	 */
	private String requestChange(String lineOfCode, String comment){
		String modifiedLine = "";
		
		// append comment at the end of line of code
		if(Math.random() <= 0.1)
			modifiedLine = modifiedLine.concat(lineOfCode).concat(comment).concat("\n");
		// try to add one comment line
		else if(Math.random() <= 0.1){
			modifiedLine = modifiedLine.concat(comment).concat("\n");
			modifiedLine = modifiedLine.concat(lineOfCode).concat("\n");
		}
		// or switch order
		else if(Math.random() <= 0.1){
			modifiedLine = modifiedLine.concat(lineOfCode).concat("\n");
			modifiedLine = modifiedLine.concat(comment).concat("\n");
		}
		
		return modifiedLine;
	}
	
	private List<String> createCodeLines() {
		List<String> code = new ArrayList<>();
		code.add("subi r1, r8, 6");
		code.add("li r6, 6");
		code.add("mul r17, r23,r2");
		code.add("sllv r6, r1, r6");
		code.add("");
		code.add("cat:                 sub r1, r1 ,r30");
		code.add("add r1, r1, r3");
		code.add("li r7, 154");
		code.add("bneq r1, r7, cat");
		code.add("beq r1, r1, cat");
		code.add("beq r0, r0, next");
		code.add("");
		code.add("");
		code.add("");
		code.add("li r1, 951");
		code.add("next: lw r6, 123(r6)");
		code.add("beq r0, r0, super_label_version_million");
		code.add("");
		code.add("super_label_version_million: ");
		code.add("mulu r6, r1, r1");
		code.add("mulu r1, r1, r1");
		code.add("nop");
		code.add("nop");
		code.add("nop");
		code.add("beq r0, r0, ending");
		code.add("nop");
		code.add("nop");
		code.add("nop");
		code.add("nop");
		code.add("ending:add r1, r1, r7");
		code.add("beq r0, r0, cat");
		return code;
	}
}
