/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.general.math.Consts;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TilePressureRenderer implements TileRenderer {

    private final int layer;

    public TilePressureRenderer(int layer)
    {
        this.layer = layer;
    }

    @Override
    public void render(PGraphics g, Tile tile)
    {
        prepareForLayerRendering(g, tile);
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

    private void prepareForLayerRendering(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layer);
        long pressure_kPa = layerMix.getPressure_kPa();
        int fill;
        if (pressure_kPa == 0)
        {
            fill = 0;
        } else if (pressure_kPa > Consts.STANDARD_PRESSURE_kPa)
        {
            float percentage = (float) (((double) pressure_kPa / (double) Consts.STANDARD_PRESSURE_kPa) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) ((double) pressure_kPa / (double) Consts.STANDARD_PRESSURE_kPa);
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
