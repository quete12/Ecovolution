/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.environment.world.World;
import ch.schaermedia.ecovolution.general.math.Consts;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TilePressureRenderer implements TileRenderer {

    private final int layer;

    public TilePressureRenderer()
    {
        layer = -1;
    }

    public TilePressureRenderer(int layer)
    {
        this.layer = layer;
    }

    @Override
    public void render(PGraphics g, Tile tile)
    {
        g.noStroke();
        if (layer == -1)
        {
            prepareForTileRendering(g, tile);
        } else
        {
            prepareForLayerRendering(g, tile);
        }
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

    private void prepareForTileRendering(PGraphics g, Tile tile)
    {
        long pressure_kPa = tile.getPressure_kPa();
        int fill;
        long tilePressure = Consts.STANDARD_PRESSURE_kPa * World.NUMBER_OF_LAYERS;
        if (pressure_kPa > tilePressure)
        {
            float percentage = (float) (((double) pressure_kPa / (double) tilePressure) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) ((double) pressure_kPa / (double) tilePressure);
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

    private void prepareForLayerRendering(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layer);
        long pressure_kPa = layerMix.getPressure_kPa();
        int fill;
        if (pressure_kPa > Tile.LAYER_VOLUME_L)
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
