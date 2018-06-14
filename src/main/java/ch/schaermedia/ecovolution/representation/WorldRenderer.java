/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.basic.Tile;
import ch.schaermedia.ecovolution.environment.basic.World;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class WorldRenderer {

    private final TileRenderer tileRenderer;
    private final PGraphics g;

    public WorldRenderer(TileRenderer tileRenderer,PGraphics g)
    {
        this.tileRenderer = tileRenderer;
        this.g = g;
    }

    public void render(World world)
    {
        g.beginDraw();
        Tile[][] grid = world.getGrid();
        for (Tile[] tiles : grid)
        {
            for (Tile tile : tiles)
            {
                tileRenderer.render(tile, g);
            }
        }
        g.endDraw();
    }

    public PGraphics getG()
    {
        return g;
    }

    
}
