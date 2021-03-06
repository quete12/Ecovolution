/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.threads;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.Compound;
import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.chemics.atmospherics.PhaseMixture;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.representation.layers.PhaseRenderer;
import ch.schaermedia.ecovolution.world.DefaultWorldGen;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import java.util.List;
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
        scale += (event.getCount() * 0.03);
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
    }

    private float scale = 0.75f;
    private final BigDouble HUNDRED = new BigDouble(100, 0).setImmutable();

    @Override
    public void draw()
    {
        background(255);
        pushMatrix();
        scale(scale);
        renderWorld();

        popMatrix();
        int xIdx = (int) (mouseX / scale / Tile.SIZE);
        int yIdx = (int) (mouseY / scale / Tile.SIZE);

        world.getGrid()[5][5].getLayer(0).addEnergy(new BigDouble(100, 0));
        int xAlign = 1000;
        if (xIdx >= 0 && xIdx < world.getWidth() && yIdx >= 0 && yIdx < world.getHeight())
        {
            LayerMixture selectedLayer = world.getGrid()[xIdx][yIdx].getLayer(0);
            fill(0);
            textSize(30);
            text("Tile at: " + xIdx + " | " + yIdx, xAlign, 50);
            text("Temperature: " + ChemUtilities.toCelsius(selectedLayer.getTemperature_k()).toDoubleString() + " C", xAlign, 100);
            text("Pressure: " + selectedLayer.getPressure_kPa().toDoubleString() + " kPa", xAlign, 150);
            BigDouble[] phasePercentages = selectedLayer.getPhasePercentages();
            if (phasePercentages != null)
            {
                int offset = 0;
                final int start = 200;
                for (int i = 0; i < phasePercentages.length; i++)
                {
                    BigDouble phasePercentage = phasePercentages[i];
                    textSize(20);
                    offset += 10;
                    PhaseMixture phase = selectedLayer.getMixtureForPhase(i);
                    text("["+i+"] % of " + Phase.fromIdx(i) + ": " + phasePercentage.mul(HUNDRED).toDoubleString() + " moles: " + phase.getAmount_mol().toDoubleString(), xAlign, start + offset);
                    offset += 20;
                    List<Compound> compounds = phase.getCompounds();
                    text("Number of Compounds: " + compounds.size(), xAlign, start + offset);
                    offset += 20;
                    for (Compound compound : compounds)
                    {
                        if (compound.getAmount_mol().isNotZero())
                        {
                            textSize(15);
                            text("Amount of " + compound.getCode() + ": " + compound.getAmount_mol().toDoubleString() + " energy: " + compound.getEnergy_kj() + " temperature: " + compound.getTemperature_k().toDoubleString(), xAlign, start + offset);
                            offset += 20;
                        }else{
                            textSize(10);
                            text("Amount of " + compound.getCode() + ": " + compound.getAmount_mol().toDoubleString() + " energy: " + compound.getEnergy_kj() + " temperature: " + compound.getTemperature_k().toDoubleString(), xAlign, start + offset);
                            offset += 20;
                        }
                    }
                }
            }
        }
        startUpdatersIfNotRunning();
    }

    private void renderWorld()
    {
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
