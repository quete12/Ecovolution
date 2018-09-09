/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class LinearFunctionTest {

    public LinearFunctionTest()
    {
    }

    /**
     * Test of y method, of class LinearFunction.
     */
    @Test
    public void testY()
    {
        System.out.println("y");
        BigDouble x = new BigDouble(536, 0);
        //f(x) = 2.36*x+574
        LinearFunction instance = new LinearFunction(new BigDouble(2.36), new BigDouble(574, 0));
        BigDouble expResult = new BigDouble(1838.96);
        BigDouble result = instance.y(x);
        assertEquals(expResult, result);
    }

    /**
     * Test of x method, of class LinearFunction.
     */
    @Test
    public void testX()
    {
        System.out.println("x");
        BigDouble y = new BigDouble(1838.96);
        LinearFunction instance = new LinearFunction(new BigDouble(2.36), new BigDouble(574, 0));
        BigDouble expResult = new BigDouble(536, 0);
        BigDouble result = instance.x(y);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorNoLimit(){
        System.out.println("Constructor no limit");
        BigDouble x1 = new BigDouble(15.3792);
        BigDouble y1 = new BigDouble(1783.8596);
        BigDouble x2 = new BigDouble(23.7851);
        BigDouble y2 = new BigDouble(2853.9421);

        LinearFunction instance = new LinearFunction(x1, y1, x2, y2);
        System.out.println(instance);
        assertEquals(x1.toDouble(), instance.x(y1).toDouble(),0.000000005);
        assertEquals(x2.toDouble(), instance.x(y2).toDouble(),0.000000005);
        assertEquals(y1.toDouble(), instance.y(x1).toDouble(),0.000000005);
        assertEquals(y2.toDouble(), instance.y(x2).toDouble(),0.000000005);
    }
    @Test
    public void testConstructorLimited(){
        System.out.println("Constructor no limit");
        BigDouble x1 = new BigDouble(15.3792);
        BigDouble y1 = new BigDouble(1783.8596);
        BigDouble x2 = new BigDouble(23.7851);
        BigDouble y2 = new BigDouble(2853.9421);

        LinearFunction instance = new LinearFunction(x1, y1, x2, y2,true);
        System.out.println(instance);
        assertEquals(x1.toDouble(), instance.x(y1).toDouble(),0.000000005);
        assertEquals(x2.toDouble(), instance.x(y2).toDouble(),0.000000005);
        assertEquals(y1.toDouble(), instance.y(x1).toDouble(),0.000000005);
        assertEquals(y2.toDouble(), instance.y(x2).toDouble(),0.000000005);
    }

}
