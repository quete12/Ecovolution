/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class PhaseDiagramH2OTest {

    public PhaseDiagramH2OTest()
    {
    }

    @BeforeClass
    public static void initChemics() throws FileNotFoundException
    {
        ChemUtilities.readElements("res/Chemics.json");
    }

    @Test
    public void testFailingEnergyAtZeroPressure()
    {
        BigDouble pressure = new BigDouble(0, 0);
        BigDouble energy = new BigDouble(28, 626243750);
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("H2O");
        PhaseDiagram diag = prop.getPhaseDiagram();
        Phase phase = diag.getPhase(energy, pressure);

        assertEquals(phase, Phase.GAS);
    }

}
