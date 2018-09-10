/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.threads.RenderThread;
import ch.schaermedia.ecovolution.world.Tile;
import java.io.FileNotFoundException;
import processing.core.PApplet;

/**
 *
 * @author Quentin
 */
public class Ecovolution {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        ChemUtilities.readElements("res/Chemics.json");
        System.out.println("Moles in a Layer: " + ChemUtilities.moles(ChemUtilities.STANDARD_PRESSURE_kPa, Tile.LAYER_VOLUME_L, ChemUtilities.toKelvin(new BigDouble(13.5))));
        PApplet.main(RenderThread.class, args);
    }

}
