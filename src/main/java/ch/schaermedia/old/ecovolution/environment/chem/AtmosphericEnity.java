/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.environment.chem;

import ch.schaermedia.old.ecovolution.general.math.BigDouble;

/**
 *
 * @author Quentin
 */
public abstract class AtmosphericEnity {

    protected BigDouble amount_mol = new BigDouble();
    protected BigDouble energy_kj = new BigDouble();

    protected BigDouble temperature_k = new BigDouble();
    protected BigDouble pressure_kPa = new BigDouble();
    protected BigDouble volume_L = new BigDouble();

    protected BigDouble heatCapacity_kj_K = new BigDouble();

    public abstract void updateStats(BigDouble externalPressure_kPa, BigDouble totalVolume_L);

    protected void clearStats()
    {
        amount_mol.clear();
        energy_kj.clear();
        temperature_k.clear();
        pressure_kPa.clear();
        volume_L.clear();
        heatCapacity_kj_K.clear();
    }

    public BigDouble getAmount_mol()
    {
        return amount_mol;
    }

    public BigDouble getEnergy_kj()
    {
        return energy_kj;
    }

    public BigDouble getTemperature_k()
    {
        return temperature_k;
    }

    public BigDouble getPressure_kPa()
    {
        return pressure_kPa;
    }

    public BigDouble getVolume_L()
    {
        return volume_L;
    }

    public BigDouble getHeatCapacity_kj_K()
    {
        return heatCapacity_kj_K;
    }
}
