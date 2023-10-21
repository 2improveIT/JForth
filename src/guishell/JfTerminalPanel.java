package guishell;

import jforth.JForth;
import jforth.RuntimeEnvironment;
import tools.ForthProperties;
import tools.StringStream;
import tools.Utilities;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Administrator
 */
public class JfTerminalPanel extends ColorPane {
    private static final String AnsiDefaultOutput = AnsiColors.getCode(Color.yellow);
    private static final String AnsiError = AnsiColors.getCode(Color.RED);
    private static final String AnsiReset = AnsiColors.getCode(Color.white);
    // --------------------------------------------------------------------------------------------
    private final LineListener lineListener = new LineListener();
    private final JComboBox<String> combo;
    // The forth and its output channel -----------------------------------------------------------
    public StringStream _ss = new StringStream();
    public JForth _jf = new JForth(_ss.getPrintStream(), RuntimeEnvironment.GUITERMINAL, this);

    public JfTerminalPanel(JComboBox<String> combo) {
        super();
        this.combo = combo;
        combo.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxEdited")) {
                String item = combo.getEditor().getItem() + "\n";
                lineListener.fakeIn(item);
                appendANSI(item);
            }
        });

        addMouseListener(new MouseHandler(this));

        setCaret(new BlockCaret());
        addKeyListener(lineListener);
        setBackground(ForthProperties.getBkColor());
        setFont(new java.awt.Font("Monospaced", Font.PLAIN, 16)); // NOI18N
        setCaretColor(new java.awt.Color(255, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        runForthLoop();
    }

    public String singleShot(String in) {
        _jf.singleShot(in);
        String ret = _ss.getAndClear();
        return ret;
    }

    /**
     * Make image from TextArea
     *
     * @return The Image
     */
    public BufferedImage getScreenShot() {
        Rectangle r = this.getVisibleRect();
        int w = r.width;
        int h = r.height;
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage image = new BufferedImage(w, h, type);
        Graphics2D g2 = image.createGraphics();
        // Translate g2 to clipping rectangle of textArea.
        g2.translate(-r.x, -r.y);
        this.paint(g2);
        g2.dispose();
        return image;
    }

    /**
     * Handle text from clipboard
     * "laladumm"
     */
    @Override
    public void paste() {
        super.paste();
        String clip = Utilities.getClipBoardString().trim();
        clip = clip.replaceAll("[\\p{C}]", " ");
        lineListener.fakeIn(clip);
    }

    /**
     * Add image to our TextPane
     *
     * @param img the Image
     */
    public void addImage(Image img) {
        appendANSI("\n");
        addIcon(img);
        appendANSI("\n");
    }

    public String currentLine(JTextPane textTx) {
        // Get section element
        Element section = textTx.getDocument().getDefaultRootElement();

        // Get number of paragraphs.
        // In a text pane, a span of characters terminated by single
        // newline is typically called a paragraph.
        int paraCount = section.getElementCount();

        int position = textTx.getCaret().getDot();

        // Get index ranges for each paragraph
        for (int i = 0; i < paraCount; i++) {
            Element e1 = section.getElement(i);

            int rangeStart = e1.getStartOffset();
            int rangeEnd = e1.getEndOffset();

            try {
                String para = textTx.getText(rangeStart, rangeEnd - rangeStart);

                if (position >= rangeStart && position <= rangeEnd)
                    return para;
            } catch (BadLocationException ex) {
                System.err.println("Get current line from editor error: " + ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Initialize and start Forth thread
     */
    private void runForthLoop() {
        try {
            _jf.executeFile("autoexec.4th");
        } catch (Exception e) {
            appendANSI("autoexec file not found");
        }
        _jf.singleShot("");
        appendANSI(_ss.getAndClear());

        Utilities.executeThread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                lineListener.getBufferedLine(); / WAIT

                ///
                String lineData = currentLine(this);
                lineData = lineData.replace("JFORTH>", "");
                ///
                boolean ok = _jf.interpretLine(lineData);
                String txt = _ss.getAndClear();
                if (ok)
                    txt = AnsiDefaultOutput + txt;
                else
                    txt = AnsiError + "Error";
                txt = txt+ "\nJFORTH> ";
                appendANSI(txt);
            }
        });
    }

    /**
     * Get single char from input buffer
     * Blocks if there is none
     *
     * @return the character
     */
    public char getKey() {
        return lineListener.getBufferedChar();
    }

    /**
     * Halt line input but keep single char input runing
     *
     * @param lock true if line input is disabled
     */
    public void lockLineInput(boolean lock) {
        lineListener.lock(lock);
        lineListener.reset();
    }
}
