/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class QuadraticFunctionTest {

    private QuadraticFunction standradPosFunction;
    private QuadraticFunction standradNegFunction;

    public QuadraticFunctionTest()
    {
        standradPosFunction = new QuadraticFunction(-1, 1, 0, 0, 1, 1);
        standradNegFunction = new QuadraticFunction(-1, -1, 0, 0, 1, -1);
    }

    /**
     * Test of y method, of class QuadraticFunction.
     */
    @Test
    public void testPosY1()
    {
        double x = -1.0;
        double expResult = 1.0;
        double result = standradPosFunction.y(x);
        System.out.println("f("+x+") = " + result);
        assertEquals(expResult, result, 0.0);
    }
    @Test
    public void testPosY2()
    {
        double x = 2.0;
        double expResult = 4.0;
        double result = standradPosFunction.y(x);
        System.out.println("f("+x+") = " + result);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of x method, of class QuadraticFunction.
     */
    @Test
    public void testPosX()
    {
        double y = 4.0;
        double expResult = 2.0;
        double result = standradPosFunction.x(y);
        System.out.println("x = " + result);
        assertEquals(expResult, result, 0.0);
    }

}
