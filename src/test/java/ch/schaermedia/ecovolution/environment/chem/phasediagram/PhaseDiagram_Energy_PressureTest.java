/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.Sim;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.compound.Compound;
import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.ecovolution.general.math.BigDouble;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class PhaseDiagram_Energy_PressureTest {

    public PhaseDiagram_Energy_PressureTest()
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testNegTempCO2()
    {
        CompoundProperties co2Prop = CompoundProperties.getPropertiesFromCode("CO2");
        Compound co2 = new Compound(co2Prop);
        co2.add(new BigDouble(2000, 0), new BigDouble(31705, 458000));
        co2.importBuffers();
        PhaseDiagram_Energy_Pressure diag = co2Prop.getEnergy_Pressure_Diagram();

        BigDouble externalPressure = new BigDouble(8, 264011);
        BigDouble totalVolume = new BigDouble(125000, 0);

        BigDouble energyPerMol = co2.getEnergy_kj().div(co2.getAmount_mol(), new BigDouble());
        System.out.println("E per Mol: " + energyPerMol);
        Phase phaseAt = diag.getPhaseAt(energyPerMol, externalPressure);
        System.out.println("Phase: " + phaseAt);
        co2.updateStats(externalPressure, totalVolume);
        System.out.println("Compound: " + co2);

    }

}
