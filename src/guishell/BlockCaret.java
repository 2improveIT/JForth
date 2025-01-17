package guishell;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Code based on Groovy text editor
 * ... groovy-console/src/main/groovy/groovy/ui/text/TextEditor.java
 */

public class BlockCaret extends DefaultCaret
{
    public BlockCaret ()
    {
        setBlinkRate(500);
    }

    protected synchronized void damage (Rectangle r)
    {
        if (r != null)
        {
            JTextComponent component = getComponent();
            x = r.x;
            y = r.y;
            Font font = component.getFont();
            width = component.getFontMetrics(font).charWidth('w');
            height = r.height;
            repaint();
        }
    }

    public void mouseClicked (MouseEvent e)
    {
        JComponent c = (JComponent) e.getComponent();
        c.repaint();
    }

    public void paint (Graphics g)
    {
        if (isVisible())
        {
            try
            {
                JTextComponent component = getComponent();
                Rectangle r = component.getUI().modelToView(component, getDot());
                Color c = g.getColor();
                g.setColor(component.getBackground());
                g.setXORMode(component.getCaretColor());
                r.setBounds(r.x, r.y,
                        g.getFontMetrics().charWidth('w'),
                        g.getFontMetrics().getHeight());
                g.fillRect(r.x, r.y, r.width, r.height);
                g.setPaintMode();
                g.setColor(c);
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }
        }
    }
}