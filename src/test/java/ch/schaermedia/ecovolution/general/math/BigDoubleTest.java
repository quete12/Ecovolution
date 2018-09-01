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
        System.out.println("Start Multiplication");
        BigDouble value = new BigDouble(2.25);
        System.out.println(value);
        BigDouble factor = new BigDouble(3.69);
        System.out.println(factor);
        value.mul(factor);
        System.out.println(value);
        System.out.println("End Multiplication");
    }
    @Test
    public void testNegMul(){
        System.out.println("Start neg Multiplication");
        BigDouble value = new BigDouble(-2.25);
        System.out.println(value);
        BigDouble factor = new BigDouble(-3.69);
        System.out.println(factor);
        value.mul(factor);
        System.out.println(value);
        System.out.println("End neg Multiplication");
    }

    @Test
    public void testDiv(){
        System.out.println("Test Division:");
        BigDouble dividend = new BigDouble(20000000000.25);
        System.out.println(dividend);
        BigDouble divisor = new BigDouble(30000000.69);
        System.out.println(divisor);
        dividend.div(divisor);
        System.out.println(dividend);
        System.out.println("End Division");
    }

}
