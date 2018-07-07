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

    private final double varA;
    private final double varB;

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

    public long y(long x)
    {
        return (long) (varA * x + varB);
    }

    public long x(long y)
    {
        return (long) ((y - varB) / varA);
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

    private double findVarB(double varA, long px, long py)
    {
        return py - (varA * px);
    }

    private double findVarA(long p1X, long p1Y, long p2X, long p2Y)
    {
        try
        {
            return (double)(p2Y - p1Y) / (double)(p2X - p1X);
        }
        catch (Exception ex)
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
        return varA > 0;
    }

    public double getVarA()
    {
        return varA;
    }

    public double getVarB()
    {
        return varB;
    }

    @Override
    public boolean isNegative()
    {
        return varA < 0;
    }
}
