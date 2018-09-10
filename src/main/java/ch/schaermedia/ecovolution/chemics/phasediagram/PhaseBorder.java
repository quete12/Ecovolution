/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.phasediagram;

import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.math.LinearFunction;

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

    public abstract BigDouble getMinEnergy_kj_mol(BigDouble pressure_kPa);

    protected boolean isSteepFirst(LinearFunction[] functions)
    {
        if (functions.length != 2)
        {
            throw new IllegalArgumentException("isSteepFirst excepts an array with length 2!");
        }
        return functions[0].getVarA().abs().compareTo(functions[1].getVarA().abs()) > 0;
    }

}
