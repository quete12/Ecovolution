/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
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
            Logger.getLogger(PhaseDiagram_Energy_PressureTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testWaterAtNeg10(){
        String compound = "H2O";
        CompoundProperties properties = CompoundProperties.getPropertiesFromCode(compound);
        double targetTemp = ChemUtilities.toKelvin(-10);
        Phase targetPhase = Phase.SOLID;


    }

}
