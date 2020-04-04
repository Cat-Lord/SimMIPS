/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author catlord
 */
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
		Loader loader = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
		
    }
}
