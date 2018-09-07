/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

import ch.schaermedia.ecovolution.math.BigDouble;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class BigDoubleTest {

    public BigDoubleTest()
    {
    }

    /**
     * Test of compareTo method, of class BigDouble.
     */
    @Test
    public void testCompareTo()
    {
        System.out.println("compareTo");
        BigDouble o = new BigDouble(2.5);
        BigDouble instance = new BigDouble(-2.3);
        int result = instance.compareTo(o);
        assertEquals(-1, result);
        result = o.compareTo(instance);
        assertEquals(1, result);
        BigDouble o2 = new BigDouble(2.5);
        result = o2.compareTo(o);
        assertEquals(0, result);
    }

    /**
     * Test of toDoubleString method, of class BigDouble.
     */
    @Test
    public void testToDoubleString()
    {
        System.out.println("toDoubleString");
        BigDouble instance = new BigDouble(2.5);
        String expResult = "2.500000000";
        String result = instance.toDoubleString();
        assertEquals(expResult, result);
    }

    /**
     * Test of toDoubleString method, of class BigDouble.
     */
    @Test
    public void testToDoubleString_NegativeNumber()
    {
        System.out.println("toDoubleString");
        BigDouble instance = new BigDouble(-2.5);
        String expResult = "-2.500000000";
        String result = instance.toDoubleString();
        assertEquals(expResult, result);
    }

    /**
     * Test of toDouble method, of class BigDouble.
     */
    @Test
    public void testToDouble()
    {
        System.out.println("toDouble");
        BigDouble instance = new BigDouble(2.5);
        double expResult = 2.5;
        double result = instance.toDouble();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of add method, of class BigDouble.
     */
    @Test
    public void testAdd_BigDouble()
    {
        System.out.println("add");
        BigDouble instance = new BigDouble(1.5);
        BigDouble other = new BigDouble(2.51);
        BigDouble expResult = new BigDouble(4.01);
        BigDouble result = instance.add(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class BigDouble.
     */
    @Test
    public void testAdd_BigDouble_2NegativeNumbers()
    {
        System.out.println("add");
        BigDouble instance = new BigDouble(-1.5);
        BigDouble other = new BigDouble(-2.51);
        BigDouble expResult = new BigDouble(-4.01);
        BigDouble result = instance.add(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class BigDouble.
     */
    @Test
    public void testAdd_BigDouble_NegativeResult()
    {
        System.out.println("add");
        BigDouble instance = new BigDouble(1.5);
        BigDouble other = new BigDouble(-5.51);
        BigDouble expResult = new BigDouble(-4.01);
        BigDouble result = instance.add(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class BigDouble.
     */
    @Test
    public void testAdd_BigDouble_PositiveResult()
    {
        System.out.println("add");
        BigDouble instance = new BigDouble(-1.5);
        BigDouble other = new BigDouble(5.51);
        BigDouble expResult = new BigDouble(4.01);
        BigDouble result = instance.add(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class BigDouble.
     */
    @Test
    public void testSub_BigDouble()
    {
        System.out.println("sub");
        BigDouble instance = new BigDouble(5.51);
        BigDouble other = new BigDouble(1.5);
        BigDouble expResult = new BigDouble(4.01);
        BigDouble result = instance.sub(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class BigDouble.
     */
    @Test
    public void testSub_BigDouble_2NegativeNumbers()
    {
        System.out.println("sub");
        BigDouble instance = new BigDouble(-5.51);
        BigDouble other = new BigDouble(-1.5);
        BigDouble expResult = new BigDouble(-4.01);
        BigDouble result = instance.sub(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class BigDouble.
     */
    @Test
    public void testSub_BigDouble_NegativeResult()
    {
        System.out.println("sub");
        BigDouble instance = new BigDouble(-5.51);
        BigDouble other = new BigDouble(1.5);
        BigDouble expResult = new BigDouble(-7.01);
        BigDouble result = instance.sub(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class BigDouble.
     */
    @Test
    public void testSub_BigDouble_PositiveResult()
    {
        System.out.println("sub");
        BigDouble instance = new BigDouble(5.51);
        BigDouble other = new BigDouble(-1.5);
        BigDouble expResult = new BigDouble(7.01);
        BigDouble result = instance.sub(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of mul method, of class BigDouble.
     */
    @Test
    public void testMul_BigDouble()
    {
        System.out.println("mul");
        BigDouble other = new BigDouble(2.5);
        BigDouble instance = new BigDouble(3.6);
        BigDouble expResult = new BigDouble(9.0);
        BigDouble result = instance.mul(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of mul method, of class BigDouble.
     */
    @Test
    public void testMul_BigDouble_2NegativeNumbers()
    {
        System.out.println("mul");
        BigDouble other = new BigDouble(-2.5);
        BigDouble instance = new BigDouble(-3.6);
        BigDouble expResult = new BigDouble(9.0);
        BigDouble result = instance.mul(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of mul method, of class BigDouble.
     */
    @Test
    public void testMul_BigDouble_1NegativeNumber()
    {
        System.out.println("mul");
        BigDouble other = new BigDouble(2.5);
        BigDouble instance = new BigDouble(-3.6);
        BigDouble expResult = new BigDouble(-9.0);
        BigDouble result = instance.mul(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of div method, of class BigDouble.
     */
    @Test
    public void testDiv_BigDouble()
    {
        System.out.println("div");
        BigDouble instance = new BigDouble(3.6);
        BigDouble other = new BigDouble(2.5);
        BigDouble expResult = new BigDouble(1, 440000000);
        BigDouble result = instance.div(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of div method, of class BigDouble.
     */
    @Test
    public void testDiv_BigDouble_2NegaitveNumbers()
    {
        System.out.println("div");
        BigDouble instance = new BigDouble(-3.6);
        BigDouble other = new BigDouble(-2.5);
        BigDouble expResult = new BigDouble(1, 440000000);
        BigDouble result = instance.div(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of div method, of class BigDouble.
     */
    @Test
    public void testDiv_BigDouble_1NegativeNumber()
    {
        System.out.println("div");
        BigDouble instance = new BigDouble(-3.6);
        BigDouble other = new BigDouble(2.5);
        BigDouble expResult = new BigDouble(-1, 440000000);
        BigDouble result = instance.div(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of isPositive method, of class BigDouble.
     */
    @Test
    public void testIsPositive()
    {
        System.out.println("isPositive");
        BigDouble instance = new BigDouble(3.69);
        boolean expResult = true;
        boolean result = instance.isPositive();
        assertEquals(expResult, result);
    }

    /**
     * Test of isPositive method, of class BigDouble.
     */
    @Test
    public void testIsPositive_NegativeNumber()
    {
        System.out.println("isPositive");
        BigDouble instance = new BigDouble(-3.69);
        boolean expResult = false;
        boolean result = instance.isPositive();
        assertEquals(expResult, result);
    }

    /**
     * Test of isNegative method, of class BigDouble.
     */
    @Test
    public void testIsNegative()
    {
        System.out.println("isNegative");
        BigDouble instance = new BigDouble(-3.69);
        boolean expResult = true;
        boolean result = instance.isNegative();
        assertEquals(expResult, result);
    }

    /**
     * Test of isNegative method, of class BigDouble.
     */
    @Test
    public void testIsNegative_PositiveNumber()
    {
        System.out.println("isNegative");
        BigDouble instance = new BigDouble(3.69);
        boolean expResult = false;
        boolean result = instance.isNegative();
        assertEquals(expResult, result);
    }

    /**
     * Test of isZero method, of class BigDouble.
     */
    @Test
    public void testIsZero()
    {
        System.out.println("isZero");
        BigDouble instance = new BigDouble();
        boolean expResult = true;
        boolean result = instance.isZero();
        assertEquals(expResult, result);
    }

    /**
     * Test of isZero method, of class BigDouble.
     */
    @Test
    public void testIsZero_NonZero()
    {
        System.out.println("isZero");
        BigDouble instance = new BigDouble(-1.5);
        boolean expResult = false;
        boolean result = instance.isZero();
        assertEquals(expResult, result);
    }

    /**
     * Test of isNotZero method, of class BigDouble.
     */
    @Test
    public void testIsNotZero()
    {
        System.out.println("isNotZero");
        BigDouble instance = new BigDouble(2.4);
        boolean expResult = true;
        boolean result = instance.isNotZero();
        assertEquals(expResult, result);
    }

    /**
     * Test of isNotZero method, of class BigDouble.
     */
    @Test
    public void testIsNotZero_Zero()
    {
        System.out.println("isNotZero");
        BigDouble instance = new BigDouble();
        boolean expResult = false;
        boolean result = instance.isNotZero();
        assertEquals(expResult, result);
    }

    /**
     * Test of clear method, of class BigDouble.
     */
    @Test
    public void testClear()
    {
        System.out.println("clear");
        BigDouble instance = new BigDouble(5.36);
        instance.clear();
        assertEquals(0.0, instance.toDouble(), 0.0);
    }

    /**
     * Test of max method, of class BigDouble.
     */
    @Test
    public void testMax()
    {
        System.out.println("max");
        BigDouble a = new BigDouble(-2.3);
        BigDouble b = new BigDouble(-5.6);
        BigDouble expResult = a;
        BigDouble result = BigDouble.max(a, b);
        assertEquals(expResult, result);
    }

    /**
     * Test of min method, of class BigDouble.
     */
    @Test
    public void testMin()
    {
        System.out.println("min");
        BigDouble a = new BigDouble(-2.3);
        BigDouble b = new BigDouble(-5.6);
        BigDouble expResult = b;
        BigDouble result = BigDouble.min(a, b);
        assertEquals(expResult, result);
    }

    @Test
    public void testToDoubleString_SmallFractions()
    {
        System.out.println("toDoubleString");
        BigDouble instance = new BigDouble(0, 1);
        String expResult = "0.000000001";
        String result = instance.toDoubleString();
        assertEquals(expResult, result);
    }

}
