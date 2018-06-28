/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class CompoundMixTest {

    private final double standardPressure = CompoundMix.STATIC_PRESSURE_kPa;
    private final double standardVolume = CompoundMix.STATIC_VOLUME_L;
    private final double standardTemperature = ChemUtilities.toKelvin(13.5);

    public CompoundMixTest()
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(CompoundMixTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CompoundMix getDefaultMix()
    {
        String compound = "H2O";
        CompoundMix mix = new CompoundMix(0, 0, 0, 1);
        Phase phase = Phase.LIQUID;
        double moles = ChemUtilities.moles(standardPressure, standardVolume, standardTemperature);
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode(compound);
        double energyForTemp = moles * (prop.getSpecificHeatCapacity_kj_mol_K() * standardTemperature + prop.getFusionHeat_kj());

        mix.add(compound, phase.idx, moles, energyForTemp);
        mix.updateStats();
        mix.updateTemperatureAndPhaseChanges(standardPressure);
        mix.updateStats();
        assertEquals(moles, mix.getAmount_mol(), 0.0);
        assertEquals(standardTemperature, mix.getTemperature_K(), 0.0);
        assertEquals(standardPressure, mix.getPressure_kPa(), 0.0);
        assertEquals(standardVolume, mix.getVolume_L(), 0.0);
        return mix;
    }

    /**
     * Test of molesUnderPressure method, of class CompoundMix.
     */
    @Test
    public void testMolesUnderPressure()
    {
        System.out.println("molesUnderPressure");
        CompoundMix instance = getDefaultMix();
        double expResult = 0.0;
        double result = instance.molesUnderPressure();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of molesOverPressure method, of class CompoundMix.
     */
    @Test
    public void testMolesOverPressure()
    {
        System.out.println("molesOverPressure");
        CompoundMix instance = getDefaultMix();
        double expResult = 0.0;
        double result = instance.molesOverPressure();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of molesOverVolume method, of class CompoundMix.
     */
    @Test
    public void testMolesOverVolume()
    {
        System.out.println("molesOverVolume");
        CompoundMix instance = getDefaultMix();
        double expResult = 0.0;
        double result = instance.molesOverVolume();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of molesUnderVolume method, of class CompoundMix.
     */
    @Test
    public void testMolesUnderVolume()
    {
        System.out.println("molesUnderVolume");
        CompoundMix instance = getDefaultMix();
        double expResult = 0.0;
        double result = instance.molesUnderVolume();
        assertEquals(expResult, result, 0.0);
    }

}
