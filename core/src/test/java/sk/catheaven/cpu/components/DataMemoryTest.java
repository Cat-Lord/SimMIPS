package sk.catheaven.cpu.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.cpu.components.DataMemory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
@DisplayName("Data memory should")
public class DataMemoryTest extends CPUContainer {
	private static DataMemory memory;
	
	private static final int NOT_IMPORTANT = 69;
	private static final String dataToStoreLabel = "reg2Value";
	private static final String addressLabel = "aluResult";				// result from alu is a calculated address
	private static final String readSignalLabel = "memReadSignal";
	private static final String writeSignalLabel = "memWriteSignal";
	private static Data dataToStore, address, readSignal, writeSignal;
	
	@BeforeAll
	static void prepareComponent() {
		memory = (DataMemory) cpu.getComponents().get("Data Memory");
		dataToStore = new Data();
		address = new Data();
		readSignal = new Data(1);
		writeSignal = new Data(1);
	}
	
	@BeforeEach
	public void clear() {
		memory.clearMemory();
	}
	
	@Test
	@DisplayName("return 0 when there is not an active signal")
	public void memoryInactive() {
		address.setData(123321);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(4125);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(0);
		setInputsAndExecute();
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(236248);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(2);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
	}

	@Test
	@DisplayName("correctly store data when write signal is active")
	public void memoryWrite() {
		// WRITE SIGNAL ACTIVE
		address.setData(0);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(0);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(123, memory.getOutput(Component.IGNORED_LABEL).getData());

		address.setData(234237);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(4326);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(342);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	@DisplayName("correctly return empty data from unset memory cells")
	public void memoryRead() {
		address.setData(234237);
		dataToStore.setData(0);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(234237);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(4326);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(342);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(234237);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(4326);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(342);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	@DisplayName("correctly write and read value (common operation sequence)")
	public void commonOperationTest(){
		// read-write sequence
		address.setData(0);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());

		address.setData(0);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(123, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		// read-write sequence
		address.setData(234237);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(234237);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(123, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		// read-write sequence
		address.setData(4326);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(4326);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(123, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		// read-write sequence
		address.setData(342);
		dataToStore.setData(123);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(342);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(123, memory.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	@DisplayName("set multiple values one after another and later retrieve it correctly")
	public void firstSetLaterGet() {
		
		// WRITE VALUES INTO MULTIPLE MEMORY BLOCKS
		address.setData(6969);
		dataToStore.setData(69);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(97531);
		dataToStore.setData(9155);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(124578);
		dataToStore.setData(966311);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(66666);
		dataToStore.setData(666);
		readSignal.setData(0);
		writeSignal.setData(1);
		setInputsAndExecute();
		assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		
		// READ STORED VALUES
		address.setData(6969);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(69, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(97531);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(9155, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(66666);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(666, memory.getOutput(Component.IGNORED_LABEL).getData());
		
		address.setData(124578);
		dataToStore.setData(NOT_IMPORTANT);
		readSignal.setData(1);
		writeSignal.setData(0);
		setInputsAndExecute();
		assertEquals(966311, memory.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	@DisplayName("be able to byte-align correctly a number to store")
	public void testNumberStoringAtDifferentByteAlignments() {
		int numberToStore = 0xf1f2f3f4;		// 32-bit number
		int addressValue = 0x5000000;

		// shift the address by 0, 1, 2, ..., 4 bytes
		for (int byteShift = 0; byteShift < CPU.getByteSize(); byteShift++, memory.clearMemory()) {
			address.setData(addressValue + byteShift);
			dataToStore.setData(numberToStore);
			readSignal.setData(0);
			writeSignal.setData(1);
			setInputsAndExecute();
			assertEquals(0, memory.getOutput(Component.IGNORED_LABEL).getData());
			
			address.setData(addressValue + byteShift);
			dataToStore.setData(numberToStore);
			readSignal.setData(1);
			writeSignal.setData(0);
			setInputsAndExecute();
			assertEquals(numberToStore, memory.getOutput(Component.IGNORED_LABEL).getData());
		}
	}
	
	private int getShiftInBits(int bytes) {
		return Byte.SIZE * bytes;
	}
	
	// private method to easily set all memory data. Immediately calls "execute()"
	private void setInputsAndExecute(){
		assertTrue(memory.setInput(dataToStoreLabel,	dataToStore));
		assertTrue(memory.setInput(addressLabel,		address));
		assertTrue(memory.setInput(readSignalLabel,	readSignal));
		assertTrue(memory.setInput(writeSignalLabel,	writeSignal));
		
		memory.execute();
	}
	
	/*
	  Code when testing in gui
		li r1, 69
		li r2, 99
		li r3, 123
		li r4, 6969
		li r5, 1000
		li r6, 9
		li r7, 827
		li r8, 888
		li r9, 666
		li r10, 1024

		sw r1, 50(r0)
		sw r2, 9182(r0)
		sw r3, 6666(r0)
		sw r3, 4321(r0)
		sw r4, 96(r0)
		sw r5, 800(r0)
		sw r6, 70(r0)
		sw r7, 0(r0)
		sw r8, 4(r0)
		sw r9, 16(r0)
		sw r10, 24(r0)
	 */
}
