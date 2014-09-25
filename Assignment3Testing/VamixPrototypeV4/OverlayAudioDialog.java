import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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


public class OverlayAudioDialog {

	private JDialog _overlayDialog;
	private JTextField _outputFileField, _audioFileField;
	private JComboBox<String> _fileFormatBox;
	
	private static final String TEMP_FILE = "temp.wav";
	private static final String TEMP_FILE2 = "temp2.wav";
	
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
		String[] formats = { "Use source video format" };
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
				_outputFileField.setText(GuiManager.promptFileChooser(GuiManager.SAVE, "Select output destination", _overlayDialog));
				
			}	
		};
	}
	
	private ActionListener setOverlayButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String format = (String)_fileFormatBox.getSelectedItem();
				if (format.equals("Use source video format")) {
					BashCommand cmd = CommandManager.extractAudioCommand(_sourceVideo, TEMP_FILE);
					cmd.run();
					cmd = CommandManager.overlayAudioCommand(TEMP_FILE, _audioFileField.getText(), TEMP_FILE2);
					cmd.run();
					cmd = CommandManager.replaceAudioCommand(_sourceVideo, TEMP_FILE2, _outputFileField.getText() + 
							"." + GuiManager.getExtension(new File(_sourceVideo)));
					cmd.run();
					new File(TEMP_FILE).delete();
					new File(TEMP_FILE2).delete();
				}
			}
		};
	}
}
