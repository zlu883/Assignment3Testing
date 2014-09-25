import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextOverlayTab extends TextOverlayPanel {
	private TextOverlay textComponent;

	// This field represents its index position in the tabbed pane component.
	private int tabPosition;

	private JLabel pathToVideoLabel;

	private JButton previewButton, deleteButton;

	public TextOverlayTab(TextOverlay textComponent, int tabPosition) {
		this.textComponent = textComponent;

		this.tabPosition = tabPosition;

		pathToVideoLabel = new JLabel(GuiManager.getInstance()
				.getVideoFilePath());

		previewButton = new JButton("Preview");
		previewButton.addActionListener(setPreviewButton());

		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(setDeleteButton());

		add(pathToVideoLabel);

		add(previewButton);
		add(deleteButton);

		setInitialComponentValues();
		addFocusListenersToComponents();

	}

	/**
	 * This method will update all fields related to its text component object,
	 * when the panel is first initialized.
	 */
	private void setInitialComponentValues() {

		Component[] components = super.getComponents();

		// Set the selected font
		super.getFontsBox().setSelectedIndex(
				TextOverlayWindow.getInstance().getTOpanel().getFontsBox()
						.getSelectedIndex());

		super.getPositionChooser().setSelectedIndex(
				TextOverlayWindow.getInstance().getTOpanel()
						.getPositionChooser().getSelectedIndex());

		for (Component c : components) {
			if (c instanceof JTextArea) {
				((JTextArea) c).setText(textComponent.textToOverlay);
			}

			if (c instanceof TimeInput) {
				((TimeInput) c).setText(textComponent.fontSize);
			}

			if (c instanceof InputPanel) {
				if (((InputPanel) c).getLabelText().equals(
						"Specify start time in hh:mm:ss")) {
					((InputPanel) c).setInputs(textComponent.startInputs);
				} else {
					((InputPanel) c).setInputs(textComponent.endInputs);
				}
			}

			if (c instanceof JButton) {
				if (((JButton) c).getText().equals("Add Text Component")) {
					c.setVisible(false);
				}
			}

			if (c instanceof JLabel) {
				if (!((JLabel) c).getText().contains("Specify")
						&& !(((JLabel) c).getText().contains("Select"))
						&& !(((JLabel) c).getText().contains("/"))) {
					((JLabel) c).setText(textComponent.color);
				}
			}
		}
	}

	private void updateTextComponent() {
		System.out.println("checking");
		
		String videoPath = GuiManager.getInstance().getVideoFilePath();
		
		File selectedFontFile = getFontMap().get(
				getFontsBox().getSelectedItem().toString());
		String selectedFontPath = selectedFontFile.getPath();

		String fontsize = getFontSize().getText();
		String colorString = getColorLabel().getText();

		if (fontsize.equals("")) {
			fontsize = "" + 16;
		}
		if (colorString.equals("ff000000") || colorString.equals(null)) {
			colorString = "000000ff";
		}
		
		textComponent = new TextOverlay(videoPath,
						getTextToOverlay().getText(), selectedFontPath, fontsize,
						colorString,
						TextOverlay.TextPosition.valueOf(getPositionChooser()
								.getSelectedItem().toString()),
						getStartInputPanel().getInputs(), getEndInputPanel().getInputs());
		
		TextOverlayWindow.getInstance().getTOpanel().getTextOverlayComponents().set(tabPosition, textComponent);
		
	}

	public void addFocusListenersToComponents() {
		Component[] components = getComponents();

		for (Component c : components) {
			c.addFocusListener(setFocusListener());
		}
	}

	public FocusListener setFocusListener() {
		return new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				updateTextComponent();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				updateTextComponent();
			}

		};
	}

	public ActionListener setPreviewButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				ArrayList<TextOverlay> textOverlayCommands = TextOverlayWindow.getInstance().getTOpanel().getTextOverlayComponents();
				ArrayList<String> commands = new ArrayList<String>();
				
				String command = "avplay -i " + GuiManager.getInstance().getVideoFilePath()
						+ " -strict experimental -vf \"";
				
				int count = 0;
				
				for (TextOverlay t : textOverlayCommands) {
					count++;
					
					command = command + t.createBashCommand();
					if (!(count==textOverlayCommands.size())) command = command + ","; 
				}
				
				command = command + "\"";
				
				System.out.println(command);
				
				commands.add(command);
				
				CustomSwingWorker csw = new CustomSwingWorker(commands);
				csw.execute();
				
				//AvPreviewPlayer avp = new AvPreviewPlayer();
				//avp.execute();
			}

		};
	}

	private class AvPreviewPlayer extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			
			String cmd = "avplay -i " + textComponent.pathToVideo + " -strict experimental -vf \"" + textComponent.createBashCommand() + "\"";
			System.out.println(cmd);

			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process p;

			pb.redirectErrorStream(true);
			p = pb.start();
			System.out.println(pb.toString());
			String s;
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			StringBuffer sb = new StringBuffer();

			while ((s = stdout.readLine()) != null) {
				// Append avconv's output to a buffer
				sb.append(s + "\n");
				System.out.println(s);
			}

			p.destroy();
			this.cancel(true);

			return null;
		}

	}

	public ActionListener setDeleteButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TextOverlayWindow.getInstance().getTOpanel()
						.getTextOverlayComponents().remove(tabPosition);
				TextOverlayWindow.getInstance().removeTab(tabPosition + 1);
			}

		};
	}

}
