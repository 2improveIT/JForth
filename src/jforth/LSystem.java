package jforth;

import java.util.*;
import java.util.function.Function;

public class LSystem
{
    String m_str;
    String save;
    String lastResult;
    final TreeMap <String, String> m_map = new TreeMap<>
            (
                    // largest key comes first, greedy parser
                    Comparator.comparingInt(String::length).reversed()
                            .thenComparing(Function.identity())
            );

//    public LSystem (String in)
//    {
//        setMaterial (in);
//    }
//
//    public LSystem ()
//    {
//    }

    public void setMaterial (String in)
    {
        m_str = in;
        save = in;
    }

    public void clrRules()
    {
        m_map.clear ();
    }

    public void putRule (String from, String to) throws Exception
    {
        from = from.trim ();
        to = to.trim ();
        if (from.isEmpty () || to.isEmpty ())
            throw new Exception ("Rule error");
        m_map.put (from, to);
    }

    public void putRule (String rule) throws Exception
    {
        String[] sp = rule.split ("->");
        if (sp.length !=2)
            throw new Exception ("Not a valid rule! "+rule);
        putRule (sp[0], sp[1]);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder ();
        sb.append (m_str).append ('\n');
        for (Map.Entry<String, String> mapElement : m_map.entrySet ())
        {
            sb.append (mapElement.getKey ()).append("->");
            sb.append (mapElement.getValue ()).append ('\n');
        }
        return sb.toString ();
    }

    public String doIt()
    {
        StringBuilder bui = new StringBuilder();

        while (!m_str.isEmpty ())
        {
            boolean found;
            do
            {
                found = false;
                for (Map.Entry<String, String> mapElement : m_map.entrySet ())
                {
                    String k = mapElement.getKey ();
                    if (m_str.startsWith (k))
                    {
                        bui.append (mapElement.getValue ());
                        m_str = m_str.substring (k.length ());
                        found = true;
                        break;
                    }
                }
            } while (found);

            if (!m_str.isEmpty ())
            {
                bui.append (m_str.charAt (0));
                m_str = m_str.substring (1);
            }
        }
        m_str = save; // restore source string

        lastResult = bui.toString ();
        return lastResult;
    }

    public String doNext()
    {
        m_str = lastResult;
        save = lastResult;
        return doIt ();
    }
}
//
//public class TestClass
//{
//    public static void main (String[] args) throws Exception
//    {
//        MultiStringReplace ch = new MultiStringReplace ("A");
//
//        ch.putRule ("A->AB");
//        ch.putRule ("B->A");
//
//        String s = ch.doIt();
//        System.out.println (s);
//        s = ch.doNext ();
//        System.out.println (s);
//        s = ch.doNext ();
//        System.out.println (s);
//    }
//}