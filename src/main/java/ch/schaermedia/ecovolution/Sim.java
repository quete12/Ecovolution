/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.environment.basic.Tile;
import ch.schaermedia.ecovolution.environment.basic.TileGenerator;
import ch.schaermedia.ecovolution.environment.basic.World;
import ch.schaermedia.ecovolution.representation.WorldRenderer;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class Sim extends PApplet {

    private World world;
    private WorldRenderer renderer;

    @Override
    public void settings() {
        size(1200, 800, P2D);
    }

    @Override
    public void draw() {
        background(255);
        PGraphics graph = createGraphics(200, 200, P2D);
        renderer.render(world, graph);
        image(graph, 0, 0);
    }

    @Override
    public void setup() {
        windowSetup();
        worldSetup();
        rendererSetup();
    }

    private void rendererSetup() {
        renderer = new WorldRenderer();
    }

    private void windowSetup() {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(60);
    }

    public static void main(String[] args) {
        PApplet.main(Sim.class, args);
    }

    private void worldSetup() {
        world = new World(20, 20, 3, new TGenerator());
    }

    private class TGenerator implements TileGenerator {

        @Override
        public Tile generate(int x, int y, float size) {
            return new Tile(size, size, x, y);
        }

    }

}
