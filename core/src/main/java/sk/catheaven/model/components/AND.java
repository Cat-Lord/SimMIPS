/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;

/**
 * Basic AND logical operation component.
 * @author catlord
 */
public class AND extends Component {
    
    public void execute() {
        Data data = new Data();
        for (String inputLabel: inputs.keySet()) {
            data.setData(
                    data.getData() & inputs.get(inputLabel).getData()
            );
        }
    
        for (String outputLabel: outputs.keySet())
            outputs.get(outputLabel).setData(data.getData());
    }
    
}
