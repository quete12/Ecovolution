/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.environment.basic.Tile;
import ch.schaermedia.ecovolution.environment.basic.TileGenerator;
import ch.schaermedia.ecovolution.environment.basic.World;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.representation.TileRenderer;
import ch.schaermedia.ecovolution.representation.WorldRenderer;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class Sim extends PApplet {

    private World world;
    private WorldRenderer[][] renderers;

    @Override
    public void settings()
    {
        size(1800, 1000, P2D);
    }

    @Override
    public void draw()
    {
        world.update();
        world.getGrid()[1][1].getMixAtLayer(0).addEnergy(10000000d);
        System.out.println("WorldTemp: " + world.getWorldTemeprature());
        background(255);
        for (int i = 0; i < renderers.length; i++)
        {
            WorldRenderer[] rendererArray = renderers[i];
            for (int j = 0; j < rendererArray.length; j++)
            {
                WorldRenderer renderer = renderers[i][j];
                PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
                renderer.render(world, graph);
                image(graph, j * graph.width, i * graph.height);
            }
        }
    }

    @Override
    public void setup()
    {
        windowSetup();
        chemSetup();
        worldSetup();
        rendererSetup();
    }

    private void chemSetup()
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void rendererSetup()
    {
        renderers = new WorldRenderer[2][3];
        for (int i = 0; i < renderers[0].length; i++)
        {
            renderers[0][i] = new WorldRenderer(new TileRenderer(i, TileRenderer.ShowDetail.VOLUME));
        }
        for (int i = 0; i < renderers[1].length; i++)
        {
            renderers[1][i] = new WorldRenderer(new TileRenderer(i, TileRenderer.ShowDetail.PHASE));
        }
    }

    private void windowSetup()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(60);
    }

    public static void main(String[] args)
    {
        PApplet.main(Sim.class, args);
    }

    private void worldSetup()
    {
        world = new World(50, 50, 3, new TGenerator());
    }

    private class TGenerator implements TileGenerator {

        @Override
        public Tile generate(int x, int y, float size)
        {
            Tile tile = new Tile(size, size, x, y);
            tile.getMixAtLayer(0).add("CO2", 0, 10000, 150);
            return tile;
        }

    }

}
