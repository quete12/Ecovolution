/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.threads;

import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.representation.layers.PhaseRenderer;
import ch.schaermedia.ecovolution.world.DefaultWorldGen;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class RenderThread extends PApplet {

    private static final int FRAMERATE = 60;
    private static final int WORLDWIDTH = 30;
    private static final int WORLDLENGTH = 30;

    @Override
    public void keyPressed(KeyEvent event)
    {
    }

    @Override
    public void mouseWheel(MouseEvent event)
    {
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
    }

    @Override
    public void draw()
    {
        background(255);
        pushMatrix();
        scale(0.5f);
        renderWorld();

        popMatrix();
        LayerMixture layer1010 = world.getGrid()[10][10].getLayer(0);
        layer1010.addEnergy(new BigDouble(1000,0));

        fill(0);
        textSize(30);
        text("Temperature at 10|10: " + layer1010.getTemperature_k().toDoubleString()+ " K",1200,100);
        text("Pressure at 10|10: " + layer1010.getPressure_kPa().toDoubleString() + " kPa",1200,150);
        startUpdatersIfNotRunning();
    }

    private void renderWorld(){
        PGraphics phaseOverlay = phaseRenderer.render();
        image(phaseOverlay, 0, 0);
    }

    private void startUpdatersIfNotRunning()
    {
        if (!atmosUpdater.isRunning())
        {
            new Thread(atmosUpdater).start();
        }
        if (!entityUpdater.isRunning())
        {
            new Thread(entityUpdater).start();
        }
    }

    @Override
    public void setup()
    {
        setupWindow();
        setupWorld();
        setupEntities();
        setupUpdateThreads();
        setupRenderers();
    }

    private void setupWindow()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(FRAMERATE);
    }

    private World world;

    private void setupWorld()
    {
        world = new World(WORLDWIDTH, WORLDLENGTH, new DefaultWorldGen(this));
    }

    private void setupEntities()
    {

    }

    private EntityUpdater entityUpdater;
    private AtmosUpdater atmosUpdater;

    private void setupUpdateThreads()
    {
        entityUpdater = new EntityUpdater();
        atmosUpdater = new AtmosUpdater(world);
    }

    private PhaseRenderer phaseRenderer;

    private void setupRenderers()
    {
        phaseRenderer = new PhaseRenderer(
                createGraphics(
                        (int) Tile.SIZE * world.getWidth(),
                        (int) Tile.SIZE * world.getHeight(),
                        P2D
                ),
                world,
                0);
    }

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

}
