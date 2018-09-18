/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics;

import ch.schaermedia.ecovolution.math.BigDouble;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class ChemUtilitiesTest {

    public ChemUtilitiesTest()
    {
    }

    @Test
    public void CelsiusToKelvinPositiveValue()
    {
        BigDouble instance = new BigDouble(15.5);
        BigDouble expected = new BigDouble(288.65);
        BigDouble result = ChemUtilities.toKelvin(instance);

        assertEquals(expected, result);
    }

    @Test
    public void CelsiusToKelvinNegativeValue()
    {
        BigDouble instance = new BigDouble(-80.0);
        BigDouble expected = new BigDouble(193.15);
        BigDouble result = ChemUtilities.toKelvin(instance);

        assertEquals(expected, result);
    }

    @Test
    public void KelvinToCelsiusPositiveResult()
    {
        BigDouble instance = new BigDouble(288.65);
        BigDouble expected = new BigDouble(15.5);
        BigDouble result = ChemUtilities.toCelsius(instance);

        assertEquals(expected, result);
    }

    @Test
    public void KelvinToCelsiusNegativeResult()
    {
        BigDouble instance = new BigDouble(193.15);
        BigDouble expected = new BigDouble(-80.0);
        BigDouble result = ChemUtilities.toCelsius(instance);

        assertEquals(expected, result);
    }

}
