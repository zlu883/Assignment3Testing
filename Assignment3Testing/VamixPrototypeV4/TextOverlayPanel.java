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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextOverlayPanel extends JPanel {
	// private static TextOverlayPanel instance = new TextOverlayPanel();

	private JTextArea textToOverLay;

	private JLabel textToOverLayLabel, fontTypeLabel, fontSizeLabel,
			fontColorLabel, positionLabel, startInputLabel, endInputLabel;

	private JComboBox fontsBox;

	private JButton addTextButton, colorButton;

	private TimeInput fontSize;

	private JComboBox positionChooser;

	private InputPanel startInput, endInput;

	private HashMap<String, File> fontMap;

	private JColorChooser colorChooser;

	private ArrayList<TextOverlay> textComponents;
	private ArrayList<TextOverlayTab> textTabs;

	public TextOverlayPanel() {
		textComponents = new ArrayList<TextOverlay>();
		textTabs = new ArrayList<TextOverlayTab>();

		textToOverLay = new JTextArea(10, 35);
		fontSize = new TimeInput();
		fontSize.setText("16");

		textToOverLayLabel = new JLabel("Specify text to overlay");
		fontTypeLabel = new JLabel("Select font");
		fontSizeLabel = new JLabel("Specify font size as an integer");
		fontColorLabel = new JLabel("");
		positionLabel = new JLabel("Select Position");

		CreateFontChooser fontChooser = new CreateFontChooser();
		fontsBox = fontChooser.getFontsBox();

		addTextButton = new JButton("Add Text Component");
		addTextButton.addActionListener(setAddTextButton());

		colorButton = new JButton("Select Font Color");
		colorButton.addActionListener(setColorChooserButton());

		String[] positions = new String[] { "TOPLEFT", "TOPRIGHT",
				"BOTTOMLEFT", "BOTTOMRIGHT", "LEFT", "RIGHT", "TOP", "BOTTOM",
				"CENTER" };
		positionChooser = new JComboBox(positions);

		startInput = new InputPanel("Specify start time in hh:mm:ss");
		endInput = new InputPanel("Specify end time in hh:mm:ss");

		colorChooser = new JColorChooser(Color.BLACK);
		fontColorLabel.setText(Integer.toHexString(colorChooser.getColor()
				.getRGB()));

		add(textToOverLayLabel);
		add(textToOverLay);

		add(fontTypeLabel);
		add(fontsBox);

		add(fontSizeLabel);
		add(fontSize);

		add(colorButton);
		add(fontColorLabel);
		// add(fontColor);

		JPanel positionContainer = new JPanel();
		positionContainer.add(positionLabel);
		positionContainer.add(positionChooser);
		add(positionContainer);

		add(startInput);
		add(endInput);

		add(addTextButton);

		setVisible(true);

		CreateFontList fontList = new CreateFontList();
		setFontMap(fontList.getMap());
	}

	public ActionListener setAddTextButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Integer.parseInt(fontSize.getText()) > 72) {
					JOptionPane.showMessageDialog(getParent(),
							"The specified font size is too large, please change it");
					return;
				}

				TextOverlay newTextComponent = createTextOverlayObject();

				textComponents.add(newTextComponent);
				
				TextOverlayTab newTab = new TextOverlayTab(newTextComponent, textComponents.size() - 1);
				newTab.setName("Text Component " + textComponents.size());

				TextOverlayWindow.getInstance().addTab(newTab.getName(), newTab);
			}

		};
	}

	private ActionListener setColorChooserButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Color newColor = JColorChooser.showDialog(
						TextOverlayPanel.this, "Choose Background Color",
						Color.BLACK);
				String red = Integer.toHexString(newColor.getRed());
				if (red.length() == 1) red = "0" + red;
				
				String green = Integer.toHexString(newColor.getGreen());
				if (green.length() == 1) green = "0" + green;
				
				String blue = Integer.toHexString(newColor.getBlue());
				if (blue.length() == 1) blue = "0" + blue;
				
				String alpha = Integer.toHexString(newColor.getAlpha());
				if (alpha.length() == 1) alpha = "0" + alpha;
				
				String hexString = red + green + blue + alpha;

				fontColorLabel.setText(hexString);
				fontColorLabel.setForeground(newColor);
			}

		};
	}

	public ArrayList<TextOverlay> getTextOverlayComponents() {
		return textComponents;
	}
	
	public TextOverlay createTextOverlayObject() {
		String videoPath = GuiManager.getInstance()
				.getVideoFilePath();

		File selectedFontFile = getFontMap().get(
				fontsBox.getSelectedItem().toString());
		String selectedFontPath = selectedFontFile.getPath();

		String fontsize = fontSize.getText();
		String colorString = fontColorLabel.getText();

		if (fontsize.equals("")) {
			fontsize = "" + 16;
		}
		if (colorString.equals("ff000000") || colorString.equals(null)) {
			colorString = "000000ff";
		}

		TextOverlay newTextComponent = new TextOverlay(videoPath,
				textToOverLay.getText(), selectedFontPath, fontsize,
				colorString,
				TextOverlay.TextPosition.valueOf(positionChooser
						.getSelectedItem().toString()),
				startInput.getInputs(), endInput.getInputs());
		
		return newTextComponent;
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
		return addTextButton;
	}

	public ArrayList<TextOverlay> getTextOverlays() {
		return textComponents;
	}
	
	public ArrayList<TextOverlayTab> getTextTabs() {
		return textTabs;
	}

	public HashMap<String, File> getFontMap() {
		return fontMap;
	}

	public void setFontMap(HashMap<String, File> fontMap) {
		this.fontMap = fontMap;
	}
	
	public JTextArea getTextToOverlay() {
		return textToOverLay;
	}

	public JComboBox getFontsBox() {
		return fontsBox;
	}

	public JComboBox getPositionChooser() {
		return positionChooser;
	}
	
	public JLabel getColorLabel() {
		return fontColorLabel;
	}
	
	public TimeInput getFontSize() {
		return fontSize;
	}
	
	public InputPanel getStartInputPanel() {
		return startInput;
	}
	
	public InputPanel getEndInputPanel() {
		return endInput;
	}
}
