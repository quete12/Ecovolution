/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem;

/**
 *
 * @author Quentin
 */
public abstract class AtmosphericEnity {
    protected long amount_mol;
    protected long energy_kj;

    protected long temperature_k;
    protected long pressure_kPa;
    protected long volume_L;

    public abstract void updateStats(long externalPressure_kPa, long totalVolume_L);

    public abstract void importBuffers();

    protected void clearStats(){
        amount_mol = 0;
        energy_kj = 0;
        temperature_k = 0;
        pressure_kPa = 0;
        volume_L = 0;
    }

    public long getAmount_mol()
    {
        if(amount_mol <0){
            throw new RuntimeException("Has negative amount");
        }
        return amount_mol;
    }

    public long getEnergy_kj()
    {
        return energy_kj;
    }

    public long getTemperature_k()
    {
        return temperature_k;
    }

    public long getPressure_kPa()
    {
        return pressure_kPa;
    }

    public long getVolume_L()
    {
        return volume_L;
    }
}
