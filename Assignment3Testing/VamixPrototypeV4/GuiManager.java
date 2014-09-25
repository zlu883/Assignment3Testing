import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class GuiManager {
	private static GuiManager instance = new GuiManager();
	
	public static final int OPEN = 0;
	public static final int SAVE = 1;
	
	private EmbeddedMediaPlayerComponent _embeddedMediaPlayer;
	private MediaPlayer _mediaPlayer;

	private JFrame _vamixWindow;

	private JPanel _mainPanel, _videoDisplayPanel, _videoControlsPanel, _videoBarPanel;

	private JMenuBar _menuBar;
	private JMenu _mediaMenu, _audioMenu, _effectsMenu;
	private JMenuItem _playMedia, _downloadMedia, _extractAudio,_overlayAudio, _replaceAudio, 
		_removeAudio, _overlayText;

	private JButton _playButton, _stopButton, _muteButton, _forwardButton, _rewindButton;

	private JLabel _fileLabel, _volumeLabel, _currentTimeLabel, _totalTimeLabel;

	private JSlider _volumeSlider, _videoBar;

	private Timer _timeCount;
	private boolean _userInput = true;
	
	private String _currentPlayingFile = null;
	
	private GuiManager() {};
	
	public static GuiManager getInstance() {
		return instance;
	}
	
	public JFrame createVamixWindow() {
		
		_vamixWindow = new JFrame("Vamix Prototype");

		_mainPanel = new JPanel(new BorderLayout());
		
		setUpMenuBar();
		setUpDisplayPanel();
		setUpControlsPanel();
		
		_mainPanel.add(BorderLayout.CENTER, _videoDisplayPanel);
		_mainPanel.add(BorderLayout.SOUTH, _videoControlsPanel);
		
		_vamixWindow.setContentPane(_mainPanel);
		_vamixWindow.setJMenuBar(_menuBar);
		
		_mediaPlayer.setVolume(_volumeSlider.getValue());

		_timeCount = new Timer(10, setTimer());

		_vamixWindow.setLocation(100, 100);
		_vamixWindow.setSize(1050, 600);
		_vamixWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_vamixWindow.setVisible(true);
		
		return _vamixWindow;
		
	}
	
	private void setUpMenuBar() {
		
		_menuBar = new JMenuBar();
		_mediaMenu = new JMenu("Media");
		_audioMenu = new JMenu("Audio");
		_effectsMenu = new JMenu("Effects");

		_playMedia = new JMenuItem("Play Media File...");
		_playMedia.addActionListener(setChooseMediaFileButton());
		_mediaMenu.add(_playMedia);

		_downloadMedia = new JMenuItem("Download Media File...");
		_downloadMedia.addActionListener(setDownloadButton());
		_mediaMenu.add(_downloadMedia);

		_extractAudio = new JMenuItem("Extract Audio...");
		_extractAudio.addActionListener(setExtractAudioButton());
		_audioMenu.add(_extractAudio);
		
		_replaceAudio = new JMenuItem("Replace Audio...");
		_replaceAudio.addActionListener(setReplaceAudioButton());
		_audioMenu.add(_replaceAudio);
		
		_overlayAudio = new JMenuItem("Overlay Audio...");
		_overlayAudio.addActionListener(setOverlaytAudioButton());
		_audioMenu.add(_overlayAudio);
		
		_removeAudio = new JMenuItem("Remove Audio...");
		_removeAudio.addActionListener(setRemoveAudioButton());
		_audioMenu.add(_removeAudio);

		_overlayText = new JMenuItem("Overlay Text...");
		_overlayText.addActionListener(setOverlayTextButton());
		_effectsMenu.add(_overlayText);
		
		_audioMenu.setEnabled(false);
		_effectsMenu.setEnabled(false);

		_menuBar.add(_mediaMenu);
		_menuBar.add(_audioMenu);
		_menuBar.add(_effectsMenu);
		
	}

	private void setUpDisplayPanel() {
		
		_videoDisplayPanel = new JPanel(new BorderLayout());
		
		_embeddedMediaPlayer = new EmbeddedMediaPlayerComponent();
		_mediaPlayer = _embeddedMediaPlayer.getMediaPlayer();
		
		_videoBarPanel = new JPanel(new BorderLayout());
		_videoBar = new JSlider(0,0,0);
		_currentTimeLabel = new JLabel("   --:--:--  ");
		_totalTimeLabel = new JLabel("  --:--:--   ");
		
		_videoBar.addMouseListener(setVideoBarMouseAdapter());
		_videoBar.addChangeListener(setVideoBarChangeListener());
		_mediaPlayer.addMediaPlayerEventListener(new MediaPlayerOutputListener());
		
		_videoBarPanel.add(_videoBar, BorderLayout.CENTER);
		_videoBarPanel.add(_currentTimeLabel, BorderLayout.WEST);
		_videoBarPanel.add(_totalTimeLabel, BorderLayout.EAST);
		
		_videoDisplayPanel.add(_embeddedMediaPlayer, BorderLayout.CENTER);
		_videoDisplayPanel.add(_videoBarPanel, BorderLayout.SOUTH);

	}
	
	private void setUpControlsPanel() {
		
		_videoControlsPanel = new JPanel(new FlowLayout());
		
		_playButton = new JButton("Play");
		_stopButton = new JButton("Stop");
		_forwardButton = new JButton("Forward");
		_rewindButton = new JButton("Rewind");
		_muteButton = new JButton("Mute");

		_playButton.addActionListener(setPlayButton());
		_stopButton.addActionListener(setStopButton());
		_muteButton.addActionListener(setMuteButton());
		_forwardButton.addActionListener(setForwardButton());
		_rewindButton.addActionListener(setRewindButton());

		_volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 50);
		_volumeSlider.addChangeListener(setVolumeBar());

		_fileLabel = new JLabel("Please select a file to play     ");
		_volumeLabel = new JLabel("      Volume");
		
		_playButton.setEnabled(true);
		_stopButton.setEnabled(false);
		_forwardButton.setEnabled(false);
		_rewindButton.setEnabled(false);
		_volumeSlider.setEnabled(false);
		_muteButton.setEnabled(false);

		_videoControlsPanel.add(_fileLabel);
		_videoControlsPanel.add(_playButton);
		_videoControlsPanel.add(_stopButton);
		_videoControlsPanel.add(_forwardButton);
		_videoControlsPanel.add(_rewindButton);
		_videoControlsPanel.add(_volumeLabel);
		_videoControlsPanel.add(_volumeSlider);
		_videoControlsPanel.add(_muteButton);
			
	}
	
	private ActionListener setStopButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopMedia();
			}
			
		};
	}

	private ActionListener setPlayButton() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_currentPlayingFile == null) {
					openMedia();
				} else if (_mediaPlayer.isPlaying()) {
					_playButton.setText("Play");
					_mediaPlayer.pause();
					_timeCount.stop();
				} else {
					_playButton.setText("Pause");
					_stopButton.setEnabled(true);
					_forwardButton.setEnabled(true);
					_rewindButton.setEnabled(true);
					_mediaPlayer.play();
					_timeCount.start();
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
					if (_mediaPlayer.getTime() < _mediaPlayer.getLength() - 1000) {
						_mediaPlayer.skip(1000);
						_currentTimeLabel.setText("   " + convertSecondsToTime((int)_mediaPlayer.getTime()/1000) + "  ");
					} else {
						_forwardButton.setText("Forward");
						_playButton.setEnabled(true);
						_rewindButton.setEnabled(true);
						timer.stop();
					}
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
					if (_mediaPlayer.getTime() > 1000) {
						_mediaPlayer.skip(-1000);
						_currentTimeLabel.setText("   " + convertSecondsToTime((int)_mediaPlayer.getTime()/1000) + "  ");
					} else {
						_rewindButton.setText("Rewind");
						_playButton.setEnabled(true);
						_forwardButton.setEnabled(true);
						timer.stop();
					}
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
				long currentTimeMilli = _mediaPlayer.getTime();
				int currentTime = (int) currentTimeMilli / 1000;
				_userInput = false;
				_videoBar.setValue(currentTime);
				_userInput = true;
				_currentTimeLabel.setText("   " + convertSecondsToTime(currentTime) + "  ");
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

	private ActionListener setExtractAudioButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new ExtractAudioDialog(_vamixWindow, _currentPlayingFile);
				//_extractAudio.setEnabled(false);
			}
		};
	}
	
	private ActionListener setRemoveAudioButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new RemoveAudioDialog(_vamixWindow, _currentPlayingFile);
				//_removeAudio.setEnabled(false);
			}
			
		};
	}

	private ActionListener setOverlaytAudioButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new OverlayAudioDialog(_vamixWindow, _currentPlayingFile);
				//_overlayAudio.setEnabled(false);
			}
			
		};
	}

	private ActionListener setReplaceAudioButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ReplaceAudioDialog(_vamixWindow, _currentPlayingFile);
				//_replaceAudio.setEnabled(false);
			}
			
		};
	}

	private ActionListener setChooseMediaFileButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				openMedia();
			}
		};
	}
	
	private MouseAdapter setVideoBarMouseAdapter() {
		
		return new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {

				// Retrieves the mouse position relative to the component
				// origin.
				int mouseX = e.getX();

				// Computes how far along the mouse is relative to the component
				// width then multiply it by the progress bar's maximum value.
				int progressBarVal = (int) Math.round(((double) mouseX / (double) _videoBar.getWidth()) * _videoBar.getMaximum());

				_videoBar.setValue(progressBarVal);

			}
		};
	}
	
	private ChangeListener setVideoBarChangeListener() {
		return new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (_userInput) {
					_mediaPlayer.setTime((long)_videoBar.getValue()*1000);
				}
			}		
		};
	}
	
	private class MediaPlayerOutputListener extends MediaPlayerEventAdapter {
		
		public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
			
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					int totalTime = (int)_mediaPlayer.getLength()/1000;
					_videoBar.setMaximum(totalTime);
					_totalTimeLabel.setText("  " + convertSecondsToTime(totalTime) + "   ");
				}
			});
		}
		
		public void finished(MediaPlayer mediaPlayer) {
			
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					stopMedia();
				}
			});
		}
	}
	
	private ActionListener setOverlayTextButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextOverlayWindow.getInstance().showGUI();
			}

		};
	}

	private ActionListener setDownloadButton() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
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
	
	public void openMedia() {
		
		_currentPlayingFile = promptFileChooser(OPEN, "Open Media File", _vamixWindow);
		
		if (_currentPlayingFile != null) {
			_fileLabel.setText(new File(_currentPlayingFile).getName() + "     ");
			_playButton.setEnabled(true);
			_playButton.setText("Pause");
			_stopButton.setEnabled(true);
			_forwardButton.setEnabled(true);
			_rewindButton.setEnabled(true);
			_muteButton.setEnabled(true);
			_volumeSlider.setEnabled(true);
			_audioMenu.setEnabled(true);
			_effectsMenu.setEnabled(true);
			_mediaPlayer.prepareMedia(_currentPlayingFile);
			_mediaPlayer.parseMedia();
			_mediaPlayer.play();
			_timeCount.start();
		}
	}
	
	public void stopMedia() {
		_mediaPlayer.stop();
		_stopButton.setEnabled(false);
		_forwardButton.setEnabled(false);
		_rewindButton.setEnabled(false);
		_playButton.setText("Play");
		_timeCount.stop();
	}
	
	public static String promptFileChooser(int spec, String title, Component parent) {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(title);
		int returnVal = JFileChooser.CANCEL_OPTION;
		if (spec == OPEN) {
			returnVal = chooser.showOpenDialog(parent);
		} else if (spec == SAVE) {
			returnVal = chooser.showSaveDialog(parent);
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getPath();
		} else {
			return null;
		}
	}
	
	/**
     * Get the extension of a file.
     * Taken from http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html#customization
     **/  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public String getVideoFilePath() {
    	return _currentPlayingFile;
    }
}
