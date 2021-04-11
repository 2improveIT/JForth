package streameditor;

import javax.swing.*;
import java.awt.*;

public class GuiTerminal {
    private JPanel MainPanel;
    private JTextPane textArea1;
    private JComboBox<String> comboBox1;

    public GuiTerminal() {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GuiTerminal");
        frame.setContentPane(new GuiTerminal().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        MainPanel = new JPanel();
        MainPanel.setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        MainPanel.add(scrollPane1, BorderLayout.CENTER);
        scrollPane1.setViewportView(textArea1);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 1));
        panel1.setAutoscrolls(true);
        panel1.setBackground(new Color(-16777216));
        panel1.setPreferredSize(new Dimension(0, 30));
        MainPanel.add(panel1, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        label1.setBackground(new Color(-16777216));
        label1.setForeground(new Color(-1642753));
        label1.setOpaque(true);
        label1.setPreferredSize(new Dimension(54, 30));
        label1.setText("History ->");
        panel1.add(label1);
        comboBox1.setEditable(true);
        comboBox1.setMinimumSize(new Dimension(200, 30));
        comboBox1.setPreferredSize(new Dimension(200, 30));
        panel1.add(comboBox1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

    private void createUIComponents() {
        comboBox1 = new JComboBox<>();
        textArea1 = new StreamingTextArea(comboBox1);
    }
}
