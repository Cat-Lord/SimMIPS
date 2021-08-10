package sk.catheaven.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.components.CPU;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataFormatterTest {
    private Data data;
    
    @BeforeAll
    private void preparation() {
        data = new Data();
        data.setBitSize(CPU.getBitSize());
        data.setData(69696969); // set a fixed number
    }
    
    @Test
    void getBinary() {
        System.out.println(DataFormatter.getBinary(data));
        assertEquals("0000 0100 0010 0111 0111 1101 1100 1001",
                DataFormatter.getBinary(data));
    }
    
    @Test
    void getLimitedBinary() {
        System.out.println(DataFormatter.getBinary(data, 10));
        assertEquals("01 1100 1001",
                DataFormatter.getBinary(data, 10));
    }
    
    @Test
    void getHex() {
        System.out.println(DataFormatter.getHex(data));
        assertEquals("04277DC9", DataFormatter.getHex(data));
    }
    
    @Test
    void getOct() {
        System.out.println(DataFormatter.getOct(data));
        assertEquals("0411676711", DataFormatter.getOct(data));
    }
}