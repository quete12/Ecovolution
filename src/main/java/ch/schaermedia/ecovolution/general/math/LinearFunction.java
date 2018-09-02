/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

/**
 *
 * @author Quentin
 */
public class LinearFunction implements Function {

    private final BigDouble varA;
    private final BigDouble varB;

    public LinearFunction(BigDouble p1x, BigDouble p1y, BigDouble p2x, BigDouble p2y)
    {
        this.varA = findVarA(p1x, p1y, p2x, p2y);
        this.varB = findVarB(varA, p2x, p2y);
    }

    public LinearFunction(BigDouble varA, BigDouble varB)
    {
        this.varA = varA;
        this.varB = varB;
    }

    public BigDouble y(BigDouble x)
    {
        return varA.mul(x, new BigDouble()).add(varB);
    }

    public BigDouble x(BigDouble y)
    {
        return y.sub(varB, new BigDouble()).div(varA);
    }

    public boolean isPointLeft(BigDouble px, BigDouble py)
    {
        if (varA.isPositive())
        {
            return y(px).compareTo(py) < 0;
        } else
        {
            return y(px).compareTo(py) > 0;
        }
    }

    public boolean isPointOnOrOver(BigDouble px, BigDouble py)
    {
        if (varA.isPositive())
        {
            return y(px).compareTo(py) <= 0;
        } else
        {
            return y(px).compareTo(py) >= 0;
        }
    }

    private BigDouble findVarB(BigDouble varA, BigDouble px, BigDouble py)
    {
        return py.sub(varA.mul(px, new BigDouble()), new BigDouble());
    }

    private BigDouble findVarA(BigDouble p1X, BigDouble p1Y, BigDouble p2X, BigDouble p2Y)
    {
        try
        {
            return p2Y.sub(p1Y, new BigDouble()).div(p2X.sub(p1X, new BigDouble()));
        } catch (Exception ex)
        {
            System.out.println("p1: " + p1X + "|" + p1Y + " p2: " + p2X + "|" + p2Y);
            throw ex;
        }
    }

    @Override
    public String toString()
    {
        return "f(x) = " + varA + "x + " + varB;
    }

    @Override
    public boolean isPositive()
    {
        return varA.isPositive();
    }

    public BigDouble getVarA()
    {
        return varA;
    }

    public BigDouble getVarB()
    {
        return varB;
    }

    @Override
    public boolean isNegative()
    {
        return varA.isNegative();
    }
}
