/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.compound.Compound;
import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.ecovolution.environment.chem.compound.PhaseMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.environment.world.World;
import ch.schaermedia.ecovolution.general.math.Consts;
import ch.schaermedia.ecovolution.representation.TilePressureRenderer;
import ch.schaermedia.ecovolution.representation.TileVolumeRenderer;
import ch.schaermedia.ecovolution.representation.WorldRenderer;
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

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

    @Override
    public void draw()
    {
        background(255);
        long updateStart = System.currentTimeMillis();
        world.update();
        long updateDuration = System.currentTimeMillis() - updateStart;
        pushMatrix();
        scale(0.01f);
        int xOffsFactor = (int) ((world.getWidth() + 1) * Tile.SIZE);
        int yOffsFactor = (int) ((world.getHeight() + 1) * Tile.SIZE);
        long renderStart = System.currentTimeMillis();
        int i = 0;

        for (int j = 0; j < renderers[i].length; j++)
        {
            WorldRenderer renderer = renderers[i][j];
            renderer.render(world);
            image(renderer.getGraphics(), i * xOffsFactor, j * yOffsFactor);
        }
//        for (int i = 0; i < renderers.length; i++)
//        {
//            for (int j = 0; j < renderers[i].length; j++)
//            {
//                WorldRenderer renderer = renderers[i][j];
//                renderer.render(world);
//                image(renderer.getGraphics(), i * xOffsFactor, j * yOffsFactor);
//            }
//        }
        long renderDuration = System.currentTimeMillis() - renderStart;
        popMatrix();
        fill(0);
        text("FPS: " + frameRate, 1200, 100);
        text("Update: " + updateDuration, 1200, 150);
        text("Rendering: " + renderDuration, 1200, 200);
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
        renderers = new WorldRenderer[World.NUMBER_OF_LAYERS][2];
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
        world = new World(100, 100);
        Tile tile = world.getGrid()[0][0];
        LayerMixture layer = tile.getLayer(0);
        PhaseMixture solids = layer.getMixtureForPhase(Phase.SOLID);
        Compound water = solids.getCompound("H2O");
        water.add(100000 * Consts.PRESCISION, 10000 * Consts.PRESCISION);
        Compound o2 = solids.getCompound("CO2");
        o2.add(200000 * Consts.PRESCISION, 10000 * Consts.PRESCISION);
    }
}
