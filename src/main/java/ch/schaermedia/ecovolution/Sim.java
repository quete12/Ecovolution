/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.environment.world.World;
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
    private WorldRenderer renderer;

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
        renderer.render(world);
        image(renderer.getGraphics(), 0, 0);
        popMatrix();
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
        renderer = new WorldRenderer(
                createGraphics(
                        (int) Tile.SIZE * world.getWidth(),
                        (int) Tile.SIZE * world.getHeight()),
                new TileVolumeRenderer());
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
        world = new World(30, 30);
    }

}
