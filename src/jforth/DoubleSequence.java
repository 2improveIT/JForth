package jforth;

import jforth.scalacode.MyMath;
import jforth.scalacode.SieveOfEratosthenes;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import scala.math.BigInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.DoubleStream;


/**
 * Created by Administrator on 3/23/2017.
 */
public class DoubleSequence
{
    private ArrayList<Double> mem = new ArrayList<>();

    public DoubleSequence()
    {

    }

    public DoubleSequence (String s)
    {
        for(char c : s.toCharArray())
        {
            mem.add ((double)c);
        }
    }

    public DoubleSequence (String[] values)
    {
        for (int s=0; s<values.length; s++)
        {
            try
            {
                mem.add(Double.parseDouble(values[s]));
            }
            catch (Exception unused)
            {

            }
        }
    }

    public DoubleSequence (List<Double> list)
    {
        mem = new ArrayList<Double>(list);
    }

    public DoubleSequence (double ... vals)
    {
        for (int s=0; s<vals.length; s++)
        {
            mem.add (vals[s]);
        }
    }

    public DoubleSequence (int ... vals)
    {
        for (int s=0; s<vals.length; s++)
        {
            mem.add ((double)vals[s]);
        }
    }

    public DoubleSequence (DoubleSequence src)
    {
        for (int s=0; s<src.mem.size(); s++)
        {
            mem.add (src.mem.get(s));
        }
    }

    public DoubleSequence (DoubleSequence src1, DoubleSequence src2)
    {
        mem.addAll(src1.mem);
        mem.addAll(src2.mem);
    }

    public static DoubleSequence makeCounted (double start, long howmuch, double step)
    {
        DoubleStream ds = DoubleStream.iterate(start, n -> n + step).limit(howmuch);
        return new DoubleSequence (ds.toArray());
    }

    public static DoubleSequence makeBits (long in)
    {
        DoubleSequence out = new DoubleSequence();
        do
        {
            if (in % 2 == 0)
            {
                out.mem.add(0.0);
            }
            else
            {
                out.mem.add(1.0);
            }
            in /= 2;
        } while (in != 0);
        return out.reverse();
    }

    public static DoubleSequence primes (long in)
    {
        return primes (BigInt.apply(in));
    }

    public static DoubleSequence primes (BigInt in)
    {
        DoubleSequence out = new DoubleSequence();
        List<BigInt> list = MyMath.toJList(SieveOfEratosthenes.factors(in));
        for (BigInt i : list)
        {
            out.mem.add (i.toDouble());
        }

        return out;
    }


    public static DoubleSequence parseSequence (String in)
    {
        if (in.length() < 2)
            return null;
        if (in.charAt(0) == '{' && in.charAt(in.length()-1) == '}')
        {
            in = in.substring(1, in.length()-1);
            String vals[] = in.split(",");
            return new DoubleSequence(vals);
        }
        return null;
    }

    public double sum()
    {
        return new Sum().evaluate (this.asPrimitiveArray());
    }

    public double[] asPrimitiveArray ()
    {
        double[] out = new double[mem.size()];
        for (int s=0; s<mem.size(); s++)
            out[s] = mem.get(s);
        return out;
    }

    public double sumQ()
    {
        return new SumOfSquares().evaluate (this.asPrimitiveArray());
    }

    public double prod()
    {
        return new Product().evaluate (this.asPrimitiveArray());
    }

    public DoubleSequence apply (PolynomialFunction p)
    {
        DoubleSequence ret = new DoubleSequence();
        for (double d : mem)
        {
            ret.mem.add(p.value(d));
        }
        return ret;
    }

    public DoubleSequence add (DoubleSequence other)
    {
        DoubleSequence ds = new DoubleSequence(this);
        ds.mem.addAll(other.mem);
        return ds;
    }

    public boolean isEmpty()
    {
        return mem.isEmpty();
    }

    public DoubleSequence reverse()
    {
        DoubleSequence ret = new DoubleSequence(this);
        Collections.reverse(ret.mem);
        return ret;
    }

    public Double pick (int i)
    {
        return mem.get(i);
    }

    public DoubleSequence shuffle()
    {
        DoubleSequence ret = new DoubleSequence(this);
        Collections.shuffle(ret.mem);
        return ret;
    }

    public DoubleSequence sort()
    {
        DoubleSequence ret = new DoubleSequence(this);
        Collections.sort (ret.mem);
        return ret;
    }

    public DoubleSequence intersect (DoubleSequence other)
    {
        DoubleSequence ret = new DoubleSequence(this);
        ret.mem.retainAll(other.mem);
        return ret;
    }

    public DoubleSequence difference (DoubleSequence other)
    {
        DoubleSequence ret = new DoubleSequence(this);
        ret.mem.removeAll(other.mem);
        return ret;
    }

    public DoubleSequence rotateLeft (int n)
    {
        DoubleSequence ret = new DoubleSequence(this);
        while (n != 0)
        {
            ret.mem.add(ret.mem.size(), 0.0);
            ret.mem.remove(0);
            n--;
        }
        return ret;
    }

    public DoubleSequence rotateRight (int n)
    {
        DoubleSequence ret = new DoubleSequence(this);
        while (n != 0)
        {
            ret.mem.add(0, 0.0);
            ret.mem.remove(ret.mem.size()-1);
            n--;
        }
        return ret;
    }

    public int length()
    {
        return mem.size();
    }

    public DoubleSequence unique ()
    {
        ArrayList<Double> nodupe = new ArrayList<>(new LinkedHashSet<>(mem));
        return new DoubleSequence(nodupe);
    }

    public DoubleSequence subList (int from, int to)
    {
        return new DoubleSequence(this.mem.subList(from, to));
    }

    public DoubleSequence rearrange (int pos[])
    {
        DoubleSequence out = new DoubleSequence();
        for (int p : pos)
        {
            out = out.add(this.mem.get(p));
        }
        return out;
    }

    public DoubleSequence add (double d)
    {
        DoubleSequence ds = new DoubleSequence(this);
        ds.mem.add(d);
        return ds;
    }

    public String asString ()
    {
        StringBuilder sb = new StringBuilder();
        for (Double d : this.mem)
        {
            sb.append((char)d.intValue());
        }
        return sb.toString();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String str;
        sb.append('{');
        if (mem.size() > 0)
        {
            for (int s = 0; s < mem.size() - 1; s++)
            {
                str = Utilities.removeTrailingZero(mem.get(s));
                sb.append(str).append(',');
            }
            str = Utilities.removeTrailingZero(mem.get(mem.size() - 1));
            sb.append(str);
        }
        sb.append('}');
        return sb.toString();
    }
}
