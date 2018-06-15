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
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import ch.schaermedia.ecovolution.environment.chem.Phase;
import ch.schaermedia.ecovolution.representation.PhaseDiagramRenderer;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class Test extends PApplet {

    //TODO: move Configuration!
    private static final int WORLD_WIDTH = 50;
    private static final int WORLD_HEIGHT = 50;
    private static final int SPREAD_RANGE = 3;
    private static final int FRAMERATE = 60;

    private World world;
    private PhaseDiagramRenderer phaseRenderer;

    public Test()
    {
    }

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

    private double prevTemp;
    private PGraphics graphic;

    @Override
    public void draw()
    {
        clear();
        phaseRenderer.render(graphic);
        scale(0.5f);
        image(graphic, 0, 0);
    }

    @Override
    public void setup()
    {
        windowSetup();
        chemSetup();
        worldSetup();
        rendererSetup();
        graphic = createGraphics(2000, 2000, P2D);
    }

    private void chemSetup()
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void rendererSetup()
    {
        phaseRenderer = new PhaseDiagramRenderer(CompoundProperties.getPropertiesFromCode("H2O"));
    }

    private void windowSetup()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(FRAMERATE);
    }

    public static void main(String[] args)
    {
        PApplet.main(Test.class, args);
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