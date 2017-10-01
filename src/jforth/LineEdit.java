package jforth;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Line Editor: -->
 * Commands are:
 * #l         -- List (with line numbers)
 * #t         -- print list as String
 * #c         -- clear all
 * #x         -- leave line editor. Content is pushed on forth stack
 * #r text    -- read file where text is the file name
 * #s test    -- save file where text is the file name
 * #innn text -- Insert before, where nnn is the line number and text is the content
 * #nnn      -- Delete line nnn
 * <p>
 * ... any other input is appended.
 * Type "editor" to enter the line editor
 */
public class LineEdit
{
    ArrayList<String> list = new ArrayList<>();
    InputStream _in;
    PrintStream _out;

    static public String helpText =
            " * Line Editor: -->\n" +
                    " Commands are:\n" +
                    " #l         -- List (with line numbers)\n" +
                    " #t         -- print list as String\n" +
                    " #c         -- clear all\n" +
                    " #x         -- leave line editor. Content is pushed on forth stack\n" +
                    " #r text    -- read file where text is the file name\n" +
                    " #s test    -- save file where text is the file name\n" +
                    " #innn text -- Insert before, where nnn is the line number and text is the content\n" +
                    " #nnn      -- Delete line nnn\n" +
                    "\n" +
                    " ... any other input is appended.\n" +
                    " Type \"editor\" to enter the line editor\n";

    public LineEdit (InputStream i, PrintStream p)
    {
        _in = i;
        _out = p;

        p.println(helpText);
    }

    private void printErr()
    {
        _out.println("ERROR");
    }

    /**
     * Tester
     *
     * @param args
     */
    public static void main (String[] args)
    {
        LineEdit le = new LineEdit(System.in, System.out);
        le.run();
    }

    public void run ()
    {
        Scanner scanner = new Scanner(_in);
        while (true)
        {
            _out.print("Edit: ");
            _out.flush();
            if (!handleLine(scanner.nextLine().trim()))
            {
                break;
            }
        }
    }

    private boolean handleLine (String in)
    {
        if (in.startsWith("#"))
        {
            int firstspc = in.indexOf(' ');
            String args;
            String cmd;
            if (firstspc == -1)  // no Space found
            {
                cmd = in.substring(1, in.length());
                args = null;
            }
            else
            {
                cmd = in.substring(1, firstspc);
                args = in.substring(firstspc + 1, in.length());
            }
            try
            {
                int linenum = Integer.parseInt(cmd);
                if (args == null)
                {
                    try
                    {
                        list.remove(linenum);
                    }
                    catch (Exception e)
                    {
                        printErr();
                    }
                }
                else
                {
                    list.set(linenum, args);
                }
                return true;
            }
            catch (Exception unused)
            {

            }
            try
            {
                if (cmd.equals("l")) // List with line numbers
                {
                    for (int s = 0; s < list.size(); s++)
                    {
                        _out.println(s + ": " + list.get(s));
                    }
                }
                else if (cmd.equals("t")) // List without line numbers
                {
                    String s = toString();
                    if (!s.isEmpty())
                    {
                        _out.println(s);
                    }
                }
                else if (cmd.equals("c"))  // clear program
                {
                    clear();
                }
                else if (cmd.equals("r"))   // load new program
                {
                    load(args);
                }
                else if (cmd.equals("a"))   // append program from disk
                {
                    append(args);
                }
                else if (cmd.equals("s"))   // save program
                {
                    save(args);
                }
                else if (cmd.equals("dir")) // show directory
                {
                    String s = Utilities.dir(".");
                    _out.println(s);
                }
                else if (cmd.equals("x"))   // leave editor
                {
                    return false;
                }
                else if (cmd.startsWith("i"))
                {
                    int pos = Integer.parseInt(cmd.substring(1));
                    list.add(pos, args);
                }
            }
            catch (Exception e)
            {
                printErr();
            }
        }
        else
        {
            if (in.length() > 0)
            {
                list.add(in);
            }
        }
        return true;
    }

    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        for (String s : list)
        {
            sb.append(s).append('\n');
        }
        return sb.toString().trim();
    }

    public void clear ()
    {
        list.clear();
    }

    public void load (String name) throws Exception
    {
        list = Utilities.fileLoad(name);
    }

    public void append (String name) throws Exception
    {
        ArrayList<String> l2 = Utilities.fileLoad(name);
        list.addAll(l2);
    }

    public void save (String name) throws Exception
    {
        Utilities.fileSave(list, name);
    }
}