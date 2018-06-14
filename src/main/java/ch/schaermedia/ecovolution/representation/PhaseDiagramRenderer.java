/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import ch.schaermedia.ecovolution.environment.chem.CompoundProperties;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class PhaseDiagramRenderer {

    private static final int SCALE_Y = 20;
    private static final int SCALE_X = 1;

    private final CompoundProperties properties;

    public PhaseDiagramRenderer(CompoundProperties properties)
    {
        this.properties = properties;
    }

    public void render(PGraphics g)
    {
        float tpH = (float) properties.getTriplePointHeat_K() / SCALE_X;
        float tpP = (float) (g.height - properties.getTriplePointPressure_kPa() / SCALE_Y);

        float meltH = (float) properties.getMeltingPoint_K() / SCALE_X;
        float staticP = (float) (g.height - CompoundMix.STATIC_PRESSURE_kPa / SCALE_Y);

        float cpH = (float) properties.getCriticalPointHeat_K() / SCALE_X;
        float cpP = (float) (g.height - properties.getCriticalPointPressure_kPa() / SCALE_Y);

        g.beginDraw();
        g.beginPGL();

        g.background(255);
        g.strokeWeight(3);

        //Gas
        g.beginShape();
        g.fill(125);
        g.vertex(0, g.height);
        g.vertex(tpH, tpP);
        g.vertex(cpH, cpP);
        g.vertex(g.width, cpP);
        g.vertex(g.width, g.height);
        g.endShape();
        //Solid
        g.beginShape();
        g.fill(255,0,0);
        g.vertex(0, g.height);
        g.vertex(tpH, tpP);
        float solidX = findX(g.height, meltH, staticP, tpH, tpP);
        if(solidX > cpH){
            solidX = cpH;
        }
        g.vertex(solidX, 0);
        g.vertex(0, 0);
        g.endShape();
        //Liquid
        g.beginShape();
        g.fill(0,0,255);
        g.vertex(tpH, tpP);
        g.vertex(cpH, cpP);
        g.vertex(cpH, 0);
        g.vertex(solidX, 0);
        g.endShape();
        //supercritical fluid
        g.beginShape();
        g.fill(0,255,255);
        g.vertex(cpH, cpP);
        g.vertex(g.width, cpP);
        g.vertex(g.width, 0);
        g.vertex(cpH, 0);
        g.endShape();

        g.endPGL();
        g.endDraw();
    }

    private float findY(float varA, float varB, float x){
        return varA*x+varB;
    }

    private float findY(float x, float p1X, float p1Y, float p2X, float p2Y){
        float varA = findVarA(p1X, p1Y, p2X, p2Y);
        float varB = findVarB(varA, p2X, p2Y);
        return findY(varA, varB, x);
    }

    private float findX(float varA, float varB, float y){
        return (y-varB)/varA;
    }

    private float findX(float y, float p1X, float p1Y, float p2X, float p2Y){
        float varA = findVarA(p1X, p1Y, p2X, p2Y);
        float varB = findVarB(varA, p2X, p2Y);
        return findX(varA, varB, y);
    }

    private float findVarB(float varA, float px, float py){
        return py-(varA*px);
    }

    private float findVarA(float p1X, float p1Y, float p2X, float p2Y)
    {
        //invert var A because of height inversion
        return ((p2Y - p1Y) / (p2X - p1X))*-1;
    }
}
