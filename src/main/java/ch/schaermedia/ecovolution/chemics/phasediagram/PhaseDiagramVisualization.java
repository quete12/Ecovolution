/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.chemics.ChemUtilities;
import ch.schaermedia.ecovolution.chemics.atmospherics.CompoundProperties;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class PhaseDiagramVisualization extends PApplet{
    private static final int FRAMERATE = 60;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            ChemUtilities.readElements("res/Chemics.json");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(PhaseDiagramVisualization.class.getName()).log(Level.SEVERE, null, ex);
        }
        PApplet.main(PhaseDiagramVisualization.class,args);
    }
    @Override
    public void mouseWheel(MouseEvent event)
    {
        scale += event.getCount()*0.1;
    }
    private PGraphics diagram;
    private float scale = 1.0f;

    @Override
    public void draw()
    {

        background(255);
        CompoundProperties prop = CompoundProperties.getPropertiesFromCode("CO2");

        diagram.beginDraw();
        diagram.noFill();
        diagram.background(255);
        diagram.strokeWeight(2);
        prop.getEnergy_Pressure_Diagram().render(diagram);
        diagram.endDraw();



        scale(scale);
        image(diagram, 0, 0);
    }
    @Override
    public void setup()
    {
        setupWindow();
        setupDiagram();
    }
    private void setupWindow()
    {
        surface.setResizable(true);
        surface.setTitle("Diagram view");
        surface.setFrameRate(FRAMERATE);
    }

    private void setupDiagram(){
        diagram = createGraphics(width, height, P2D);
    }

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

}
