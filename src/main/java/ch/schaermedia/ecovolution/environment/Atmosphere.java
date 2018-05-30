/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.List;

/**
 * An AtmosphericObject that represents the air and airmovement above ground.
 * @author Quentin
 */
public class Atmosphere extends AtmosphericObject{

    public void flow(Element element, double amount) {
        if (amount == 0) {
            return;
        }
        if (amount > 0) {
            add(element, amount);
        } else {
            //ERROR
        }
    }

    public void calculate(List<Atmosphere> neighbours) {
        int divisor = neighbours.size() + 1;
        composition.entrySet().forEach((entry) -> {
            Element key = entry.getKey();
            Double value = entry.getValue();
            if (value <= 0) {
                return;
            }
            if (outBuffer.containsKey(key)) {
                value -= outBuffer.get(key);
                if (value > 0) {
                    outBuffer.replace(key, 0d);
                }else{
                    return;
                }
            }
            double flow = value / divisor;
            neighbours.forEach((neighbour) -> {
                neighbour.flow(key, flow);
            });
            double totalFlow = flow * neighbours.size();
            remove(key, totalFlow);
        });
    }
}
