/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

/**
 * Reg bank, which provides temporary storage for CPU calculations. Has multiple inputs and 
 * two outputs, as well as regWrite signal.
 * @author catlord
 */
public class RegBank extends Component {
    private int dataBitSize;        // bit size of data stored in registers
    private int regCount;           // number of registers
    
    public int getDataBitSize() {
        return dataBitSize;
    }
    
    public void setDataBitSize(int dataBitSize) {
        this.dataBitSize = dataBitSize;
    }
    
    public int getRegCount() {
        return regCount;
    }
    
    public void setRegCount(int regCount) {
        this.regCount = regCount;
    }
}
