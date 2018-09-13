/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

/**
 *
 * @author Quentin
 */
public class LinearFunction implements Function {

    private final BigDouble varA;
    private final BigDouble varB;

    private BigDouble minX = null;
    private BigDouble maxX = null;
    private BigDouble minY = null;
    private BigDouble maxY = null;

    public LinearFunction(BigDouble p1x, BigDouble p1y, BigDouble p2x, BigDouble p2y)
    {
        this.varA = findVarA(p1x, p1y, p2x, p2y);
        this.varB = findVarB(varA, p1x, p1y);
    }

    public LinearFunction(BigDouble p1x, BigDouble p1y, BigDouble p2x, BigDouble p2y, boolean isLimiting)
    {
        this(p1x, p1y, p2x, p2y);
        if (isLimiting)
        {
            this.minX = BigDouble.min(p1x, p2x);
            this.maxX = BigDouble.max(p1x, p2x);
            this.minY = BigDouble.min(p1y, p2y);
            this.maxY = BigDouble.max(p1y, p2y);
        }
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
        if (!isWithinLimits(px, py))
        {
            return false;
        }
        if (varA.isPositive())
        {
            return y(px).compareTo(py) < 0;
        } else
        {
            return y(px).compareTo(py) > 0;
        }
    }
    public boolean isPointOnOrLeft(BigDouble px, BigDouble py)
    {
        if (!isWithinLimits(px, py))
        {
            return false;
        }
        if (varA.isPositive())
        {
            return y(px).compareTo(py) <= 0;
        } else
        {
            return y(px).compareTo(py) >= 0;
        }
    }

    public boolean isPointOnOrOver(BigDouble px, BigDouble py)
    {
        if (!isWithinLimits(px, py))
        {
            return false;
        }
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
        BigDouble x = varA.mul(px, new BigDouble());
        return py.sub(x, new BigDouble());
    }

    private BigDouble findVarA(BigDouble p1X, BigDouble p1Y, BigDouble p2X, BigDouble p2Y)
    {
        try
        {
            BigDouble yDiff = p2Y.sub(p1Y, new BigDouble());
            BigDouble xDiff = p2X.sub(p1X, new BigDouble());
            System.out.println(yDiff.toDouble() + " / " + xDiff.toDouble());
            return yDiff.div(xDiff);
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

    @Override
    public void limitMinX(BigDouble minX)
    {
        this.minX = minX;
    }

    @Override
    public void limitMaxX(BigDouble maxX)
    {
        this.maxX = maxX;
    }

    @Override
    public void limitMinY(BigDouble minY)
    {
        this.minY = minY;
    }

    @Override
    public void limitMaxY(BigDouble maxY)
    {
        this.maxY = maxY;
    }

    @Override
    public boolean isWithinLimits(BigDouble x, BigDouble y)
    {
        if (minX != null && minX.compareTo(x) >= 0)
        {
            return false;
        }
        if (maxX != null && maxX.compareTo(x) <= 0)
        {
            return false;
        }
        if (minY != null && minY.compareTo(y) >= 0)
        {
            return false;
        }
        if (maxY != null && maxY.compareTo(y) <= 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LinearFunction))
        {
            return false;
        }
        LinearFunction other = (LinearFunction) obj;
        return other.getVarA().compareTo(varA) == 0 && other.getVarB().compareTo(varB) == 0;
    }
}
