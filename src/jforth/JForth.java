package jforth;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory;
import jforth.forthwords.PredefinedWords;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.fraction.Fraction;
import streameditor.StreamingTextArea;
import tools.Func;
import tools.Utilities;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class JForth {
    public final RuntimeEnvironment CurrentEnvironment;

    public enum MODE {EDIT, DIRECT}

    public long LastETime;
    public final long StartTime;
    public final Random random = new Random();
    public HashMap<String, Object> globalMap = new HashMap<>();
    public Exception LastError = null;
    public static final Charset ENCODING = StandardCharsets.ISO_8859_1;
    public static final Long TRUE = 1L;
    public static final Long FALSE = 0L;
    // --Commented out by Inspection (3/25/2017 10:54 AM):private static final String ANSI_CLS = "\u001b[2J";
    // private static final String ANSI_BOLD = "\u001b[1m";
    // private static final String ANSI_YELLOW = "\u001b[33m";
    // private static final String ANSI_NORMAL = "\u001b[0m";
    // --Commented out by Inspection (3/25/2017 10:54 AM):private static final String ANSI_WHITEONBLUE = "\u001b[37;44m";
    // private static final String ANSI_ERROR = "\u001b[93;41m";
    private static final String FORTHPROMPT = "\nJFORTH> ";
    private static final String EDITORPROMPT = "\nEdit> ";
    private static final String OK = " OK";
    // --Commented out by Inspection (4/10/2021 8:28 PM):private static final int HISTORY_LENGTH = 1000;
    private static Voice voice = null;
    public final WordsList dictionary = new WordsList();
    private final OStack dStack = new OStack();
    private final OStack vStack = new OStack();
    public MODE mode = MODE.DIRECT;
    public final transient PrintStream _out; // output channel
    public boolean compiling;
    public int base;
    public NonPrimitiveWord wordBeingDefined = null;
    public BaseWord currentWord;
    public final LineEdit _lineEditor;
    public final LSystem _lsys = new LSystem();
    private MultiDotStreamTokenizer tokenizer = null;
    public StreamingTextArea guiTerminal;

    public JForth(RuntimeEnvironment ri) {
        this(System.out, ri);
    }

    public JForth(PrintStream out, RuntimeEnvironment ri, StreamingTextArea ta) {
        this (out, ri);
        guiTerminal = ta;
    }

    public JForth(PrintStream out, RuntimeEnvironment ri) {
        StartTime = System.currentTimeMillis();
        CurrentEnvironment = ri;
        compiling = false;
        base = 10;
        _out = out;
        new PredefinedWords(this, dictionary);
        _lineEditor = new LineEdit(out, this);
    }

    public String getMapContent () {
        StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> map = (HashMap<String, Object>) globalMap.clone();
        map.forEach((key, value) -> sb.append(key)
                .append(" --> ")
                .append(makePrintable(value))
                .append('\n'));
        return sb.toString();
    }

    /**
     * Call speech output
     *
     * @param txt Text to speak
     */
    public static void speak(String txt) {
        if (voice == null) {
            KevinVoiceDirectory dir = new KevinVoiceDirectory();
            //AlanVoiceDirectory dir = new AlanVoiceDirectory();
            voice = dir.getVoices()[0];
            voice.allocate();
        }
        voice.speak(txt);
    }

    /*
      Starting point

      @param args not used
     */
//    public static void main(String[] args) {
//        AnsiConsole.systemInstall();
//        JForth jf;
//        while (true) // Restart the interpreter on memory errors
//        {
//            jf = new JForth(AnsiConsole.out, RuntimeEnvironment.CONSOLE);
//            try {
//                jf.mainLoop();
//                break;
//            } catch (OutOfMemoryError ex) {
//                jf._out.println(ANSI_ERROR + "Memory Error: " + ex.getMessage());
//                jf._out.println(ANSI_ERROR + "RESET!");
//            }
//        }
//    }

    /**
     * Execute one line and generate output
     *
     * @param input String containing forth commands
     */
    public boolean singleShot(String input) {
        boolean res = true;
        input = Utilities.replaceUmlauts(input);
        input = StringEscape.escape(input);
        if (mode == MODE.DIRECT) {
            if (!interpretLine(input)) {
                _out.print(input + " word execution or stack error");
                dStack.removeAllElements();
                res = false;
            } else {
                _out.print(OK);
            }
        } else // mode == EDIT
        {
            if (!_lineEditor.handleLine(input)) {
                mode = MODE.DIRECT;
            }
        }
        if (mode == MODE.DIRECT) {
            _out.print(FORTHPROMPT);
        } else {
            _out.print(EDITORPROMPT);
        }
        _out.flush();
        return res;
    }

    /**
     * Main loop
     */
//    private void mainLoop() {
//        dStack.removeAllElements();
//        Scanner scanner = new Scanner(System.in);
//        _out.println(Utilities.buildInfo);
//        singleShot("\n"); // to show prompt immediately
//        try {
//            executeFile("autoexec.4th");
//        } catch (Exception unused) {
//            // execution error, file not found
//        }
//        while (true) {
//            String s = scanner.nextLine().trim();
//            singleShot(s);
//        }
//    }

    /**
     * Make human-readable String from object
     *
     * @param o input object
     * @return String
     */
    public String makePrintable(Object o) {
        return Utilities.makePrintable(o, base);
    }

//    public String ObjectToString(Object o) {
//        return Utilities.makePrintable(o, base);
//    }

    /**
     * Run a single line of FORTH statements
     *
     * @param text The line
     * @return false if an error occured
     */
    public boolean interpretLine(String text) {
        try {
            StringReader sr = new StringReader(text);
            tokenizer = new MultiDotStreamTokenizer(sr);
            tokenizer.resetSyntax();
            tokenizer.wordChars('!', '~');
            //st.quoteChar('_');  // test
            tokenizer.whitespaceChars('\u0000', '\u0020');
            //tokenizer.whitespaceChars('\u0020', '\u0020');
            int ttype = tokenizer.nextToken();
            while (ttype != StreamTokenizer.TT_EOF) {
                String word = tokenizer.sval;
                if (word == null)
                    return true;
                if (word.equals("//"))   // Comment until line end
                {
                    return true;
                }
                if (word.equals("(")) // filter out comments
                {
                    for (; ; ) {
                        tokenizer.nextToken();
                        String word2 = tokenizer.sval;
                        if (word2.endsWith(")")) {
                            break;
                        }
                    }
                    tokenizer.nextToken();
                    continue;
                }
                if (!compiling) {
                    if (!doInterpret(word)) {
                        return false;
                    }
                } else {
                    if (!doCompile(word)) {
                        return false;
                    }
                }
                ttype = tokenizer.nextToken();
            }
            return true;
        } catch (Exception e) {
            setLastError(e);
            return false;
        }
    }

    private String handleDirectStringOut(String word, boolean compile) throws Exception {
        if (word.equals(".\"")) {
            StringBuilder sb = new StringBuilder();
            for (; ; ) {
                tokenizer.nextToken();
                String word2 = tokenizer.sval;
                if (word2.endsWith("\"")) {
                    sb.append(word2, 0, word2.length() - 1);
                    break;
                }
                sb.append(word2).append(' ');
            }
            if (compile) {
                wordBeingDefined.addWord(new Literal(sb.toString()));
            } else {
                dStack.push(sb.toString());
            }
            return ".";
        }
        return word;
    }

    /**
     * Interpret or compile known words
     *
     * @param word      the word
     * @param action function to be applied
     * @return true if word is known
     */
    public boolean doForKnownWords(String word, Func<Object, Object> action) {
        Long num = Utilities.parseLong(word, base);
        if (num != null) {
            action.apply(num);
            return true;
        }
        BigInteger big = Utilities.parseBigInt(word, base);
        if (big != null) {
            action.apply(big);
            return true;
        }
        Double dnum = Utilities.parseDouble(word, base);
        if (dnum != null) {
            action.apply(dnum);
            return true;
        }
        Complex co = Utilities.parseComplex(word, base);
        if (co != null) {
            action.apply(co);
            return true;
        }
        Fraction fr = Utilities.parseFraction(word, base);
        if (fr != null) {
            action.apply(fr);
            return true;
        }
        DoubleMatrix ma = DoubleMatrix.parseMatrix(word, base);
        if (ma != null) {
            action.apply(ma);
            return true;
        }
        DoubleSequence lo = DoubleSequence.parseSequence(word, base);
        if (lo != null) {
            action.apply(lo);
            return true;
        }
        StringSequence ss = StringSequence.parseSequence(word);
        if (ss != null) {
            action.apply(ss);
            return true;
        }
        String ws = Utilities.parseString(word);
        if (ws != null) {
            action.apply(ws);
            return true;
        }
        double[] pd = PolynomialParser.parsePolynomial(word, base);
        if (pd != null) {
            PolynomialFunction plf = new PolynomialFunction(pd);
            action.apply(plf);
            return true;
        }
        //setLastError (new Exception("Error executing: "+word));
        return false;
    }

    private void setLastError(Exception e)
    {
        LastError = e;
        LastETime = System.currentTimeMillis();
    }

    private boolean doInterpret(String word) throws Exception {
        word = handleDirectStringOut(word, false);
        BaseWord bw = dictionary.search(word);
        if (bw != null) {
            if (bw instanceof NonPrimitiveWord) {
                currentWord = bw;  // Save for recursion
            }
            boolean ret = bw.apply(dStack, vStack) != 0;
            if (!ret)
                setLastError(new Exception("failed execution of '"+word+"'"));
            return ret;
        }
        boolean ret = doForKnownWords(word, dStack::push);
        if (ret) {
            return true;
        }
        dStack.push(word); // as String if word isn't known
        return true;
    }

    boolean recflag = false;

    private boolean doCompile(String word) throws Exception {
        word = handleDirectStringOut(word, true);
        BaseWord bw;
        if (word.equalsIgnoreCase("recursive"))
            recflag = true;
        if (recflag && currentWord != null && word.equalsIgnoreCase(currentWord.name)) {
            bw = dictionary.search("recurse");
            recflag = false;
        } else {
            bw = dictionary.search(word);
        }
        if (bw != null) {
            if (bw.immediate) {
                bw.apply(dStack, vStack);
            } else {
                wordBeingDefined.addWord(bw);
            }
            return true;
        }
        boolean ret = doForKnownWords(word, o -> wordBeingDefined.addWord(new Literal(o)));
        if (ret) {
            return true;
        }
        _out.print(word + " ?");
        compiling = false;
        return false;
    }

    public String getNextToken() {
        try {
            if (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                return tokenizer.sval;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public void executeFile(ArrayList<String> as, boolean crlf) {
        for (String s : as) {
            interpretLine(s);
            if (crlf) {
                _out.println();
            }
        }
    }

    public void executeFile(String fileName) throws Exception {
        executeFile(FileUtils.loadStrings(fileName), false);
    }
}
