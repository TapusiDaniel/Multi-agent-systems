import javax.swing.*;
import java.awt.*;

public class ArbitratorGUI extends JFrame {
    private static final long serialVersionUID = 1L;
	private JTextArea textArea;

    public ArbitratorGUI() {
        setTitle("Arbitrator GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    public void appendText(String text) {
        textArea.append(text + "\n");
    }
}