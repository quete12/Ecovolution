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
    private WorldRenderer[] renderers;

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

    @Override
    public void draw()
    {
        background(255);
        world.update();

        pushMatrix();
        scale(0.1f);
        int xOffsFactor = (int) ((world.getWidth()+1)*Tile.SIZE);
        for (int i = 0; i < renderers.length; i++)
        {
            WorldRenderer renderer = renderers[i];
            renderer.render(world);
            image(renderer.getGraphics(), i*xOffsFactor, 0);
        }
        popMatrix();
        fill(0);
        text("FPS: " + frameRate, 1200, 100);
        text("Amount_moles: " + Consts.toDouble(world.getAmount_mol()), 1200, 150);
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
        renderers = new WorldRenderer[World.NUMBER_OF_LAYERS];
        for (int i = 0; i < renderers.length; i++)
        {
            renderers[i] = new WorldRenderer(
                    createGraphics(
                            (int) Tile.SIZE * world.getWidth(),
                            (int) Tile.SIZE * world.getHeight()),
                    new TileVolumeRenderer(i));

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
        world = new World(20, 20);
        Tile tile = world.getGrid()[0][0];
        LayerMixture layer = tile.getLayer(0);
        PhaseMixture solids = layer.getMixtureForPhase(Phase.SOLID);
        Compound water = solids.getCompound("H2O");
        water.add(10000 * Consts.PRESCISION, 1000 * Consts.PRESCISION);
        Compound o2 = solids.getCompound("O2");
        o2.add(10000 * Consts.PRESCISION, 1000 * Consts.PRESCISION);
    }

}
