package Misc;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * This panel will hold all the necessary components for the user to: specify a
 * url to download, to specify a directory for which the file should be saved,
 * download the actual file, cancel the download, and display a progress bar.
 */
public class DownloadPanel extends JPanel {
	private JTextField urlField;

	private JLabel targetDirectoryLabel;
	private JTextField targetDirectory;
	private JButton browseDirectoryButton;
	private Path downloadLocation;

	private JButton downloadButton;
	private JButton cancelButton;

	private JLabel downloadLabel;
	private JProgressBar progressBar;

	private ProcessBuilder pb;
	private Process p;

	private String fileName;

	private SwingDownloader download;

	// The output of wget will be put in a buffer
	private StringBuffer wgetOutput;

	/**
	 * Instantiate all the necessary ui components for the download panel, then
	 * add them to the panel.
	 */
	public DownloadPanel() {
		downloadLabel = new JLabel("Enter url of MP3 to download:");

		urlField = new JTextField(30);

		downloadButton = new JButton("Download");

		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);

		targetDirectoryLabel = new JLabel(
				"PLease choose the directory to save your file");
		targetDirectory = new JTextField(20);
		browseDirectoryButton = new JButton("Browse");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		setUpWgetButton();
		setUpCancelButton();
		setUpBrowseDirectoryButton();

		this.add(downloadLabel);
		this.add(urlField);

		this.add(targetDirectoryLabel);
		this.add(targetDirectory);
		this.add(browseDirectoryButton);

		this.add(downloadButton);
		this.add(cancelButton);

		this.add(progressBar, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	/**
	 * 
	 * This class will override the methods found in swing worker. It will be
	 * used to create a background process that uses the wget download process,
	 * and will update any interim results.
	 * 
	 */
	private class SwingDownloader extends SwingWorker<Void, Integer> {

		/**
		 * Set the wget process to a background thread.
		 */
		protected Void doInBackground() {
			try {

				// Create the wget process. Uses the "-P" modifier to set the
				// target directory.
				// Use the "-c" option to resume partial downloads of files.
				pb = new ProcessBuilder("wget", "-P",
						targetDirectory.getText(), "--progress=bar:force",
						"-c", urlField.getText());

				pb.redirectErrorStream(true);
				p = pb.start();
				String s;
				BufferedReader stdout = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				wgetOutput = new StringBuffer();

				while ((s = stdout.readLine()) != null && !isCancelled()) {
					wgetOutput.append(s + "\n");

					// Obtain the percentage integer from wget's output.
					Pattern varPattern = Pattern.compile("(\\d*)\\%");

					Matcher matcher = varPattern.matcher(s);

					while (matcher.find()) {
						String var = matcher.group(1);
						// Publish the percentage integer to update the progress
						// bar
						publish(Integer.parseInt(var));
					}
				}

				// If the background thread is cancelled, destroy the linux
				// process and exit doInBackground();
				if (isCancelled()) {
					p.destroy();
					return null;
				}

				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
				p.destroy();

			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return null;
		}

		/**
		 * Update the progress bar from the values given from the publish
		 * method.
		 */
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				progressBar.setValue(i);
				progressBar.setForeground(Color.red);
				progressBar.setString(progressBar.getValue() + "%");
			}
		}

		/**
		 * Depending on the process's exit value or if swing workers background
		 * thread was cancelled, it will display a message.
		 */
		protected void done() {
			if (isCancelled()) { // If the download operation was cancelled,
									// display "Download was cancelled" and update progress bar
				JOptionPane.showMessageDialog(null, "Download was cancelled");
				progressBar.setValue(0);
				progressBar.setString("0%");
			} else if (p.exitValue() == 0) {
				// If the operation was successful, inform use of completion by
				// updating the progress bar
				progressBar.setStringPainted(true);
				progressBar.setForeground(Color.green);
				progressBar.setString("Complete!");

				cancelButton.setEnabled(false);
				downloadButton.setEnabled(true);
			} else { // If an error occurred, display wget's output
				JOptionPane.showMessageDialog(null, wgetOutput.toString());
			}
		}

	}

	/**
	 * Sets up the download button, so it will call SwingDownloaders execute
	 * method.
	 */
	private void setUpWgetButton() {
		downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				// Check if the url field is empty, if so prompt user to enter
				// url.
				if (urlField.getText().equals("")) {
					JOptionPane.showMessageDialog(getParent(),
							"Please specify a url");
					return;
				}

				// Check if the directory field is empty, if so prompt user to
				// enter directory path.
				if (targetDirectory.getText().equals("")) {
					JOptionPane.showMessageDialog(getParent(),
							"Please specify the download location");
					return;
				}

				// Obtains the filename from the url string
				fileName = urlField.getText().substring(
						urlField.getText().lastIndexOf('/') + 1,
						urlField.getText().length());

				// Creates a path to the download location, by combining what
				// the specified directory + file name
				downloadLocation = Paths.get(targetDirectory.getText() + "/"
						+ fileName);

				// If a file with the given file name already exists, then
				// prompt the user to resume the partial download
				// Or over write the existing file.
				if (Files.exists(downloadLocation, LinkOption.NOFOLLOW_LINKS)) {
					System.out.println("check");
					Object[] options = { "Resume Download", "Overwrite" };
					int result = JOptionPane.showOptionDialog(null,
							"The current file you are trying to download seems to already "
									+ "exist,\n resume the download or"
									+ " overwrite the existing file?",
							"File already exists", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

					switch (result) {
					case 0:
						// If resume download chosen, then do nothing.
						break;
					case 1:
						// If over write chosen, delete the existing file.
						try {
							Files.delete(FileSystems.getDefault().getPath(
									fileName));
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					default:
						// If the option dialog was closed without choosing to
						// resume or over write, then cancel the
						// download operation.
						JOptionPane.showMessageDialog(null,
								"Download cancelled");
						return;
					}

				}

				// Prompt the use to confirm the file is open source, if not,
				// then cancel the download.
				Object[] options = { "Yes", "No" };
				int result = JOptionPane
						.showOptionDialog(null, "Confirm file is open source",
								"Open Source only", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

				if (result == 1) {
					JOptionPane.showMessageDialog(null,
							"File must be open source!");
					return;
				} else if (result != 0) {
					// If the option dialog was closed, then cancel the
					// download.
					JOptionPane.showMessageDialog(null, "Download cancelled");
					return;
				}

				// Enable cancel button, and disable download button.
				cancelButton.setEnabled(true);
				downloadButton.setEnabled(false);

				// Create a new swing worker and execute it.
				download = new SwingDownloader();
				download.execute();
			}
		});
	}

	/**
	 * This sets up the cancel button so that it will stop the swing workers
	 * current running background thread.
	 */
	private void setUpCancelButton() {
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				download.cancel(true);

				// Disable cancel button, and enable download button.
				cancelButton.setEnabled(false);
				downloadButton.setEnabled(true);
			}

		});
	}

	/**
	 * Sets up the browse button so that when clicked, it will open a File
	 * Chooser, that will allow the user to specify the directory for which the
	 * file should be saved.
	 */
	private void setUpBrowseDirectoryButton() {
		browseDirectoryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create a new JFileChooser, and set it so it can only choose
				// folders.
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = chooser.showOpenDialog(getParent());

				// Set the download path by obtaining the given directories
				// path.
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetDirectory
							.setText(chooser.getSelectedFile().getPath());
					downloadLocation = Paths.get(chooser.getSelectedFile()
							.getPath());
				}

			}

		});
	}
}
