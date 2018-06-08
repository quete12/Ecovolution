/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.basic.Tile;
import ch.schaermedia.ecovolution.environment.chem.Compound;
import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileRenderer {

    public void render(Tile tile, PGraphics g)
    {
        g.noStroke();
        CompoundMix mix = tile.getMixAtLayer(1);
        renderCo2Phase(mix, g);
        g.rect(tile.getX() * tile.getWidth(), tile.getY() * tile.getHeight(), tile.getWidth(), tile.getHeight());
    }

    private void renderCo2Phase(CompoundMix mix, PGraphics g){
        if(mix == null){
            g.fill(0);
            return;
        }
        Compound[] phasesByCode = mix.getPhasesByCode("CO2");
        if(phasesByCode == null){
            g.fill(0);
            return;
        }
        float red = 0;
        float green = 0;
        float blue = 0;
        if(phasesByCode[0] != null && phasesByCode[0].getAmount_mol() > 0){
            green = 255;
        }
        if(phasesByCode[1] != null && phasesByCode[1].getAmount_mol() > 0){
            blue = 255;
        }
        if(phasesByCode[2] != null && phasesByCode[2].getAmount_mol() > 0){
            red = 255;
        }
        g.fill(red, green, blue);
    }

    private void renderTemperature(CompoundMix mix, PGraphics g)
    {
        double temperature_K = mix.getTemperature_K();
        if(Double.isNaN(temperature_K)){
            System.out.println("INVALID Temperature");
        }
        double percent = temperature_K / 500;
        if(percent>1.0){
            percent = 1.0;
        }
        g.fill((float) percent * 255.0f, 0, 0);

    }

    private void renderVolumeGrayScale(CompoundMix mix, PGraphics g)
    {
        double volume = mix.getVolume_L();
        if (volume < 0)
        {
            g.color(255, 0, 0);
        } else
        {
            g.fill((float) (volume / CompoundMix.STATIC_VOLUME_L) * 255.0f);
        }
    }
}
