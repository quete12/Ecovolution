/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.math.Consts;
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
        BigDouble pressure_kPa = layerMix.pressure_kPa();
        int fill;
        if (pressure_kPa.isZero())
        {
            fill = 0;
        } else if (pressure_kPa.compareTo(Consts.STANDARD_PRESSURE_kPa) > 0)
        {
            float percentage = (float) ((pressure_kPa.toDouble() / Consts.STANDARD_PRESSURE_kPa.toDouble()) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) (pressure_kPa.toDouble() / Consts.STANDARD_PRESSURE_kPa.toDouble());
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
