/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.threads;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class RenderThread extends PApplet {

    private static final int FRAMERATE = 60;

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

        if (!entityUpdater.isRunning())
        {
            new Thread(entityUpdater).start();
        }
        if (!atmosUpdater.isRunning())
        {
            new Thread(atmosUpdater).start();
        }
    }

    @Override
    public void setup()
    {
        setupWindow();
        setupWorld();
        setupEntities();
        setupUpdateThreads();
    }

    private void setupWindow()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(FRAMERATE);
    }

    private void setupWorld()
    {

    }

    private void setupEntities()
    {

    }

    private EntityUpdater entityUpdater;
    private AtmosUpdater atmosUpdater;

    private void setupUpdateThreads()
    {
        entityUpdater = new EntityUpdater();
        atmosUpdater = new AtmosUpdater();
    }

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

}
