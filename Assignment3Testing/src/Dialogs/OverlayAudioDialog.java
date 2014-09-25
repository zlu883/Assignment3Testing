package Dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.MaskFormatter;

import Commands.BashCommand;
import Commands.CommandManager;
import Main.GuiManager;


public class OverlayAudioDialog {

	private JDialog _overlayDialog;
	private JTextField _outputFileField, _audioFileField;
	private JFormattedTextField _startTimeField;
	private JComboBox<String> _fileFormatBox;
	
	private static final String TEMP_FILE = "temp.wav";
	private static final String TEMP_FILE2 = "temp2.wav";
	private static final String TEMP_FILE3 = "temp3.wav";
	private static final String TEMP_FILE4 = "temp4.wav";
	
	private String _sourceVideo;
	
	private static int GAPSIZE = 10;

	public OverlayAudioDialog(JFrame parent, String sourceVideo) {
		
		_sourceVideo = sourceVideo;
		
		_overlayDialog = new JDialog(parent, "Overlay Audio Track");
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JLabel labelA = new JLabel("Please select overlay audio track:");
		labelA.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelA);
		JPanel chooseFilePanelA = new JPanel();
		chooseFilePanelA.setAlignmentX(Component.CENTER_ALIGNMENT);
		_audioFileField = new JTextField(20);
		JButton chooseAudioFileButton = new JButton("Browse...");
		chooseAudioFileButton.addActionListener(setChooseAudioFile());
		chooseFilePanelA.add(_audioFileField);
		chooseFilePanelA.add(chooseAudioFileButton);
		contentPane.add(chooseFilePanelA);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JPanel startTimePanel = new JPanel();
			startTimePanel.add(new JLabel("Please enter start time for overlay:"));
			try {
				_startTimeField = new JFormattedTextField(new MaskFormatter("##:##:##"));
			} catch (ParseException e) {}
			_startTimeField.setColumns(5);
			_startTimeField.setText("00:00:00");
			startTimePanel.add(_startTimeField);
			contentPane.add(startTimePanel);
			contentPane.add(Box.createVerticalStrut(GAPSIZE));
			contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
			
			contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JLabel labelB = new JLabel("Please select output destination:");
		labelB.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelB);
		JPanel chooseFilePanelB = new JPanel();
		chooseFilePanelB.setAlignmentX(Component.CENTER_ALIGNMENT);
		_outputFileField = new JTextField(20);
		JButton chooseFileButton = new JButton("Browse...");
		chooseFileButton.addActionListener(setChooseFile());
		chooseFilePanelB.add(_outputFileField);
		chooseFilePanelB.add(chooseFileButton);
		contentPane.add(chooseFilePanelB);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		JLabel labelC = new JLabel("Please enter video output format:");
		labelC.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelC);
		contentPane.add(Box.createVerticalStrut(5));
		String[] formats = { "Use source video format", "mp4", "avi", "mpg"};
		_fileFormatBox = new JComboBox<String>(formats);
		_fileFormatBox.setMaximumSize(new Dimension(250,100));
		_fileFormatBox.setPreferredSize(new Dimension(250,20));
		_fileFormatBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(_fileFormatBox);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		
		JButton exportButton = new JButton("Export");
		exportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exportButton.addActionListener(setOverlayButton());
		contentPane.add(exportButton);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));

		_overlayDialog.setContentPane(contentPane);
		_overlayDialog.setLocation(100, 100);
		_overlayDialog.pack();
		_overlayDialog.setVisible(true);
	}

	private ActionListener setChooseAudioFile() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_audioFileField.setText(GuiManager.promptFileChooser(GuiManager.SAVE, "Select overlay audio file", _overlayDialog));		
			}
			
		};
	}

	private ActionListener setChooseFile() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_audioFileField.setText(GuiManager.promptFileChooser(GuiManager.OPEN, "Select overlay audio file", _overlayDialog));
				
			}	
		};
	}
	
	private ActionListener setOverlayButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String format = (String)_fileFormatBox.getSelectedItem();
				BashCommand cmd = CommandManager.extractAudioCommand(_sourceVideo, TEMP_FILE);
				cmd.run();
				cmd = CommandManager.extractDurationCommand(TEMP_FILE, _startTimeField.getText(), TEMP_FILE3);
				cmd.run();
				cmd = CommandManager.concatAudioCommand(TEMP_FILE3, _audioFileField.getText(), TEMP_FILE4);
				cmd.run();
				cmd = CommandManager.overlayAudioCommand(TEMP_FILE, TEMP_FILE4, TEMP_FILE2);
				cmd.run();
				if (format.equals("Use source video format")) {
					cmd = CommandManager.replaceAudioCommand(_sourceVideo, TEMP_FILE2, _outputFileField.getText() + 
							"." + GuiManager.getExtension(new File(_sourceVideo)));
					cmd.run();
				} else {
										cmd = CommandManager.replaceAudioCommand(_sourceVideo, TEMP_FILE2, _outputFileField.getText() + "." +
											(String)_fileFormatBox.getSelectedItem());
									cmd.run();
					 				}
								new File(TEMP_FILE).delete();
								new File(TEMP_FILE2).delete();
								new File(TEMP_FILE3).delete();
								new File(TEMP_FILE4).delete();
			}
		};
	}
}
