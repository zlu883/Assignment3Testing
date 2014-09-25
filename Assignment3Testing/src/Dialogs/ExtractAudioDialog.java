package Dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Commands.BashCommand;
import Commands.CommandManager;
import Main.GuiManager;

public class ExtractAudioDialog {
	
	private JDialog _extractDialog;
	private JTextField _outputFileField;
	private JComboBox<String> _fileFormatBox;
	
	private String _sourceVideo;
	
	private static int GAPSIZE = 10;

	public ExtractAudioDialog(JFrame parent, String sourceVideo) {
		
		_sourceVideo = sourceVideo;
		
		_extractDialog = new JDialog(parent, "Extract Audio");
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JLabel labelA = new JLabel("Please select output destination:");
		labelA.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelA);
		JPanel chooseFilePanel = new JPanel();
		chooseFilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		_outputFileField = new JTextField(20);
		JButton chooseFileButton = new JButton("Browse...");
		chooseFileButton.addActionListener(setChooseFile());
		chooseFilePanel.add(_outputFileField);
		chooseFilePanel.add(chooseFileButton);
		contentPane.add(chooseFilePanel);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JLabel labelB = new JLabel("Please enter audio output format:");
		labelB.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelB);
		contentPane.add(Box.createVerticalStrut(5));
		String[] formats = { "mp3", "wav" };
		_fileFormatBox = new JComboBox<String>(formats);
		_fileFormatBox.setMaximumSize(new Dimension(150,100));
		_fileFormatBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(_fileFormatBox);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		
		JButton extractButton = new JButton("Extract");
		extractButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		extractButton.addActionListener(setExtractButton());
		contentPane.add(extractButton);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));

		_extractDialog.setContentPane(contentPane);
		_extractDialog.setLocation(100, 100);
		_extractDialog.pack();
		_extractDialog.setVisible(true);
	}

	private ActionListener setChooseFile() {
		return new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				_outputFileField.setText(GuiManager.promptFileChooser(GuiManager.SAVE, "Select output destination", _extractDialog));
				
			}	
		};
	}
	
	private ActionListener setExtractButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BashCommand cmd = CommandManager.extractAudioCommand(_sourceVideo, _outputFileField.getText() + "." +
						(String)_fileFormatBox.getSelectedItem());
				cmd.run();
			}
		};
	}
}
