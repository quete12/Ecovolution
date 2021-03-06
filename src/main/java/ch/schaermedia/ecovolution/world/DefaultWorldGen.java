/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.world;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.Compound;
import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.chemics.atmospherics.PhaseMixture;
import ch.schaermedia.ecovolution.math.BigDouble;
import processing.core.PApplet;

/**
 *
 * @author Quentin
 */
public class DefaultWorldGen implements TileGenerator {

    private final PApplet applet;

    public DefaultWorldGen(PApplet applet)
    {
        this.applet = applet;
    }

    @Override
    public Tile generate(int x, int y, int numLayers)
    {
        int height = (int) (applet.noise((float) (x * 0.1), (float) ((y + 1000) * 0.1)) * 100);
        //System.out.println("Tile[" + x + "][" + y + "] hegiht: " + height + " created");
        Tile result = new Tile(x, y, numLayers, height);

        BigDouble standardTemperature = ChemUtilities.toKelvin(new BigDouble(-80.0));

        LayerMixture groundLayer = result.getLayer(0);
        PhaseMixture solids = groundLayer.getMixtureForPhase(Phase.SOLID);
        Compound oxigen = solids.getCompound("O2");
        oxigen.init(new BigDouble(3000, 0), standardTemperature, Phase.GAS);
        Compound carbondioxid = solids.getCompound("CO2");
        carbondioxid.init(new BigDouble(8000, 0), standardTemperature, Phase.SOLID);
        Compound water = solids.getCompound("H2O");
        water.init(new BigDouble(3000, 0), standardTemperature, Phase.SOLID);
        groundLayer.updateStats(ChemUtilities.STANDARD_PRESSURE_kPa, Tile.LAYER_VOLUME_L);
        return result;
    }

}
