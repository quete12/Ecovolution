/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.debug;

import ch.schaermedia.ecovolution.Sim;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.PhaseDiagram_Temperature_Pressure;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public class PrintPhaseDiagramFunctions {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sim.class.getName()).log(Level.SEVERE, null, ex);
        }
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("H2O");
        PhaseDiagram_Temperature_Pressure tpd = prop.getTemperature_Pressure_Diagram();
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(PrintPhaseDiagramFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Sublimation: " + tpd.getSublimationBorder());
        System.out.println("Melting: " + tpd.getMeltingBorder());
        System.out.println("Vaporating: " + tpd.getVaporizationBorder());

        System.out.println("MeltingPointTemp: " + tpd.getMeltingBorder().x(CompoundMix.STATIC_PRESSURE_kPa));
        System.out.println("BoilingPointTemp: " + tpd.getVaporizationBorder().x(CompoundMix.STATIC_PRESSURE_kPa));

    }

}
