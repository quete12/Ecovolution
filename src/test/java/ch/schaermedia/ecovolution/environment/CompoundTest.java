/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.Compound;
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Quentin
 */
public class CompoundTest {

    public CompoundTest() {
        try {
            ChemUtilities.readElements("res/Chemics.json");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CompoundTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getTemperature_K method, of class Compound.
     */
    @Test
    public void testGetTemperature_K() {
        System.out.println("getTemperature_K");
        Compound instance = new Compound(CompoundProperties.getPropertiesFromCode("CO2"));
        instance.setAmount_mol(100);
        double expResult = 0.0;
        for (int i = 0; i < 25; i++) {
        double result = instance.getTemperature_K();
        instance.addEnergy(2000);
            System.out.println("Temperature: " + result + " phase: " + instance.getPhase());
        }
        //assertEquals(expResult, result, 0.0);
    }

}
