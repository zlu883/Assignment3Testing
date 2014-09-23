import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextOverlayPanel extends JPanel {
	//private static TextOverlayPanel instance = new TextOverlayPanel();
	
	private JTextArea textToOverLay;

	private JTextField fontColor;

	private JLabel textToOverLayLabel, fontTypeLabel, fontSizeLabel,
			fontColorLabel, startInputLabel, endInputLabel;

	private JComboBox fontsBox;

	private JButton exportButton, colorButton;

	private TimeInput fontSize;

	private InputPanel startInput, endInput;

	private HashMap<String, File> fontMap;
	
	private JColorChooser colorChooser;

	private ArrayList<TextOverlay> textComponents;

	public TextOverlayPanel() {
		textComponents = new ArrayList<TextOverlay>();

		textToOverLay = new JTextArea(10, 35);
		fontSize = new TimeInput();
		fontColor = new JTextField(25);

		textToOverLayLabel = new JLabel("Specify text to overlay");
		fontTypeLabel = new JLabel("Select font");
		fontSizeLabel = new JLabel("Specify font size as an integer");
		fontColorLabel = new JLabel("");
		startInputLabel = new JLabel("Specify start time in hh:mm:ss");
		endInputLabel = new JLabel("specify end time in hh:mm:ss");

		CreateFontChooser fontChooser = new CreateFontChooser();
		fontsBox = fontChooser.getFontsBox();

		exportButton = new JButton("Add Text Component");
		exportButton.addActionListener(setExportButton());
		
		colorButton = new JButton("Select Font Color");
		colorButton.addActionListener(setColorChooserButton());

		startInput = new InputPanel("Specify start time in hh:mm:ss");
		endInput = new InputPanel("Specify end time in hh:mm:ss");
		
		colorChooser = new JColorChooser();

		add(textToOverLayLabel);
		add(textToOverLay);

		add(fontTypeLabel);
		add(fontsBox);

		add(fontSizeLabel);
		add(fontSize);

		add(colorButton);
		add(fontColorLabel);
		//add(fontColor);

		add(startInput);
		add(endInput);

		add(exportButton);

		setVisible(true);

		CreateFontList fontList = new CreateFontList();
		setFontMap(fontList.getMap());
	}
	
	/*public static TextOverlayPanel getInstance() {
		return instance;
	}*/

	public ActionListener setExportButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String videoPath = VamixPrototypeV2.getInstance()
						.getVideoFilePath();

				File selectedFontFile = getFontMap().get(fontsBox.getSelectedItem()
						.toString());
				String selectedFontPath = selectedFontFile.getPath();

				String fontsize = fontSize.getText();
				String colorString = fontColorLabel.getText();
				
				if (fontsize.equals("")) {
					fontsize = "" + 16;
				}
				if (colorString.equals("") || colorString.equals(null)) {
					colorString = "black";
				}

				TextOverlay newTextComponent = new TextOverlay(videoPath, textToOverLay
						.getText(), selectedFontPath, fontsBox.getSelectedIndex(), fontsize, colorString,
						startInput.getInputs(), endInput.getInputs());
				
				textComponents.add(newTextComponent);
				
				TextOverlayWindow.getInstance().addTab("Text Component", new TextOverlayTab(newTextComponent, textComponents.size() - 1));
				
			}

		};
	}
	
	private ActionListener setColorChooserButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Color newColor = JColorChooser.showDialog(TextOverlayPanel.this,
	                     "Choose Background Color",
	                     Color.BLACK);
				String hexString = Integer.toHexString(newColor.getRGB());
				fontColorLabel.setText(hexString);
			}
			
		};
	}
	
	public ArrayList<TextOverlay> getTextOverlayComponents() {
		return textComponents;
	}

	/**
	 * This class acts as a container for a label, and three input text fields.
	 * It is the component that allows users to input the start and end times
	 * for extraction.
	 * 
	 */
	class InputPanel extends JPanel {
		private TimeInput hrsInput;
		private TimeInput minsInput;
		private TimeInput secsInput;

		private JLabel inputLabel;

		public InputPanel(String label) {
			this.add(inputLabel = new JLabel(label));

			this.add(hrsInput = new TimeInput());
			this.add(minsInput = new TimeInput());
			this.add(secsInput = new TimeInput());
		}

		/**
		 * This method will return what is currently entered in its input fields
		 * as an array.
		 */
		public String[] getInputs() {
			String[] inputs = new String[3];
			inputs[0] = hrsInput.getText();
			inputs[1] = minsInput.getText();
			inputs[2] = secsInput.getText();

			return inputs;
		}
		
		public void setInputs(String[] inputs) {
			hrsInput.setText(inputs[0]);
			minsInput.setText(inputs[1]);
			secsInput.setText(inputs[2]);
		}
		
		public String getLabelText() {
			return inputLabel.getText();
		}
	}

	/**
	 * This class is essentially a JTextField, though it only allows numbers to
	 * be entered. It is used in conjunction with the InputPanel class to allow
	 * users to specify the start and end times for extraction.
	 */
	class TimeInput extends JTextField {

		public TimeInput() {
			// Set the size of the input field.
			setColumns(3);

			addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					char vChar = e.getKeyChar();
					// If the character entered is not an integer, then do not
					// place it into the text field.
					if (!(Character.isDigit(vChar)
							|| (vChar == KeyEvent.VK_BACK_SPACE) || (vChar == KeyEvent.VK_DELETE))) {
						e.consume();
					}
				}
			});
				
		}
	}
	
	public JButton getAddTextButton() {
		return exportButton;
	}
	
	public ArrayList<TextOverlay> getTextOverlays() {
		return textComponents;
	}

	public HashMap<String, File> getFontMap() {
		return fontMap;
	}

	public void setFontMap(HashMap<String, File> fontMap) {
		this.fontMap = fontMap;
	}
}
