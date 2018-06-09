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
import ch.schaermedia.ecovolution.environment.chem.Phase;
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

    //TODO: move Configuration!
    private static final int WORLD_WIDTH = 50;
    private static final int WORLD_HEIGHT = 50;
    private static final int SPREAD_RANGE = 3;
    private static final int FRAMERATE = 60;

    private World world;
    private WorldRenderer[][] renderers;

    @Override
    public void settings()
    {
        size(1800, 1000, P2D);
    }

    private double prevTemp;

    @Override
    public void draw()
    {
        world.update();
        double temperature = world.getWorldTemeprature();
        if (world.getWorldTemeprature() < 400)
        {
            int x = 20;
            int y = 20;
            for (int i = -SPREAD_RANGE; i <= SPREAD_RANGE; i++)
            {
                for (int j = -SPREAD_RANGE; j <= SPREAD_RANGE; j++)
                {
                    world.getGrid()[x + i][y + j].getMixAtLayer(0).addEnergy(5000d);
                }
            }
        }
        System.out.println("WorldTemp: " + temperature);
        if (temperature - prevTemp > 100)
        {
            System.out.println("Huge Temp Diff!!");
        }
        prevTemp = temperature;
        background(255);
        scale(0.5f);
        for (int i = 0; i < renderers.length; i++)
        {
            WorldRenderer[] rendererArray = renderers[i];
            for (int j = 0; j < rendererArray.length; j++)
            {
                WorldRenderer renderer = renderers[i][j];
                PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
                renderer.render(world, graph);
                image(graph, j * graph.width + j * World.TILE_SIZE, i * graph.height + i * World.TILE_SIZE);
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
        surface.setFrameRate(FRAMERATE);
    }

    public static void main(String[] args)
    {
        PApplet.main(Sim.class, args);
    }

    private void worldSetup()
    {
        world = new World(WORLD_WIDTH, WORLD_HEIGHT, SPREAD_RANGE, new TGenerator());
    }

    private class TGenerator implements TileGenerator {

        @Override
        public Tile generate(int x, int y, float size)
        {
            Tile tile = new Tile(size, size, x, y);
            tile.getMixAtLayer(0).add("H2O", Phase.SOLID.idx, 1000, 15000);
            tile.getMixAtLayer(0).add("O2", Phase.SOLID.idx, 2000, 15000);
            tile.getMixAtLayer(0).add("CO2", Phase.SOLID.idx, 2000, 15000);
            return tile;
        }

    }

}
