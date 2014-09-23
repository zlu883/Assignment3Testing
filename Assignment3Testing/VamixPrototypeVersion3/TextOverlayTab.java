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
	
	//This field represents its index position in the tabbed pane component.
	private int tabPosition;

	private JLabel pathToVideoLabel;

	private JButton previewButton, deleteButton;

	public TextOverlayTab(TextOverlay textComponent, int tabPosition) {
		this.textComponent = textComponent;
		
		this.tabPosition = tabPosition;

		pathToVideoLabel = new JLabel(VamixPrototypeV2.getInstance()
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

		for (Component c : components) {
			if (c instanceof JTextArea) {
				((JTextArea) c).setText(textComponent.textToOverlay);
			}

			if (c instanceof JComboBox) {
				((JComboBox) c)
						.setSelectedIndex(textComponent.fontIndexInFontChooser);
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
				if (!((JLabel) c).getText().contains("Specify") && !(((JLabel) c).getText().contains("Select"))
						&& !(((JLabel)c).getText().contains("/"))) {
					((JLabel) c).setText(textComponent.color);
				}
			}
		}
	}
	
	private void updateTextComponent(Component c) {
		if (c instanceof JTextArea) {
			textComponent.textToOverlay = ((JTextArea) c).getText();
		}

		if (c instanceof JComboBox) {
			textComponent.pathToFont = getFontMap().get(((JComboBox) c).getSelectedItem().toString()).getPath();
		}

		if (c instanceof TimeInput) {
			textComponent.fontSize = ((TimeInput) c).getText();
		}

		if (c instanceof InputPanel) {
			if (((InputPanel) c).getLabelText().equals(
					"Specify start time in hh:mm:ss")) {
				textComponent.startInputs = ((InputPanel) c).getInputs();
			} else {
				((InputPanel) c).setInputs(textComponent.endInputs);
				textComponent.endInputs = ((InputPanel) c).getInputs();
			}
		}

		if (c instanceof JButton) {
			if (((JButton) c).getText().equals("Add Text Component")) {
				c.setVisible(false);
			}
		}
		
		if (c instanceof JLabel) {
			if (!((JLabel) c).getText().contains("Specify") && !(((JLabel) c).getText().contains("Select"))
					&& !(((JLabel)c).getText().contains("/"))) {
				textComponent.color = ((JLabel) c).getText();
			}
		}
	}
	
	public void addFocusListenersToComponents() {
		Component[] components = super.getComponents();
		
		for (Component c : components) {
			c.addFocusListener(setFocusListener(c));
		}
	}
	
	
	public FocusListener setFocusListener(final Component c) {
		return new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				updateTextComponent(c);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				updateTextComponent(c);
			}
			
		};
	}
	
	public ActionListener setPreviewButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				AvPreviewPlayer player = new AvPreviewPlayer();
				player.execute();
			}
			
		};
	}
	
	private class AvPreviewPlayer extends SwingWorker <Void, Void> {
	
		@Override
		protected Void doInBackground() throws Exception {
			String color = "0xff" + textComponent.color;
			
			int startSeconds = Integer.parseInt(textComponent.startInputs[0]) * 3600
					+ Integer.parseInt(textComponent.startInputs[1]) * 60
					+ Integer.parseInt(textComponent.startInputs[2]);
			int endSeconds = Integer.parseInt(textComponent.endInputs[0]) * 3600
					+ Integer.parseInt(textComponent.endInputs[1]) * 60
					+ Integer.parseInt(textComponent.endInputs[2]);
			
			String cmd = "avplay -i " + textComponent.pathToVideo
					+ " -strict experimental -vf \"drawtext=fontfile='"
					+ textComponent.pathToFont + "':text='" + textComponent.textToOverlay + "':fontsize='"
					+ textComponent.fontSize + "':" + "fontcolor='" + textComponent.color
					+ "': draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
					+ ")'\"";
			System.out.println(cmd);
			//ProcessBuilder pb = new ProcessBuilder ("avplay", "-i", textComponent.pathToVideo, "-vf",
					//"\"drawtext=" + "fontfile='"
					//	+ textComponent.pathToFont + "':text='" + textComponent.textToOverlay + "':fontsize='"
						//	+ textComponent.fontSize + "':" + "fontcolor='" + textComponent.color
						//+ "':draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
						//+ ")'\"");
			//String cmd = "avplay -i ~/Videos/big_buck_bunny.mp4 -strict experimental -vf \"drawtext=fontfile='/usr/share/fonts/truetype/fonts-japanese-gothic.ttf':text='poop':fontsize='50':fontcolor='0xffffcc66': draw='gt(t,5)*lt(t,10)'\"";

			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process p;

			pb.redirectErrorStream(true);
			p = pb.start();
			System.out.println(pb.toString());
			String s;
			BufferedReader stdout = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

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
				TextOverlayWindow.getInstance().getTOpanel().getTextOverlayComponents().remove(tabPosition);
				TextOverlayWindow.getInstance().removeTab(tabPosition + 1);
			}
			
		};
	}

}
