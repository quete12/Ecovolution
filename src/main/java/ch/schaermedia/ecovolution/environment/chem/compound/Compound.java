/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.compound;

import ch.schaermedia.ecovolution.environment.chem.AtmosphericEnity;
import ch.schaermedia.ecovolution.environment.chem.ChemUtilities;
import ch.schaermedia.ecovolution.environment.chem.phasediagram.PhaseDiagram_Energy_Pressure;
import ch.schaermedia.ecovolution.environment.chem.properties.CompoundProperties;

/**
 *
 * @author Quentin
 */
public class Compound extends AtmosphericEnity {

    private final CompoundProperties properties;

    private long amount_mol_buffer;
    private long energy_kj_buffer;
    private Phase phase;

    private boolean phaseChanged = false;

    public Compound(CompoundProperties properties)
    {
        this.properties = properties;
    }

    public long splitTo(Compound other, double percentage)
    {
        long amountDiff = (long) (amount_mol * percentage);
        amount_mol_buffer -= amountDiff;
        long energyDiff = (long) (amount_mol * percentage);
        energy_kj_buffer -= energyDiff;
        other.add(amountDiff, energyDiff);
        return amountDiff;
    }

    public void add(long amount_mol, long energy_kj)
    {
        amount_mol_buffer += amount_mol;
        energy_kj_buffer += energy_kj;
    }

    @Override
    public void updateStats(long externalPressure_kPa, long totalVolume_L)
    {
        amount_mol += amount_mol_buffer;
        energy_kj += energy_kj_buffer;
        PhaseDiagram_Energy_Pressure diag = properties.getEnergy_Pressure_Diagram();
        Phase phaseAt = diag.getPhaseAt(energy_kj, externalPressure_kPa);
        phaseChanged = phaseAt.idx != phase.idx;
        phase = phaseAt;
        temperature_k = diag.getTemperature_K_at(energy_kj, externalPressure_kPa, phase);
        volume_L = ChemUtilities.volume_L(externalPressure_kPa, amount_mol, temperature_k);
        pressure_kPa = ChemUtilities.pressure_kPa(totalVolume_L, amount_mol, temperature_k);
    }

    public boolean hasPhaseChanged()
    {
        return phaseChanged;
    }

    public Phase getPhase()
    {
        return phase;
    }

}
