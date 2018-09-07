/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public abstract class WorldOverlayRenderer {

    private PGraphics g;
    private World world;

    public WorldOverlayRenderer(PGraphics g, World world)
    {
        this.g = g;
        this.world = world;
    }

    protected abstract void render(PGraphics g, Tile tile);

    public PGraphics render()
    {
        g.beginDraw();
        g.background(0);
        Tile[][] grid = world.getGrid();
        for (Tile[] tiles : grid)
        {
            for (Tile tile : tiles)
            {
                render(g, tile);
            }
        }
        g.endDraw();
        return g;
    }

}
