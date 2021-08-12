package sk.catheaven.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author catlord
 */
@DisplayName("Data should be able to")
public class DataRangeTest {
	private static Data data;
	@BeforeAll
	static void prepareTestingData() {
		data = new Data();
	}
	
	@Test
	@DisplayName("extract data from input with shifting by amount specified in range")
	public void testShortRange(){
		data.setData(69);
		
		Tuple<String, String> shortRange = new Tuple<>("21", "25");
		Data shortRangeCutter = new Data(data.getBitSize());
		shortRangeCutter.setRange(shortRange);
		assertEquals(data.getBitSize(), shortRangeCutter.getBitSize());
		
		data.setData(11121745);
		shortRangeCutter.setData(data);
		assertEquals(69, shortRangeCutter.getData());
		
		data.setData(176836986);
		Tuple<String, String> wideRange = new Tuple<>("8", "25");
		Data wideRangeCutter = new Data(data.getBitSize());
		wideRangeCutter.setRange(wideRange);
		assertEquals(data.getBitSize(), wideRangeCutter.getBitSize());
		
		wideRangeCutter.setData(data);
		assertEquals(69, wideRangeCutter.getData());
	}
}
