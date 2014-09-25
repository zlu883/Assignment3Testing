package Dialogs;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import AvConvObjects.TextOverlay;
import Commands.BashCommand;
import Commands.CommandManager;
import Main.GuiManager;
import Misc.CustomSwingWorker;


public class ExportWindow extends JFrame {
	private static ExportWindow instance = new ExportWindow();
	
	private JLabel selectFileNameLabel, selectDirectoryLabel;
	
	private JTextField fileNameField, directoryField;
	
	private JButton browseButton, exportButton;
	
	private JProgressBar progressBar;

	private JPanel contentPane;
	
	private Path targetDirectory;
	
	ArrayList<String> pathsToTempFiles;

	/**
	 * Create the frame.
	 */
	private ExportWindow() {

		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		
		setContentPane(contentPane);
		
		selectFileNameLabel = new JLabel("Specify name of file to export");
		//selectFileNameLabel.setBounds(5, 5, 207, 15);
		selectDirectoryLabel = new JLabel("Select the directory to export to");
		//selectDirectoryLabel.setBounds(5, 38, 228, 15);
		
		fileNameField = new JTextField(30);
		//fileNameField.setBounds(33, 150, 334, 19);
		directoryField = new JTextField(30);
		//directoryField.setBounds(12, 52, 334, 19);
		
		browseButton = new JButton("Browse");
		//browseButton.setBounds(351, 49, 87, 25);
		browseButton.addActionListener(setBrowseButton());
		
		exportButton = new JButton("Export");
		//exportButton.setBounds(93, 102, 79, 25);
		exportButton.addActionListener(setExportButton());
		
		progressBar = new JProgressBar();
		//progressBar.setBounds(198, 243, 148, 14);
		progressBar.setEnabled(false);
		contentPane.setLayout(null);
		
		contentPane.add(selectFileNameLabel);
		contentPane.add(selectDirectoryLabel);
		
		contentPane.add(fileNameField);
		contentPane.add(directoryField);
		
		contentPane.add(browseButton);
		contentPane.add(exportButton);
		
		contentPane.add(progressBar);
		
		pathsToTempFiles = new ArrayList<String>();
	}
	
	public void showGUI() {
		setVisible(true);
	}
	
	public static ExportWindow getInstance() {
		return instance;
	}
	
	private ActionListener setBrowseButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// Create a new JFileChooser, and set it so it can only choose
				// folders.
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = chooser.showOpenDialog(getParent());

				// Set the download path by obtaining the given directories
				// path.
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					directoryField
							.setText(chooser.getSelectedFile().getPath());
					targetDirectory = Paths.get(chooser.getSelectedFile()
							.getPath());
				}
			}
			
		};
	}
	
	private ActionListener setExportButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Check if the name field is empty, and if so, prompt the user to
				// enter the name
				if (fileNameField.getText().equals("")) {
					JOptionPane.showMessageDialog(getParent(),
							"Please specify the name of the file to export");
					return;
				}
				// Check if the directory field is empty, if so prompt user to
				// enter directory path.
				if (directoryField.getText().equals("")) {
					JOptionPane.showMessageDialog(getParent(),
							"Please specify the download location");
					return;
				}
				
				// Creates a absolute path to the export location, by combining what
				// the specified directory + file name
				Path exportPath = Paths.get(directoryField.getText() + "/"
						+ fileNameField.getText());
				
				if (Files.exists(exportPath, LinkOption.NOFOLLOW_LINKS)) {
					Object[] options = {"Overwrite", "Cancel" };
					int result = JOptionPane.showOptionDialog(null,
							"The current file you are trying to save already "
									+ "exist. Do you want to overwrite or cancel?",
							"File already exists", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					switch (result) {
					case 0:
						// If overwrite chosen, then delete the existing file.
						try {
							Files.delete(FileSystems.getDefault().getPath(
									fileNameField.getText()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
	
					default:
						// If the option dialog was closed without choosing to
						// over write or was cancelled, then cancel the
						// operation.
						JOptionPane.showMessageDialog(null,
								"Operation Cancelled");
						return;
					}
				}
				
				ArrayList<TextOverlay> textOverlays = TextOverlayWindow.getInstance().getTOpanel().getTextOverlayComponents();
				ArrayList<String> commands = new ArrayList<String>();
				
				BashCommand command = CommandManager.drawtextCommand(GuiManager.getInstance().getVideoFilePath(), exportPath.toString(), textOverlays);
			}
			
		};
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public void deleteTempFiles() {
		for (String s : pathsToTempFiles) {
			try {
				Files.delete(FileSystems.getDefault().getPath(s));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addToTempFiles(String path) {
		pathsToTempFiles.add(path);
	}

}
