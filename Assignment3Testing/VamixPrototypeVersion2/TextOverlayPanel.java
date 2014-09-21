package VamixPrototypeVersion2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextOverlayPanel extends JPanel {
	private JTextArea textToOverLay;

	private JTextField fontSize, fontColor, fileName;

	private JLabel textToOverLayLabel, fontTypeLabel, fontSizeLabel,
			fontColorLabel, fileNameLabel;

	private JComboBox fontsBox;

	private JButton exportButton;

	private HashMap<String, File> fontMap;

	public TextOverlayPanel() {

		textToOverLay = new JTextArea(10, 35);
		fontSize = new JTextField(10);
		fontColor = new JTextField(25);
		fileName = new JTextField(35);

		textToOverLayLabel = new JLabel("Specify text to overlay");
		fontTypeLabel = new JLabel("Select font");
		fontSizeLabel = new JLabel("Specify font size as an integer");
		fontColorLabel = new JLabel("Specify color");
		fileNameLabel = new JLabel("Specify name of new video file");

		CreateFontChooser fontChooser = new CreateFontChooser();
		fontsBox = fontChooser.getFontsBox();

		exportButton = new JButton("Export");
		exportButton.addActionListener(setExportButton());

		add(textToOverLayLabel);
		add(textToOverLay);

		add(fontTypeLabel);
		add(fontsBox);

		add(fontSizeLabel);
		add(fontSize);

		add(fontColorLabel);
		add(fontColor);

		add(fileNameLabel);
		add(fileName);

		add(exportButton);

		setVisible(true);

		CreateFontList fontList = new CreateFontList();
		fontMap = fontList.getMap();
	}

	public ActionListener setExportButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String videoPath = VamixPrototypeV2.getInstance()
							.getVideoFilePath();

					File selectedFontFile = fontMap.get(fontsBox
							.getSelectedItem().toString());
					String selectedFontPath = selectedFontFile.getPath();

					System.out.println(selectedFontPath);

					String cmd = "avconv -i " + videoPath
							+ " -strict experimental -vf \"drawtext="
							+ "fontfile='" + selectedFontPath + "':text='"
							+ textToOverLay.getText() + "'\" "
							+ "/home/carvrooom/Videos/" + fileName.getText();

					ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c",
							cmd);
					pb.redirectErrorStream(true);
					Process p = pb.start();

					InputStream stdout = p.getInputStream();

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(stdout));

					String s = null;

					while ((s = reader.readLine()) != null) {
						System.out.println(s);
					}
				} catch (IOException e1) {
				}
			}

		};
	}

}
