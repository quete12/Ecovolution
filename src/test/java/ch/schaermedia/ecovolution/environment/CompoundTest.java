/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.Compound;
import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.Phase;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class CompoundTest {

    private final double standardPressure = CompoundMix.STATIC_PRESSURE_kPa;
    private final double standardVolume = CompoundMix.STATIC_VOLUME_L;
    private final double standardTemperature = ChemUtilities.toKelvin(13.5);

    public CompoundTest()
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CompoundTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getTemperature_K method, of class Compound.
     */
    @Test
    public void testGetTemperature_K_Gas()
    {
        System.out.println("getTemperature_K");
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("CO2");
        Compound instance = new Compound(prop);
        double moles = 100;
        instance.setAmount_mol(moles);
        double expResult = standardTemperature;
        double energy = moles * (prop.getSpecificHeatCapacity_kj_mol_K() * expResult + prop.getFusionHeat_kj() + prop.getVaporizationHeat_kj());
        instance.setEnergy_kj(energy);
        instance.updateTemperatureAndPhase(standardPressure);
        double temperatureAt = prop.getEnergy_Pressure_Diagram().temperatureAt(instance.getEnergy_kj() / instance.getAmount_mol(), standardPressure);
        double result = instance.getTemperature_K();
        Phase resultPhase = instance.getPhase();
        System.out.println("Result Check");
        assertEquals(expResult, result, 0.0);
        System.out.println("T instance energy Check");
        assertEquals(expResult, temperatureAt, 0.0);
        System.out.println("Phase Check");
        assertEquals(Phase.GAS, resultPhase);
    }

    @Test
    public void testGetTemperatureWater_K_Gas()
    {
        System.out.println("getTemperature_K");
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("H2O");
        Compound instance = new Compound(prop);
        double moles = 100;
        instance.setAmount_mol(moles);
        double expResult = standardTemperature;
        double energy = moles * (prop.getSpecificHeatCapacity_kj_mol_K() * expResult + prop.getFusionHeat_kj() + prop.getVaporizationHeat_kj());
        instance.setEnergy_kj(energy);
        instance.updateTemperatureAndPhase(standardPressure);
        double temperatureAt = prop.getEnergy_Pressure_Diagram().temperatureAt(instance.getEnergy_kj() / instance.getAmount_mol(), standardPressure);
        double result = instance.getTemperature_K();
        Phase resultPhase = instance.getPhase();
        System.out.println("Result Check");
        assertEquals(expResult, result, 0.0);
        System.out.println("T instance energy Check");
        assertEquals(expResult, temperatureAt, 0.0);
        System.out.println("Phase Check");
        assertEquals(Phase.GAS, resultPhase);
    }

    @Test
    public void testGetTemperatureWater_K_Liquid()
    {
        System.out.println("getTemperature_K");
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("H2O");
        Compound instance = new Compound(prop);
        double moles = 100;
        instance.setAmount_mol(moles);
        double expResult = standardTemperature;
        double energy = moles * (prop.getSpecificHeatCapacity_kj_mol_K() * expResult + prop.getFusionHeat_kj());
        instance.setEnergy_kj(energy);
        instance.updateTemperatureAndPhase(standardPressure);
        double temperatureAt = prop.getEnergy_Pressure_Diagram().temperatureAt(instance.getEnergy_kj() / instance.getAmount_mol(), standardPressure);
        double result = instance.getTemperature_K();
        Phase resultPhase = instance.getPhase();
        System.out.println("Phase Check");
        assertEquals(Phase.LIQUID, resultPhase);
        System.out.println("T instance energy Check");
        assertEquals(expResult, temperatureAt, 0.0);
        System.out.println("Result Check");
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetTemperatureWater_K_Solid()
    {
        System.out.println("getTemperature_K");
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("H2O");
        Compound instance = new Compound(prop);
        double moles = 100;
        instance.setAmount_mol(moles);
        double expResult = standardTemperature;
        double energy = moles * (prop.getSpecificHeatCapacity_kj_mol_K() * expResult);
        instance.setEnergy_kj(energy);
        instance.updateTemperatureAndPhase(standardPressure);
        double temperatureAt = prop.getEnergy_Pressure_Diagram().temperatureAt(instance.getEnergy_kj() / instance.getAmount_mol(), standardPressure);
        double result = instance.getTemperature_K();
        Phase resultPhase = instance.getPhase();
        System.out.println("Phase Check");
        assertEquals(Phase.SOLID, resultPhase);
        System.out.println("Result Check");
        assertEquals(expResult, result, 0.0);
        System.out.println("T instance energy Check");
        assertEquals(expResult, temperatureAt, 0.0);
    }

}
