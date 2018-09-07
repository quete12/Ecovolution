/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

import ch.schaermedia.ecovolution.math.BigDouble;

/**
 *
 * @author Quentin
 */
public abstract class AtmosphericEnity {

    protected BigDouble[] amount_mol = new BigDouble[2];
    protected BigDouble[] temperature_k = new BigDouble[2];
    protected BigDouble[] pressure_kPa = new BigDouble[2];
    protected BigDouble[] volume_L = new BigDouble[2];
    protected BigDouble[] heatCapacity_kj_K = new BigDouble[2];

    private int internal = 0;
    private int external = 1;

    public abstract void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L);

    protected void clearStats()
    {
        amount_mol.clear();
        temperature_k.clear();
        pressure_kPa.clear();
        volume_L.clear();
        heatCapacity_kj_K.clear();
    }

    protected void swap(){


        if(internal == 0){
            internal = 1;
            external = 0;
        }else{
            internal = 0;
            external = 1;
        }
    }

    BigDouble getAmount_mol()
    {
        return amount_mol[internal];
    }

    BigDouble getTemperature_k()
    {
        return temperature_k[internal];
    }

    BigDouble getPressure_kPa()
    {
        return pressure_kPa[internal];
    }

    BigDouble getVolume_L()
    {
        return volume_L[internal];
    }

    BigDouble getHeatCapacity_kj_K()
    {
        return heatCapacity_kj_K[internal];
    }
}
