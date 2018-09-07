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

    private static final int SWAPSIZE = 2;

    protected final BigDouble amount_mol = new BigDouble();
    protected final BigDouble amount_mol_external = new BigDouble();
    protected final BigDouble[] temperature_k = new BigDouble[SWAPSIZE];
    protected final BigDouble[] pressure_kPa = new BigDouble[SWAPSIZE];
    protected final BigDouble[] volume_L = new BigDouble[SWAPSIZE];
    protected final BigDouble[] heatCapacity_kj_K = new BigDouble[SWAPSIZE];

    protected int internal = 0;
    private int external = 1;

    public AtmosphericEnity()
    {
        for (int i = 0; i < SWAPSIZE; i++)
        {
            temperature_k[i] = new BigDouble();
            pressure_kPa[i] = new BigDouble();
            volume_L[i] = new BigDouble();
            heatCapacity_kj_K[i] = new BigDouble();
        }
    }

    public abstract void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L);

    protected void clearStats()
    {
        amount_mol.clear();
        temperature_k[internal].clear();
        pressure_kPa[internal].clear();
        volume_L[internal].clear();
        heatCapacity_kj_K[internal].clear();
    }

    protected void swap()
    {
        amount_mol_external.set(amount_mol);
        if (internal == 0)
        {
            internal = 1;
            external = 0;
        } else
        {
            internal = 0;
            external = 1;
        }
    }

    public BigDouble getAmount_mol()
    {
        return amount_mol_external;
    }

    public BigDouble getTemperature_k()
    {
        return temperature_k[external];
    }

    public BigDouble getPressure_kPa()
    {
        return pressure_kPa[external];
    }

    public BigDouble getVolume_L()
    {
        return volume_L[external];
    }

    public BigDouble getHeatCapacity_kj_K()
    {
        return heatCapacity_kj_K[external];
    }
}
