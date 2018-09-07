/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.Compound;
import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.chemics.atmospherics.PhaseMixture;
import ch.schaermedia.ecovolution.world.DefaultWorldGen;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import ch.schaermedia.old.ecovolution.general.AtmosphericUpdater;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.old.ecovolution.representation.TilePhaseRenderer;
import ch.schaermedia.old.ecovolution.representation.TilePressureRenderer;
import ch.schaermedia.old.ecovolution.representation.TileVolumeRenderer;
import ch.schaermedia.old.ecovolution.representation.WorldRenderer;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;

/**
 *
 * @author Quentin
 */
public class Sim extends PApplet {

    private static final int FRAMERATE = 60;
    private World world;
    private WorldRenderer[][] renderers;
    private AtmosphericUpdater atmosUpdater;

    @Override

    public void settings()
    {
        size(1900, 1000, P2D);
    }

    @Override
    public void draw()
    {
        background(255);
        pushMatrix();
        scale(0.25f);
        long renderStart = System.currentTimeMillis();

        int xOffsFactor = (int) ((world.getWidth() + 1) * Tile.SIZE);
        int yOffsFactor = (int) ((world.getHeight() + 1) * Tile.SIZE);
//        int i = 0;

//        for (int j = 0; j < renderers[i].length; j++)
//        {
//            WorldRenderer renderer = renderers[i][j];
//            renderer.render(world);
//            image(renderer.getGraphics(), i * xOffsFactor, j * yOffsFactor);
//        }
        for (int i = 0; i < renderers.length; i++)
        {
            for (int j = 0; j < renderers[i].length; j++)
            {
                WorldRenderer renderer = renderers[i][j];
                renderer.render(world);
                image(renderer.getGraphics(), i * xOffsFactor, j * yOffsFactor);
            }
        }

//
//        renderer.render(world);
//        image(renderer.getGraphics(), Tile.SIZE, Tile.SIZE);
        long renderDuration = System.currentTimeMillis() - renderStart;
        popMatrix();
        Tile tile00 = world.getGrid()[0][0];
        LayerMixture layer00 = tile00.getLayer(0);
        Tile tile1010 = world.getGrid()[10][10];
        LayerMixture layer1010 = tile1010.getLayer(0);
        layer00.addEnergy(new BigDouble(1000, 0));
        fill(0);
        text("FPS: " + frameRate, 1200, 100);
        text("Rendercycle time: " + renderDuration, 1200, 150);
        text("Updatecycle time: " + atmosUpdater.getCycleTime(), 1200, 200);
        text("Temp At [0][0][0]: " + layer00.getTemperature_k().toDouble() + " K", 1200, 300);
        text("Pressure At [0][0][0]: " + layer00.getPressure_kPa().toDouble() + " kPa", 1200, 350);
        text("Temp At [10][10][0]: " + layer1010.getTemperature_k().toDouble() + " K", 1200, 400);
        text("Pressure At [10][10][0]: " + layer1010.getPressure_kPa().toDouble() + " kPa", 1200, 450);

        if (!atmosUpdater.isRunning())
        {
            new Thread(atmosUpdater).start();
        }
    }

    @Override
    public void setup()
    {
        windowSetup();
        chemSetup();
        worldSetup();
        rendererSetup();
        threadSetup();
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

//        renderer = new WorldRenderer(
//                createGraphics(
//                        (int) Tile.SIZE * world.getWidth(),
//                        (int) Tile.SIZE * world.getHeight(),
//                        P2D
//                ),
//                new TileHeightRenderer());
        /*
        Renderer Setup for per layer pressure and volume display
         */
        renderers = new WorldRenderer[World.NUMBER_OF_LAYERS][3];
        for (int i = 0; i < renderers.length; i++)
        {
            renderers[i][0] = new WorldRenderer(
                    createGraphics(
                            (int) Tile.SIZE * world.getWidth(),
                            (int) Tile.SIZE * world.getHeight(),
                            P2D
                    ),
                    new TileVolumeRenderer(i));
            renderers[i][1] = new WorldRenderer(
                    createGraphics(
                            (int) Tile.SIZE * world.getWidth(),
                            (int) Tile.SIZE * world.getHeight(),
                            P2D
                    ),
                    new TilePressureRenderer(i));
            renderers[i][2] = new WorldRenderer(
                    createGraphics(
                            (int) Tile.SIZE * world.getWidth(),
                            (int) Tile.SIZE * world.getHeight(),
                            P2D
                    ),
                    new TilePhaseRenderer(i));
        }
    }

    private void windowSetup()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(FRAMERATE);
        textSize(30);
    }

    public static void main(String[] args)
    {
        PApplet.main(Sim.class, args);
    }

    private void worldSetup()
    {
        world = new World(30, 30, new DefaultWorldGen(this));
        Tile tile = world.getGrid()[0][0];
        LayerMixture layer = tile.getLayer(0);
        PhaseMixture solids = layer.getMixtureForPhase(Phase.SOLID);
        Compound water = solids.getCompound("H2O");
        water.add(new BigDouble(100000, 0), new BigDouble(500, 0));
        Compound o2 = solids.getCompound("CO2");
        o2.add(new BigDouble(200000, 0), new BigDouble(500, 0));
    }

    private void threadSetup()
    {
        atmosUpdater = new AtmosphericUpdater(world);
    }
}
