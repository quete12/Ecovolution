/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.Compound;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.world.Tile;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class PhaseDiagramO2Test {

    public PhaseDiagramO2Test()
    {
    }

    @BeforeClass
    public static void initChemics() throws FileNotFoundException
    {
        ChemUtilities.readElements("res/Chemics.json");
    }

    @Test
    public void testPhaseAtSpecificPressureAndEnergy()
    {
        BigDouble amount = new BigDouble(3000, 0);
        BigDouble pressure = new BigDouble(240, 0);
        BigDouble energy = new BigDouble(95880, 0).div(amount);
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("O2");
        PhaseDiagram diag = prop.getPhaseDiagram();
        Phase phase = diag.getPhase(energy, pressure);

        assertEquals(phase, Phase.GAS);
    }

    @Test
    public void testTemperatureAtSpecificPressureAndEnergy()
    {
        BigDouble amount = new BigDouble(3000, 0);
        BigDouble pressure = new BigDouble(240, 0);
        BigDouble energy = new BigDouble(95880, 0).div(amount);
        BigDouble targetTemp = ChemUtilities.toKelvin(new BigDouble(15.5));

        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("O2");
        Compound o2 = new Compound(prop);
        o2.init(amount, targetTemp, Phase.GAS);
        o2.updateStats(pressure, Tile.LAYER_VOLUME_L);
        System.out.println("O2: " + o2);
        System.out.println("o2 Temp: " + o2.getTemperature_k() + " " + ChemUtilities.toCelsius(o2.getTemperature_k()));
        PhaseDiagram diag = prop.getPhaseDiagram();
        BigDouble temperature = diag.getTemperature(energy, pressure, Phase.GAS);
        System.out.println("Calculated Temp: " + temperature);
    }

}
