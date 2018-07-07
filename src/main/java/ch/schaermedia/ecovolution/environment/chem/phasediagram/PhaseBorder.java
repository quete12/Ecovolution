/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.chem.phasediagram;

import ch.schaermedia.ecovolution.general.math.LinearFunction;

/**
 *
 * @author Quentin
 */
public abstract class PhaseBorder {
    protected final boolean hasDualFunction;

    public PhaseBorder(boolean hasDualFunction)
    {
        this.hasDualFunction = hasDualFunction;
    }

    public abstract long getMinEnergy_kj_mol(long pressure_kPa);

    protected boolean isSteepFirst(LinearFunction[] functions)
    {
        if (functions.length != 2)
        {
            throw new IllegalArgumentException("isSteepFirst excepts an array with length 2!");
        }
        if (functions[0].isNegative() || functions[1].isNegative())
        {
            throw new IllegalArgumentException("isSteepFirst only works with positive functions!");
        }
        return functions[0].getVarA() > functions[1].getVarA();
    }
}
