/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

import processing.core.PGraphics;

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
        this.varB = findVarB(varA, p2x, p2y);
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
        return py.sub(varA.mul(px, new BigDouble()), new BigDouble());
    }

    private BigDouble findVarA(BigDouble p1X, BigDouble p1Y, BigDouble p2X, BigDouble p2Y)
    {
        try
        {
            return p2Y.sub(p1Y, new BigDouble()).div(p2X.sub(p1X, new BigDouble()));
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
    private int timesRendered = 0;

    @Override
    public void render(PGraphics g, BigDouble maxYValue, BigDouble maxXValue)
    {
        BigDouble minW = new BigDouble();
        if (minX != null)
        {
            minW = BigDouble.max(minW, minX);
        }
        BigDouble maxW = new BigDouble(maxXValue);
        if (maxX != null)
        {
            maxW = BigDouble.min(maxW, maxX);
        }

        BigDouble bx1 =minW.div(maxXValue, new BigDouble()).mul(g.width, 0);
        float x1 = (float) bx1.toDouble();
        BigDouble by1 = y(minW).div(maxYValue).mul(g.height, 0);
        float y1 = (float) (g.height - by1.toDouble());
        BigDouble bx2 = maxW.div(maxXValue, new BigDouble()).mul(g.width, 0);
        float x2 = (float) bx2.toDouble();
        BigDouble by2 = y(maxW).div(maxYValue).mul(g.height, 0);
        float y2 = (float) (g.height - by2.toDouble());

//        if (y1 < 0)
//        {
//            y1 = g.height;
//            BigDouble minH = new BigDouble();
//            if (minY != null)
//            {
//                minH = BigDouble.max(minH, minY);
//            }
//            x1 = (float) x(minH).div(maxXValue, new BigDouble()).mul(g.width, 0).toDouble();
//        }
//        if (y2 < 0)
//        {
//            y2 = g.height;
//            BigDouble maxH = new BigDouble();
//            if (maxY != null)
//            {
//                maxH = BigDouble.min(maxH, maxY);
//            }
//            x1 = (float) x(maxH).div(maxXValue, new BigDouble()).mul(g.width, 0).toDouble();
//        }
        g.line(x1, y1, x2, y2);

        g.text("x:"+bx1.toDouble()+" y:"+by1.toDouble(),x1+30,y1);
        g.text("x:"+bx2.toDouble()+" y:"+by2.toDouble(),x2+30,y2);
//        g.fill(255);
//        g.textSize(40);
//        String text="P1(" + x1 + "|" + y1 + ") P2(" + x2 + "|" + y2 + ")";
//        g.text(text, 1200, 100 + (timesRendered % 4) * 50);
//        System.out.println(text);
//        g.noFill();
//        timesRendered++;
    }
}
