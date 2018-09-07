/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class WorldRenderer {

    private final PGraphics graphics;
    private final TileRenderer subRenderer;

    public WorldRenderer(PGraphics graphics, TileRenderer tileRenderer)
    {
        this.graphics = graphics;
        this.subRenderer = tileRenderer;
    }

    public void render(World world)
    {
        graphics.beginDraw();
        Tile[][] grid = world.getGrid();
        for (int x = 0; x < world.getWidth(); x++)
        {
            for (int y = 0; y < world.getHeight(); y++)
            {
                subRenderer.render(graphics, grid[x][y]);
            }
        }
        graphics.endDraw();
    }

    public PGraphics getGraphics()
    {
        return graphics;
    }


}
