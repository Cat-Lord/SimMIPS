/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;

/**
 * Data storage unit. InputA represents target address (to read from or write to)
 * and inputB represent actual data. Only one of the control signals (memRead or
 * memWrite) can be active at one time, but inactivity of those signals is allowed.
 * Memory is efficiently stored in a map in a address (number) and data (Data) combination.
 * When a request to an unset memory is made (which means memory at address is null),
 * zero is returned. Otherwise data duplicate at that memory address is returned.
 * Memory can be in one of three states: inactive, memory read or memory write.
 * State when memory read signal and memory write signal is active is forbidden.
 * @author catlord
 */
public class DataMemory extends Component {
    private Tuple<String, Data> memRead, memWrite;
    
    public Tuple<String, Data> getMemRead() {
        return memRead;
    }
    
    public void setMemRead(Tuple<String, Data> memRead) {
        this.memRead = memRead;
    }
    
    public Tuple<String, Data> getMemWrite() {
        return memWrite;
    }
    
    public void setMemWrite(Tuple<String, Data> memWrite) {
        this.memWrite = memWrite;
    }
}
