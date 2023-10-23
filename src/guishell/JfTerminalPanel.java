package guishell;

import jforth.JForth;
import jforth.RuntimeEnvironment;
import tools.ForthProperties;
import tools.StringStream;
import tools.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Administrator
 */
public class JfTerminalPanel extends ColorPane {
    private static final String AnsiDefaultOutput = AnsiColors.getCode(Color.yellow);
    private static final String AnsiError = AnsiColors.getCode(Color.RED);
    private static final String AnsiReset = AnsiColors.getCode(Color.white);
    // The forth and its output channel -----------------------------------------------------------
    public final StringStream _ss = new StringStream();
    public final JForth _jf = new JForth(_ss.getPrintStream(), RuntimeEnvironment.GUITERMINAL, this);
    private StringBuffer collector;
    private Object waiter = new Object();
    private int ncoll;

    public String collectKeys(int n) throws Exception{
        collector = new StringBuffer();
        ncoll = n;
        synchronized(waiter) {
            waiter.wait();
        }
        String str = collector.toString();
        collector = null;
        return str;
    }

    private void runForthThread(JComboBox<String> combo) {
        (new Thread(() -> {
            String lineData = Utilities.currentLine(JfTerminalPanel.this);
            assert lineData != null;
            lineData = lineData.replace("JFORTH>", "");
            boolean ok = _jf.interpretLine(lineData);
            String response = _ss.getAndClear();
            if (ok) {
                combo.addItem(lineData.trim());
                response = AnsiDefaultOutput + response + " OK\n";
            }
            else
                response = AnsiError + " Error\n";
            response = response+ AnsiReset + "JFORTH> ";
            appendANSI(response);
        })).start();
    }

    public JfTerminalPanel(JComboBox<String> combo) {
        super();
        combo.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxEdited")) {
                String item = combo.getEditor().getItem() + "\n";
                //lineListener.fakeIn(item);
                appendANSI(item);
            }
        });

        addMouseListener(new MouseHandler(this));

        setCaret(new BlockCaret());
        setBackground(ForthProperties.getBkColor());
        setFont(new java.awt.Font("Monospaced", Font.PLAIN, 16)); // NOI18N
        setCaretColor(new java.awt.Color(255, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        // run JForth
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (collector != null) {
                    collector.append(e.getKeyChar());
                    if (collector.length() >= ncoll) {
                        synchronized (waiter) {
                            waiter.notifyAll();
                        }
                    }
                    return;
                }
                if (e.getKeyChar() == '\n') {
                    runForthThread(combo);
                }
            }
        });

        // start ...
        try {
            _jf.executeFile("autoexec.4th");
        } catch (Exception e) {
            appendANSI("autoexec file not found");
        }
        _jf.singleShot("");
        appendANSI(_ss.getAndClear());
    }

    public String singleShot(String in) {
        _jf.singleShot(in);
        return _ss.getAndClear();
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
        String clip = Objects.requireNonNull(Utilities.getClipBoardString()).trim();
        clip = clip.replaceAll("\\p{C}", " ");
        //lineListener.fakeIn(clip);
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
}
