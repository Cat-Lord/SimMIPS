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
	*/
   public Cutter(int originalBitSize, String spec) throws NumberFormatException {
	   range = null;

	   if(spec.contains("-"))
		   this.data = parseRangedValue(originalBitSize, spec);
	   else
		   this.data = new Data(Integer.parseInt(spec));
   }

   private Data parseRangedValue(int originalBitSize, String spec) throws NumberFormatException {
	   int from = Integer.parseInt(spec.substring(0, spec.indexOf("-")));
	   int to = Integer.parseInt(spec.substring(spec.indexOf("-") + 1, spec.length()));
	   range = new Tuple<>(from, to);
	   return new Data(originalBitSize - to);		// original size - shift to right gives the actual data size
   }

   public void setDataToCut(Data origin){
	   if(range == null)
		   data.setData(origin.getData());
	   else{
		   data.setData(
			   (origin.getData() << range.getLeft()) >>> range.getRight()
		   );
	   }
   }

   public void setDataToCut(int data){
	   this.data.setData(data);
   }

   public Data getCutData(){
	   return data;
   }
}
