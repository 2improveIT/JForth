
// not used

package jforth;


import java.util.*;

public class StringSequence
{
    private ArrayList<String> _list = new ArrayList<> ();

    public StringSequence (StringSequence src)
    {
        _list.addAll(src._list);
    }

    public StringSequence (List<String> list)
    {
        _list = new ArrayList<> (list);
    }

    public StringSequence (String csv)
    {
        this (csv.split(","));
    }

    public StringSequence (String[] in)
    {
        _list.addAll (Arrays.asList (in));
    }

    public String asString ()
    {
        StringBuilder sb = new StringBuilder();
        for (String d : this._list)
        {
            sb.append(d);
        }
        return sb.toString();
    }

    public StringSequence shuffle()
    {
        StringSequence ret = new StringSequence(this);
        Collections.shuffle(ret._list);
        return ret;
    }

    public StringSequence unique ()
    {
        ArrayList<String> nodupe = new ArrayList<>(new LinkedHashSet<> (_list));
        return new StringSequence(nodupe);
    }

    public StringSequence intersect (StringSequence other)
    {
        StringSequence ret = new StringSequence(this);
        ret._list.retainAll(other._list);
        return ret;
    }

    public static StringSequence mixin (StringSequence d1, StringSequence d2)
    {
        int len = Math.max (d1.length(),d2.length());
        ArrayList<String> ar = new ArrayList<>();
        for (int s=0; s<len; s++)
        {
            if (s < d1.length())
                ar.add (d1.pick(s));
            if (s < d2.length())
                ar.add (d2.pick(s));
        }
        return new StringSequence(ar);
    }

    public int length ()
    {
        return _list.size ();
    }

    public void put (int x, String s)
    {
        _list.add (x, s);
    }

    public String pick (int x)
    {
        return _list.get(x);
    }

    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder ();
            sb.append ('{');
            for (int x = 0; x <_list.size(); x++)
            {
                sb.append (_list.get(x));
                if (x != _list.size()-1)
                    sb.append (",");
            }
            sb.append ('}');
        return sb.toString ();
    }

    public static StringSequence parseSequence (String in)
    {
        String seq = Utilities.extractSequence (in);
        if (seq == null)
            return null;
        return new StringSequence (seq);
    }

    public StringSequence rotateLeft (int n)
    {
        StringSequence ret = new StringSequence(this);
        ret._list = Utilities.rotateLeft (ret._list, n);
        return ret;
    }

    public StringSequence rotateRight (int n)
    {
        StringSequence ret = new StringSequence(this);
        ret._list = Utilities.rotateRight (ret._list, n);
        return ret;
    }

    public static void main (String[] args)
    {
        StringSequence sl = parseSequence ("{das,ist,toll}");
        System.out.println (sl);
    }
}
