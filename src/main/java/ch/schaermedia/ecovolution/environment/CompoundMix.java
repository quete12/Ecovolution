/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class CompoundMix {

    public static final double STATIC_PRESSURE_kPa = 101.325;
    public static final double STATIC_VOLUME_L = 10000;

    private final List<Compound> mix;

    private double heatCapacitySum;
    private double amount_mol;
    private double volume_L;
    private double pressure_kPa;

    public CompoundMix() {
        this.mix = new ArrayList<>();
    }

    public void spread(CompoundMix[] layer, CompoundMix higher, CompoundMix lower) {
        boolean hasLower = lower != null;
        boolean hasHigher = higher != null;
    }

    private void spreadToLower(CompoundMix lower){
        if(lower.getPressure_kPa() < STATIC_PRESSURE_kPa){
            List<Compound>[] byPhases = byPhases();
            List<Compound> solids = byPhases[0];

        }
    }

    private List<Compound>[] byPhases(){
        List<Compound>[] phased = new List[3];
        for (int i = 0; i < phased.length; i++) {
           phased[i] = new ArrayList<>();
        }
        for (Compound compound : mix) {
            switch(compound.getPhase()){
                case SOLID:
                    phased[0].add(compound);
                    break;
                case LIQUID:
                    phased[1].add(compound);
                    break;
                case GAS:
                    phased[2].add(compound);
                    break;
                default:
                    throw new AssertionError(compound.getPhase().name());
            }
        }
        return phased;
    }

    private List<Compound> solids(){
        ArrayList<Compound> result = new ArrayList<Compound>();
        mix.stream().filter((compound) -> (compound.getPhase() == Phase.SOLID)).forEachOrdered((compound) -> {
            result.add(compound);
        });
        return result;
    }

    private List<Compound> liquides(){
        ArrayList<Compound> result = new ArrayList<Compound>();
        mix.stream().filter((compound) -> (compound.getPhase() == Phase.LIQUID)).forEachOrdered((compound) -> {
            result.add(compound);
        });
        return result;
    }

    private List<Compound> gases(){
        ArrayList<Compound> result = new ArrayList<Compound>();
        mix.stream().filter((compound) -> (compound.getPhase() == Phase.GAS)).forEachOrdered((compound) -> {
            result.add(compound);
        });
        return result;
    }

    public void addEnergy(double energy_kj) {
        mix.forEach((compound) -> {
            double percentage = heatCapacitySum / compound.getTotalHeatCapacity();
            double adding = energy_kj * percentage;
            compound.addEnergy(adding);
        });
    }

    public void update() {
        amount_mol = 0;
        heatCapacitySum = 0;
        volume_L = 0;
        pressure_kPa = 0;
        mix.forEach((compound) -> {
            compound.update();
            amount_mol += compound.getAmount_mol();
            heatCapacitySum += compound.getTotalHeatCapacity();
            volume_L += compound.volume_L(STATIC_PRESSURE_kPa);
            pressure_kPa += compound.pressure_kPa(STATIC_VOLUME_L);
        });
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
