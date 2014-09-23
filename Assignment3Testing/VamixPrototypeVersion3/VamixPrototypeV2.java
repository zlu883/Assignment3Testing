import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class VamixPrototypeV2 {
	private static VamixPrototypeV2 instance = new VamixPrototypeV2();
	
	private final EmbeddedMediaPlayerComponent _embeddedMediaPlayer;
	private MediaPlayer _mediaPlayer;

	private JFrame _vamixWindow;

	private JPanel _mainPanel;
	private JPanel _videoDisplayPanel;
	private JPanel _videoControlsPanel;

	private JMenuBar _menuBar;
	private JMenu _fileMenu, _toolsMenu;
	private JMenuItem _openFile, _downloadFile, _saveFile, _extractAudio,
			_overlayAudio, _overlayText;

	private JButton _playButton, _muteButton, _forwardButton, _rewindButton;

	private JLabel _timeLabel, _fileLabel, _soundLabel;

	private JSlider _volumeSlider;

	private JProgressBar _videoProgressBar;

	private Timer _timeCount;

	private File _inputFile;
	private String _currentPlayingFile;

	public static void main(String[] args) {
		VamixPrototypeV2.getInstance().showGUI();
	}

	private VamixPrototypeV2() {

		_vamixWindow = new JFrame("Vamix Prototype");

		_embeddedMediaPlayer = new EmbeddedMediaPlayerComponent();
		//_embeddedMediaPlayer.setPreferredSize(new Dimension(700, 400));
		_mediaPlayer = _embeddedMediaPlayer.getMediaPlayer();

		_mainPanel = new JPanel(new BorderLayout());
		_videoDisplayPanel = new JPanel(new BorderLayout());
		_videoDisplayPanel.setPreferredSize(new Dimension(800, 460));

		_videoControlsPanel = new JPanel();

		_menuBar = createJMenuBar();

		_vamixWindow.setJMenuBar(_menuBar);

		_playButton = new JButton("Play");
		_forwardButton = new JButton("Forward");
		_rewindButton = new JButton("Rewind");
		_muteButton = new JButton("Mute");

		_playButton.setEnabled(false);
		_forwardButton.setEnabled(false);
		_rewindButton.setEnabled(false);
		_muteButton.setEnabled(false);

		_playButton.addActionListener(setPlayButton());
		_muteButton.addActionListener(setMuteButton());
		_forwardButton.addActionListener(setForwardButton());
		_rewindButton.addActionListener(setRewindButton());

		Dimension d = new Dimension(700, 20);
		_videoProgressBar = new JProgressBar();
		_videoProgressBar.setPreferredSize(d);
		_videoProgressBar.addMouseListener(setVideoProgressBar());
		_videoProgressBar.setStringPainted(true);
		_videoProgressBar.setString("--:--:--/--:--:--");

		_volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 50);
		_volumeSlider.addChangeListener(setVolumeBar());

		_timeLabel = new JLabel("");
		_fileLabel = new JLabel("Please select a file to play");
		_soundLabel = new JLabel("        Adjust Volume");

		_videoDisplayPanel.add(BorderLayout.NORTH, _fileLabel);
		_videoDisplayPanel.add(BorderLayout.CENTER, _embeddedMediaPlayer);
		_videoDisplayPanel.add(BorderLayout.SOUTH, _videoProgressBar);

		_videoControlsPanel.add(_playButton);
		_videoControlsPanel.add(_muteButton);
		_videoControlsPanel.add(_forwardButton);
		_videoControlsPanel.add(_rewindButton);
		_videoControlsPanel.add(_soundLabel);
		_videoControlsPanel.add(_volumeSlider);

		_mainPanel.add(BorderLayout.NORTH, _videoDisplayPanel);
		_mainPanel.add(BorderLayout.SOUTH, _videoControlsPanel);

		_vamixWindow.setContentPane(_mainPanel);

		_vamixWindow.pack();

		_vamixWindow.setLocation(100, 100);
		_vamixWindow.setSize(1050, 600);
		_vamixWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_mediaPlayer.setVolume(_volumeSlider.getValue());

		_timeCount = new Timer(1, setTimer());
	}
	
	public static VamixPrototypeV2 getInstance() {
		return instance;
	}
	
	public void showGUI() {
		_vamixWindow.setVisible(true);

		_embeddedMediaPlayer.setVisible(true);
	}
	
	public String getVideoFilePath() {
		return _inputFile.getPath();
	}

	private ActionListener setPlayButton() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_mediaPlayer.isPlaying()) {
					_playButton.setText("Play");
					_mediaPlayer.pause();
					_timeCount.stop();
				} else {
					_playButton.setText("Pause");
					_mediaPlayer.play();
					_timeCount.start();

					_videoProgressBar.setMaximum((int) _mediaPlayer
							.getMediaMeta().getLength() / 1000);
				}
			}
		};
	}

	private ActionListener setMuteButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (_mediaPlayer.isMute()) {
					_muteButton.setText("Mute");
					_mediaPlayer.mute(false);
				} else {
					_mediaPlayer.mute();
					_muteButton.setText("Unmute");
				}
			}
		};
	}

	private ActionListener setForwardButton() {
		return new ActionListener() {

			Timer timer = new Timer(100, new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					_mediaPlayer.skip(1000);
				}
			});

			public void actionPerformed(ActionEvent e) {
				if (_forwardButton.getText() == "Forward") {
					_forwardButton.setText("Resume");
					_playButton.setEnabled(false);
					_rewindButton.setEnabled(false);
					timer.start();
				} else if (_forwardButton.getText() == "Resume") {
					_forwardButton.setText("Forward");
					_playButton.setEnabled(true);
					_rewindButton.setEnabled(true);
					timer.stop();
				}
			}
		};
	}

	private ActionListener setRewindButton() {
		return new ActionListener() {
			Timer timer = new Timer(100, new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					_mediaPlayer.skip(-1000);
				}
			});

			public void actionPerformed(ActionEvent e) {
				if (_rewindButton.getText() == "Rewind") {
					_rewindButton.setText("Resume");
					_playButton.setEnabled(false);
					_forwardButton.setEnabled(false);
					timer.start();
				} else if (_rewindButton.getText() == "Resume") {
					_rewindButton.setText("Rewind");
					_playButton.setEnabled(true);
					_forwardButton.setEnabled(true);
					timer.stop();
				}
			}
		};
	}

	private ActionListener setTimer() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int currentTime = (int) _mediaPlayer.getTime() / 1000;
				// _timeLabel.setText("" + currentTime);
				// _timeBar.setValue(currentTime);

				_videoProgressBar.setValue(currentTime);
				_videoProgressBar.setString(convertSecondsToTime(currentTime)
						+ "/"
						+ convertSecondsToTime((int) (_mediaPlayer
								.getMediaMeta().getLength() / 1000)));
			}

		};
	}

	private ChangeListener setVolumeBar() {
		return new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				_mediaPlayer.setVolume(_volumeSlider.getValue());

			}
		};
	}

	private MouseAdapter setVideoProgressBar() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int v = _videoProgressBar.getValue();

				// Retrieves the mouse position relative to the component
				// origin.
				int mouseX = e.getX();

				// Computes how far along the mouse is relative to the component
				// width then multiply it by the progress bar's maximum value.
				int progressBarVal = (int) Math
						.round(((double) mouseX / (double) _videoProgressBar
								.getWidth()) * _videoProgressBar.getMaximum());

				if (_mediaPlayer.isPlaying())
					_videoProgressBar.setValue(progressBarVal);

				_mediaPlayer.setTime(progressBarVal * 1000);

			}
		};
	}

	private ActionListener setExtractAudioButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					String fileName = JOptionPane
							.showInputDialog("Please input audio file name");
					System.out.println(fileName);

					String cmd = "avconv -i " + _currentPlayingFile + " -vn "
							+ fileName;

					ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c",
							cmd);
					pb.redirectErrorStream(true);
					Process p = pb.start();

					InputStream stdout = p.getInputStream();

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(stdout));

					String s = null;

					while ((s = reader.readLine()) != null) {
						System.out.println(s);
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
	}

	private ActionListener setChooseMediaFileButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					_inputFile = new File(chooser.getSelectedFile()
							.getPath());
					_currentPlayingFile = _inputFile.getPath();
					_fileLabel.setText(_inputFile.getName());
					_playButton.setEnabled(true);
					_forwardButton.setEnabled(true);
					_rewindButton.setEnabled(true);
					_muteButton.setEnabled(true);
					_mediaPlayer.prepareMedia(_currentPlayingFile);
					_mediaPlayer.parseMedia();
				}
			}
		};
	}
	
	private ActionListener setExportButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				for (TextOverlay t : TextOverlayWindow.getInstance().getTOpanel().getTextOverlayComponents()) {
					t.runBashCommand();
				}
			}
			
		};
	}

	private JMenuBar createJMenuBar() {
		JMenuBar menu = new JMenuBar();
		_fileMenu = new JMenu("File");
		_toolsMenu = new JMenu("Tools");

		JMenuItem _openFile = new JMenuItem("Open File...");
		_openFile.addActionListener(setChooseMediaFileButton());
		_fileMenu.add(_openFile);

		JMenuItem _downloadMedia = new JMenuItem("Download Audio or Video");
		_downloadMedia.addActionListener(setDownloadButton());
		_fileMenu.add(_downloadMedia);
		
		JMenuItem _exportProject = new JMenuItem("Export Project");
		_exportProject.addActionListener(setExportButton());
		_fileMenu.add(_exportProject);

		JMenuItem _extractAudio = new JMenuItem("Extract Audio");
		_extractAudio.addActionListener(setExtractAudioButton());
		_toolsMenu.add(_extractAudio);

		JMenuItem _overlayText = new JMenuItem("Overlay Text");
		_overlayText.addActionListener(setOverlayTextButton());
		_toolsMenu.add(_overlayText);

		menu.add(_fileMenu);
		menu.add(_toolsMenu);

		return menu;
	}

	private ActionListener setOverlayTextButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TextOverlayWindow.getInstance().showGUI();
			}

		};
	}

	private ActionListener setDownloadButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame downloadWindow = new JFrame(
						"Download Audio or Video File");
				downloadWindow.setContentPane(new DownloadPanel());

				downloadWindow.setLocation(100, 100);
				downloadWindow.setSize(350, 400);
				downloadWindow.setVisible(true);

			}

		};
	}

	public String convertSecondsToTime(int seconds) {
		int hr = seconds / 3600;
		int rem = seconds % 3600;
		int mn = rem / 60;
		int sec = rem % 60;
		String hrStr = (hr < 10 ? "0" : "") + hr;
		String mnStr = (mn < 10 ? "0" : "") + mn;
		String secStr = (sec < 10 ? "0" : "") + sec;

		return hrStr + ":" + mnStr + ":" + secStr;
	}

}
