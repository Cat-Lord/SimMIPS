/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import sk.catheaven.essentials.Data;

/**
 *
 * @author catlord
 */
public class Main {
    public static void main(String[] args){
        int testSize = 34;
        Data[] dataTest = new Data[testSize];
        int data = -1;                      // all one's
        
        System.out.println("Set data " + data + "(binary: " + Integer.toBinaryString(data) + ")\n\n");
        for(int i = 0; i < testSize; i++){
            dataTest[i] = new Data(i);          
            dataTest[i].setData(data);
            
            System.out.println("new Data(" + i + ")");
            System.out.println("\t" + "getData(): " + dataTest[i].getData());
            System.out.println("\t" + "toBinaryString(getData()): " + Integer.toBinaryString(dataTest[i].getData()) + "(" + Integer.toBinaryString(dataTest[i].getData()).length() + ")");
            System.out.println("\t" + "toBinaryString(getMask()): " + Integer.toBinaryString(dataTest[i].getMask()));
            System.out.println("\t" + "getBinary(): " + dataTest[i].getBinary());
            System.out.println();
        }
        
        
        Data td = new Data(16);
        for(int i = 5; i > -5; i--){
            System.out.println("Setting " + i + " (" + Integer.toBinaryString(i) + ")");
            
            td.setData(i * 150);
            
            System.out.println("getData(): " + td.getData());
            System.out.println("toBinaryString(getData()): " + td.getData());
            System.out.println("toBinaryString(getMask()): " + Integer.toBinaryString(td.getMask()));
            System.out.println();
        }
    }
}
