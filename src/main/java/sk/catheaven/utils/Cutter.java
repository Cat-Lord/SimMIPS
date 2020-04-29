/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import sk.catheaven.instructionEssentials.Data;

/**
* Cutter serves to nicely contain all the necessary 
* functionality to get the output from a value. Output can be <i>constant</i>,
* which means it will behave exactly like a <code>Data</code> variable.
* it can also be <i>ranged</i>, where it will know the range of data it 
* will "cut" from. For example range 6-12 means, it will bit-wise shift 
* left for 6 bits and right for 12, thus getting only part of original input.
*  
*/
public class Cutter {
   private final Data data;
   private Tuple<Integer, Integer> range;

   /**
	* @param originalBitSize The original bitSize value of this class ties to. Serves to determine bit size of final output. 
	* @param spec Specification of output value. Can be constant integer value or range.
	* @throws java.lang.Exception If the constructor-provided data contain errors.
	*/
   public Cutter(int originalBitSize, String spec) throws Exception, NumberFormatException {
	   range = null;

	   if(spec.contains("-"))
		   this.data = parseRangedValue(originalBitSize, spec);
	   else
		   this.data = new Data(Integer.parseInt(spec));
   }

   /**
	* Creates Data object which is known to be ranged. This object will be used to manipulate with 
	* data given to <b>this</b> object.
	* @param originalBitSize To be able to know, when using range, compute the size needed for the 
	* store the data to. We avoid overflow (or possibly underflow) this way.
	* @param spec Specification of data modification. Can be a number or a range, set by "LS-RS", 
	* where LS means left-shift and RS right-shift (bit-wise).
	* @return Container to store data to. Will be used in this class to cut and temporarily store data.
	* @throws NumberFormatException 
	* @throws Exception Basic error checking of input value is done. Errors result in Exception.
	*/
   private Data parseRangedValue(int originalBitSize, String spec) throws NumberFormatException, Exception {
	   if( ! spec.contains("-")) 
		   throw new Exception("Cutter: Expecting ranged value, but no range was specified --> `" + spec + "`");
	   
	   int from = Integer.parseInt(spec.substring(0, spec.indexOf("-")));
	   int to = Integer.parseInt(spec.substring(spec.indexOf("-") + 1, spec.length()));
	   range = new Tuple<>(from, to);
	   return new Data(originalBitSize - to);		// original size - shift to right gives the actual data size
   }

   /**
	* If the range was set when creating this object, it will cut the provided data accordingly.
	* If no range was set, if will possibly shrink (or don't change at all) provided data.
	* @param origin Data to cut/shrink, or possibly only copy. The result is stored locally.
	*/
   public void setDataToCut(Data origin){
	   if(range == null)
		   data.setData(origin.getData());
	   else{
		   data.setData(
			   (origin.getData() << range.getLeft()) >>> range.getRight()
		   );
	   }
   }

   /**
	* Used to reset data or test, direct data setting.
	* @param data 
	*/
   public void setDataToCut(int data){
	   this.data.setData(data);
   }

   /**
	* Request of previously modified data
	* @return 
	*/
   public Data getCutData(){
	   return data.duplicate();
   }
}
