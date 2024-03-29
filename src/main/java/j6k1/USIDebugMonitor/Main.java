package j6k1.USIDebugMonitor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Dimension;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextArea console;
	private ErrorWindow errorWindow;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProcessBuilder pb = new ProcessBuilder(Arrays.asList(args));
					File dir = new File(args[0]).getParentFile();
					pb.directory(dir);
					Main frame = new Main(pb);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Main(ProcessBuilder pb) throws IOException, InterruptedException {
		Map<String,String> env = pb.environment();
		env.put("RUST_BACKTRACE", "1");
		NotifyQuit n = new NotifyQuit();

		setTitle("USIDebugMonitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		console = new JTextArea();
		console.setLineWrap(true);
		console.setRows(32);
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		console.setEditable(false);
		JScrollPane scrollPanel = new JScrollPane(console,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setPreferredSize(new Dimension(750, 530));
		contentPane.add(scrollPanel, BorderLayout.NORTH);
		console.setColumns(10);

		this.errorWindow = new ErrorWindow();
		this.setVisible(true);

		Process p = pb.start();

		BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
		BufferedReader stderror = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"));
		BufferedReader guiout = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		BufferedWriter engine = new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), "UTF-8"));

		ExecutorService service = Executors.newFixedThreadPool(1);

		Future<?> h = service.submit(() -> {
			while (!n.isQuit()) {
				try {
					if (stdout.ready()) {
						String output = stdout.readLine();

						if (output != null) {
							System.out.print(output + "\r\n");

							EventQueue.invokeLater(() -> {
								String lines = console.getText();
								console.setText(lines + "<" + output + "\r\n");
								this.revalidate();
							});
						} else {
							break;
						}
					}
					if (stderror.ready()) {
						String output = stderror.readLine();

						if (output != null) {
							EventQueue.invokeLater(() -> {
								String lines = this.errorWindow.getText();
								this.errorWindow.setText(lines + output + "\r\n");
								this.errorWindow.revalidate();
							});
						} else {
							break;
						}
					}
					if (guiout.ready()) {
						String output = guiout.readLine();
						if (output != null) {
							if (!output.isEmpty()) {
								engine.write(output + "\r\n");
								engine.flush();
								EventQueue.invokeLater(() -> {
									String lines = console.getText();
									console.setText(lines + ">" + output + "\r\n");
									this.revalidate();
								});
							}
						} else {
							break;
						}
					}
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				engine.write("quit\r\n");
				engine.flush();
				Main.this.dispose();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				n.invoke();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
		});
	}
}
