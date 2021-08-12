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
 */package sk.catheaven.cpu.codeTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.service.Assembler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests for different comment positions to validate commentary trimming 
 * and code adjustment on compilation.
 * @author catlord
 */
@DisplayName("Assembler should be able to")
public class CommentaryTest extends CPUContainer {
	private static List<String> codeLines;
	
	@BeforeAll
	static void prepareCode() {
		codeLines = createCodeLines();
	}
	
	@Test
	@DisplayName("correctly assemble user code with randomly generated comments")
	public void correctlyAssembleCodeWithRandomlyGeneratedComments() {
		
		for (int i = 0; i < 50; i++) {
			assembler.assembleCode(generateRandomComments());
			assertTrue(assembler.getSyntaxErrors().isEmpty(), assemblerErrorSupplier());
		}
		
	}

	/** 
	 * Traverse through the sample code and either add a comment line
	 * or append comment at the end of the line.
	*/
	private String generateRandomComments(){
		boolean modified = false;
		String comment = "".concat(Assembler.COMMENT_CHAR + "This is comment");
		String wholeCode = "";
		
		for (String lineOfCode : codeLines) {
			String modifiedLine = requestChange(lineOfCode, comment);
			
			if (!modifiedLine.isEmpty()) {
				wholeCode = wholeCode.concat(modifiedLine);
				modified = true;
			} else
				wholeCode = wholeCode.concat(lineOfCode + "\n");
		}
		
		// if there were no modified line, force modification on a random amount of lines (at least 3)
		if( ! modified)
			wholeCode = comment.concat("\n").concat(wholeCode);
		
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
	
	private static List<String> createCodeLines() {
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
