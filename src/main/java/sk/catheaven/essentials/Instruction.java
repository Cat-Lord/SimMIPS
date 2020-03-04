/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.essentials;

import java.util.List;

/**
 *
 * @author catlord
 */
public class Instruction {
                // exapmles:  1654, r2, loop, loop(r3)
    public enum ArgumentType { INT, REG, OFF, OFFREG };
    
    private Data data;                         // representation in machine code
    private String mnemo;
    private List<ArgumentType> arguments;
    private String details;
    
    
    
}
