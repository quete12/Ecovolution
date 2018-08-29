/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class BigDoubleTest {

    public BigDoubleTest()
    {
    }

    @Test
    public void testMul(){
        BigDouble value = new BigDouble(2.25);
        System.out.println(value);
        BigDouble factor = new BigDouble(3.69);
        System.out.println(factor);
        value.mul(factor);
        System.out.println(value);
    }
    @Test
    public void testNegMul(){
        BigDouble value = new BigDouble(-2.25);
        System.out.println(value);
        BigDouble factor = new BigDouble(-3.69);
        System.out.println(factor);
        value.mul(factor);
        System.out.println(value);
    }

}
