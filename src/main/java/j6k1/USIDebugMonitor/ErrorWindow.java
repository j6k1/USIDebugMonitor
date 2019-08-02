package j6k1.USIDebugMonitor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;

public class ErrorWindow extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -5983593292922241678L;
	private JPanel contentPane;
	private JTextArea console;

	/**
	 * Create the frame.
	 */
	public ErrorWindow() {
		super("stderror");

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 150, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		console = new JTextArea();
		console.setPreferredSize(new Dimension(750, 550));
		console.setEditable(false);
		console.setForeground(Color.WHITE);
		console.setBackground(Color.BLACK);
		contentPane.add(console, BorderLayout.NORTH);
		console.setColumns(10);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
	}

	public String getText() {
		return this.console.getText();
	}

	public void setText(String s) {
		this.console.setText(s);
	}
}
