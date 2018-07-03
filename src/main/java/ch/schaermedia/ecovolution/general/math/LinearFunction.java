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

    private final long varA;
    private final long varB;

    public LinearFunction(long p1x, long p1y, long p2x, long p2y)
    {
        this.varA = findVarA(p1x, p1y, p2x, p2y);
        this.varB = findVarB(varA, p2x, p2y);
    }

    public LinearFunction(long varA, long varB)
    {
        this.varA = varA;
        this.varB = varB;
    }

    public LinearFunction shiftLeft(long n)
    {
        long newB = varB + varA * n;
        return new LinearFunction(varA, newB);
    }

    public LinearFunction shiftRight(long n)
    {
        long newB = varB - varA * n;
        return new LinearFunction(varA, newB);
    }

    public long y(long x)
    {
        return varA * x + varB;
    }

    public long x(long y)
    {
        return (y - varB) / varA;
    }

    public boolean isPointLeft(long px, long py)
    {
        if (varA > 0)
        {
            return y(px) < py;
        } else
        {
            return y(px) > py;
        }
    }

    public boolean isPointOnOrOver(long px, long py)
    {
        if (varA > 0)
        {
            return y(px) <= py;
        } else
        {
            return y(px) >= py;
        }
    }

    private long findVarB(long varA, long px, long py)
    {
        return py - (varA * px);
    }

    private long findVarA(long p1X, long p1Y, long p2X, long p2Y)
    {
        return (p2Y - p1Y) / (p2X - p1X);
    }

    @Override
    public String toString()
    {
        return "f(x) = " + varA + "x + " + varB;
    }

    @Override
    public boolean isPositive()
    {
        return varA > 0;
    }

    public long getVarA()
    {
        return varA;
    }

    public long getVarB()
    {
        return varB;
    }

    @Override
    public boolean isNegative()
    {
        return varA < 0;
    }
}
