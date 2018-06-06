/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class CompoundMix {

    public static final double STATIC_PRESSURE_kPa = 101.325;
    public static final double STATIC_VOLUME_L = 10000;

    // Map<code, Compound[phase_idx]>
    private final Map<String, Compound[]> mix;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;
    private double temperature_K;

    public CompoundMix() {
        this.mix = new HashMap<>();
    }

    public void spread(CompoundMix[] layer, CompoundMix higher, CompoundMix lower) {
        boolean hasLower = lower != null;
        boolean hasHigher = higher != null;

    }

    public void addEnergy(double energy_kj){
        for (Compound[] value : mix.values()) {
            for (Compound compound : value) {
                if(compound == null){
                    continue;
                }
                double percent = compound.getSpecificHeatCapacity()/heatCapacitySum;
                compound.addEnergy(energy_kj*percent);
            }
        }
    }
    /**
     * Tries to fill the lower mix untill lower mix has reached StaticPressure (1 atm)
     * @param lower
     */
    private void spreadToLower(CompoundMix lower) {
        if (lower.getPressure_kPa() < STATIC_PRESSURE_kPa) {
            //TODO: solids fall down
            //TODO: liquids rain down
            //TODO: if there is still space in mix below fill with Gases
            //for now we just take a percentage of each compound and phase
            double molesToPressurize = lower.molesToPressurize();
            double percentage = molesToPressurize / amount_mol;
            if(percentage <=0){
                return;
            }
            if(percentage > 1){
                percentage = 1.0;
            }
            for (Map.Entry<String, Compound[]> entry : mix.entrySet()) {
                String key = entry.getKey();
                Compound[] value = entry.getValue();
                for (int i = 0; i < value.length; i++) {
                    Compound compound = value[i];
                    if(compound == null){
                        continue;
                    }
                    lower.add(key, i, compound.splitMoles(percentage), compound.splitEnergy(percentage));
                }
            }
        }
    }

    public void add(String code, int phase, double amount_mol, double energy_kj){
        //TODO add a compound
    }
    /**
     * If the mixture takes up more volume than StaticVolume excess volume flows to higher mixture
     * @param higher
     */
    private void spreadToHigher(CompoundMix higher){
        if(volume_L > STATIC_VOLUME_L){

        }
    }

    public void update() {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        int compounds = 0;
        double temperatureSum = 0;
        for (Compound[] cl : mix.values()) {
            for (int i = 0; i < cl.length; i++) {
                Compound compound = cl[i];
                if (compound == null) {
                    continue;
                }
                compound.update();
                int phaseIdx = compound.getPhase().idx;
                if (phaseIdx != i) {
                    if (cl[phaseIdx] == null) {
                        cl[phaseIdx] = compound;
                    } else {
                        cl[phaseIdx].importCompound(compound);
                    }
                    cl[i] = null;
                }
                amount_mol += compound.getAmount_mol();
                heatCapacitySum += compound.getTotalHeatCapacity();
                //the sum of all partial volumes and pressures equal the total volume and pressure
                volume_L += compound.volume_L(STATIC_PRESSURE_kPa);
                pressure_kPa += compound.pressure_kPa(STATIC_VOLUME_L);

                temperatureSum += compound.getTemperature_K();
                compounds++;
            }
        }
        //for now we average the temperature of all individual compounds to get the mixture temperature
        temperature_K = temperatureSum / compounds;
    }

    public double molesToPressurize(){
        double diffPressure = STATIC_PRESSURE_kPa-pressure_kPa;
        if(diffPressure < 0){
            return 0;
        }
        return ChemUtilities.moles(diffPressure, volume_L, temperature_K);
    }

    public double getAmount_mol() {
        return amount_mol;
    }

    public double getVolume_L() {
        return volume_L;
    }

    public double getPressure_kPa() {
        return pressure_kPa;
    }

}
