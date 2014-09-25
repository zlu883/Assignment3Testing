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


public class RemoveAudioDialog {

	private JDialog _removeDialog;
	private JTextField _outputFileField;
	private JComboBox<String> _fileFormatBox;
	
	private String _sourceVideo;
	
	private static int GAPSIZE = 10;

	public RemoveAudioDialog(JFrame parent, String sourceVideo) {
		
		_sourceVideo = sourceVideo;
		
		_removeDialog = new JDialog(parent, "Remove Audio Track");
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
		JLabel labelB = new JLabel("Please enter video output format:");
		labelB.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(labelB);
		contentPane.add(Box.createVerticalStrut(5));
		String[] formats = { "Use source video format", "mp4", "avi", "mpg" };
		_fileFormatBox = new JComboBox<String>(formats);
		_fileFormatBox.setMaximumSize(new Dimension(150,100));
		_fileFormatBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(_fileFormatBox);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		contentPane.add(Box.createVerticalStrut(GAPSIZE));
		
		JButton extractButton = new JButton("Export");
		extractButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		extractButton.addActionListener(setExtractButton());
		contentPane.add(extractButton);
		contentPane.add(Box.createVerticalStrut(GAPSIZE));

		_removeDialog.setContentPane(contentPane);
		_removeDialog.setLocation(100, 100);
		_removeDialog.pack();
		_removeDialog.setVisible(true);
	}

	private ActionListener setChooseFile() {
		return new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				_outputFileField.setText(GuiManager.promptFileChooser(GuiManager.SAVE, "Select output destination", _removeDialog));
				
			}	
		};
	}
	
	private ActionListener setExtractButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String format = (String)_fileFormatBox.getSelectedItem();
				if (format.equals("Use source video format")) {
					BashCommand cmd = CommandManager.removeAudioCommand(_sourceVideo, _outputFileField.getText() + "." +
							GuiManager.getExtension(new File(_sourceVideo)));
					cmd.run();
				}
			}
		};
	}
}
