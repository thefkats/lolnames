package version_1;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

// TODO: add window for list of files in sources, add loading function to file manager, add sorting buttons, add sorting functions (in data)
public class GUI {
	private static FileManager fm;
	private static Data data;
	public static void main(String[] args) throws IOException, URISyntaxException {
		data = new Data(5);
		fm = new FileManager("", data);

		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		JPanel pan = new JPanel(new FlowLayout());
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new run());
		pan.add(runButton);
		JButton importButton = new JButton("Import");
		importButton.addActionListener(new imports());
		pan.add(importButton, FlowLayout.LEFT);
		frame.getContentPane().add(pan);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				fm.save();
			}
		});

	}

	static class run implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("[Run]");
			data.run();
			System.out.println(data.toString());
			System.out.println("[Finished Run]");
		}
	}

	static class imports implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new Thread() {
				public void run() {
					System.out.println("[Import]");
					JFrame frame2 = new JFrame();
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(frame2);
					File f = fc.getSelectedFile();
					fm.importFile(f);
				}
			}.start();
		}
	}

}
