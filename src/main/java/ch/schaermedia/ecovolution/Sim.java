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
    private static final int WORLD_WIDTH = 30;
    private static final int WORLD_HEIGHT = 30;
    private static final int SPREAD_RANGE = 3;
    private static final int FRAMERATE = 60;

    private World world;
    private WorldRenderer[][] renderers;

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

    private double temp;

    @Override
    public void draw()
    {
        world.update();
        double temperature = world.getWorldTemeprature();
        int x = 10;
        int y = 10;
        if (temperature < 200)
        {
            int range = 2;
            for (int i = -range; i <= range; i++)
            {
                for (int j = -range; j <= range; j++)
                {
                    world.getGrid()[x + i][y + j].getMixAtLayer(0).addEnergy(5000d);
                    world.getGrid()[x + i][y + j].updateStats();
                }
            }
        }
        background(255);
        pushMatrix();
        scale(0.5f);
        for (int i = 0; i < renderers.length; i++)
        {
            WorldRenderer[] rendererArray = renderers[i];
            for (int j = 0; j < rendererArray.length; j++)
            {
                WorldRenderer renderer = renderers[i][j];
                renderer.render(world);
                PGraphics graph = renderer.getG();
                image(graph, j * graph.width + j * World.TILE_SIZE, i * graph.height + i * World.TILE_SIZE);
            }
        }
        popMatrix();
        stroke(0);
        strokeWeight(5);
        fill(0);
        textSize(20);
        text("Fps: " + frameRate, 1300, 50);
        text("World Temperature: " + temperature, 1300, 100);
        text("Pressure at " + x + "|" + y + " " + world.getGrid()[x][y].getMixAtLayer(0).getPressure_kPa(), 1300, 150);
        text("Moles over volume " + world.getGrid()[x][y].getMixAtLayer(0).molesOverVolume(), 1300, 200);
        text("Moles over pressure " + world.getGrid()[x][y].getMixAtLayer(0).molesOverPressure(), 1300, 250);
        text("Moles under volume " + world.getGrid()[x][y].getMixAtLayer(0).molesUnderVolume(), 1300, 300);
        text("Moles under pressure " + world.getGrid()[x][y].getMixAtLayer(0).molesUnderPressure(), 1300, 350);
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
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sim.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void rendererSetup()
    {
        renderers = new WorldRenderer[5][3];
        for (int i = 0; i < renderers[0].length; i++)
        {
            PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
            renderers[0][i] = new WorldRenderer(new TileRenderer(i, TileRenderer.ShowDetail.VOLUME), graph);
        }
        for (int i = 0; i < renderers[0].length; i++)
        {
            PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
            renderers[1][i] = new WorldRenderer(new TileRenderer(i, TileRenderer.ShowDetail.PRESSURE), graph);
        }
        for (int i = 0; i < renderers[1].length; i++)
        {
            PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
            renderers[2][i] = new WorldRenderer(new TileRenderer(i, "H2O"), graph);
        }
        for (int i = 0; i < renderers[2].length; i++)
        {
            PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
            renderers[3][i] = new WorldRenderer(new TileRenderer(i, "O2"), graph);
        }
        for (int i = 0; i < renderers[3].length; i++)
        {
            PGraphics graph = createGraphics((int) (world.getWidth() * World.TILE_SIZE), (int) (world.getHeight() * World.TILE_SIZE), P2D);
            renderers[4][i] = new WorldRenderer(new TileRenderer(i, "CO2"), graph);
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
        public Tile generate(int x, int y, float size, int horizontalSpreadSize)
        {
            Tile tile = new Tile(size, size, x, y, horizontalSpreadSize);
            tile.getMixAtLayer(0).add("H2O", Phase.SOLID.idx, 1000, 30000);
            tile.getMixAtLayer(0).add("O2", Phase.SOLID.idx, 2000, 30000);
            tile.getMixAtLayer(0).add("CO2", Phase.SOLID.idx, 2000, 30000);
            return tile;
        }

    }

}
