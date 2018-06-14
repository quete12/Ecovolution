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

    public enum ShowDetail {
        PHASE,
        TEMPERATURE,
        VOLUME;
    }

    private final int layer;
    private final ShowDetail showDetail;
    private final String compoundCode;

    public TileRenderer()
    {
        this.layer = 0;
        this.showDetail = ShowDetail.VOLUME;
        this.compoundCode = null;
    }

    public TileRenderer(int layer, ShowDetail showDetail)
    {
        this.layer = layer;
        this.showDetail = showDetail;
        if(showDetail == ShowDetail.PHASE){
            throw new RuntimeException("No Argument for compound code provided for rendering Phase!");
        }
        this.compoundCode = null;
    }

    public TileRenderer(int layer, ShowDetail showDetail, String compoundCode)
    {
        this.layer = layer;
        this.showDetail = showDetail;
        this.compoundCode = compoundCode;
    }

    public void render(Tile tile, PGraphics g)
    {
        g.noStroke();
        CompoundMix mix = tile.getMixAtLayer(layer);
        switch (showDetail)
        {
            case PHASE:
                renderPhase(compoundCode, mix, g);
                break;
            case TEMPERATURE:
                renderTemperature(mix, g);
                break;
            case VOLUME:
                renderVolumeGrayScale(mix, g);
                break;
            default:
                throw new AssertionError(showDetail.name());

        }
        g.rect(tile.getX() * tile.getWidth(), tile.getY() * tile.getHeight(), tile.getWidth(), tile.getHeight());
    }

    private void renderPhase(String code, CompoundMix mix, PGraphics g)
    {
        if (mix == null)
        {
            g.fill(0);
            return;
        }
        Compound[] phase = mix.getPhasesByCode(code);
        if (phase == null)
        {
            g.fill(0);
            return;
        }
        double total_mol = 0;
        for (Compound compound : phase)
        {
            if (compound == null)
            {
                continue;
            }
            total_mol += compound.getAmount_mol();
        }
        float red = 0;
        float green = 0;
        float blue = 0;
        if (phase[0] != null && phase[0].getAmount_mol() > 0)
        {
            green = (float) (phase[0].getAmount_mol() / total_mol) * 255;
        }
        if (phase[1] != null && phase[1].getAmount_mol() > 0)
        {
            blue = (float) (phase[1].getAmount_mol() / total_mol) * 255;
        }
        if (phase[2] != null && phase[2].getAmount_mol() > 0)
        {
            red = (float) (phase[2].getAmount_mol() / total_mol) * 255;
        }
        g.fill(red, green, blue);
    }

    private void renderTemperature(CompoundMix mix, PGraphics g)
    {
        double temperature_K = mix.getTemperature_K();
        if (Double.isNaN(temperature_K))
        {
            System.out.println("INVALID Temperature");
        }
        double percent = temperature_K / 500;
        if (percent > 1.0)
        {
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
