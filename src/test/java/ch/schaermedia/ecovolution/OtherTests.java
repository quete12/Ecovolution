/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class OtherTests {
    @Test
    public void testArcTanCalc(){
        System.out.println("flat First");
        double m1 = 0.5;
        double m2 = 2;

        double toATan = (m1-m2)/(1+m1*m2);
        double result = Math.toDegrees(Math.atan(toATan));
        System.out.println("Result: " + result);
    }
    @Test
    public void testArcTanCalc2(){
        System.out.println("Steep First");
        double m1 = 2;
        double m2 = 0.5;

        double toATan = (m1-m2)/(1+m1*m2);
        double result = Math.toDegrees(Math.atan(toATan));
        System.out.println("Result: " + result);
    }
}
