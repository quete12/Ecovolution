/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.Sim;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.compound.Compound;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.ecovolution.general.math.BigDouble;
import ch.schaermedia.ecovolution.general.math.LinearFunction;
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
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void test()
    {
        CompoundProperties properties = CompoundProperties.getPropertiesFromCode("H2O");
        Compound compound = new Compound(properties);
        compound.add(new BigDouble(1, 298250000), new BigDouble(59, 725998000));
        compound.importBuffers();
        PhaseDiagram_Energy_Pressure diag = properties.getEnergy_Pressure_Diagram();

        BigDouble externalPressure = new BigDouble(0, 0);
        BigDouble totalVolume = new BigDouble(125000, 0);

        BigDouble energyPerMol = compound.getEnergy_kj().div(compound.getAmount_mol(), new BigDouble());
        System.out.println("E per Mol: " + energyPerMol);

        LinearFunction[] vaporizationMax = diag.getSublimationBorder().getSublimationMax();
        LinearFunction sublMax = vaporizationMax[0];
        System.out.println("x at y=0.0 is: " + sublMax.x(new BigDouble(0.0)));
        System.out.println("y at x=0.0 is: " + sublMax.y(new BigDouble(0.0)));
        System.out.println("y at x="+energyPerMol+" is: " + sublMax.y(energyPerMol));
//
//        System.out.println("Phase: " + phaseAt);
//        compound.updateStats(externalPressure, totalVolume);
//        System.out.println("Compound: " + compound);

    }

}
