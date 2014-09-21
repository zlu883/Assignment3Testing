import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class VamixPrototype {

	private final EmbeddedMediaPlayerComponent _mediaPlayer;
    
	private JFrame _vamixWindow;
	private JPanel _mainPanel, _leftPanel, _rightPanel, _topPanel, _bottomPanel;
    private JButton _playButton, _forwardButton, _rewindButton, _muteButton, _chooseMediaFileButton, _extractAudioButton;
    private JSlider _volumeBar, _timeBar;
    private JLabel _timeLabel, _fileLabel;
    
    private Timer _timeCount;
    
    private String _currentPlayingFile;
    
	public static void main(String[] args) {
		new VamixPrototype();
	}
	
	private VamixPrototype() {
    	
        _vamixWindow = new JFrame("Vamix");
        
        _mainPanel = new JPanel(new BorderLayout());
        _leftPanel = new JPanel();
        _leftPanel.setLayout(new BoxLayout(_leftPanel, BoxLayout.PAGE_AXIS));
        _rightPanel = new JPanel();
        _rightPanel.setLayout(new BoxLayout(_rightPanel, BoxLayout.PAGE_AXIS));
        _topPanel = new JPanel(new FlowLayout());
        _bottomPanel = new JPanel(new FlowLayout());
        
        _mediaPlayer = new EmbeddedMediaPlayerComponent();
        
        _playButton = new JButton("Play");
        _forwardButton = new JButton("Forward");
        _rewindButton = new JButton("Rewind");
        _muteButton = new JButton("Mute");
        _chooseMediaFileButton = new JButton("Open video or audio file");
        _extractAudioButton = new JButton("Extract audio");
        
        _volumeBar = new JSlider(JSlider.VERTICAL, 0, 200, 50);
        _timeBar = new JSlider(JSlider.HORIZONTAL);
        
        _timeLabel = new JLabel("");
        _fileLabel = new JLabel("");
        
        _timeCount = new Timer(1, setTimer());
        
        _vamixWindow.setContentPane(_mainPanel);
        
        _mainPanel.add(_topPanel, BorderLayout.NORTH);
        _mainPanel.add(_bottomPanel, BorderLayout.SOUTH);
        _mainPanel.add(_rightPanel, BorderLayout.EAST);
        _mainPanel.add(_leftPanel, BorderLayout.WEST);
        _mainPanel.add(_mediaPlayer, BorderLayout.CENTER);
        
        _topPanel.add(_playButton);
        _topPanel.add(_forwardButton);
        _topPanel.add(_rewindButton);
        _leftPanel.add(_chooseMediaFileButton);
        _leftPanel.add(_extractAudioButton);
        _rightPanel.add(_muteButton);
        _rightPanel.add(_volumeBar);
        _bottomPanel.add(_fileLabel);
        _bottomPanel.add(_timeBar);
        _bottomPanel.add(_timeLabel);
        
        _playButton.addActionListener(setPlayButton());
        _muteButton.addActionListener(setMuteButton());
        _forwardButton.addActionListener(setForwardButton());
        _rewindButton.addActionListener(setRewindButton());
        _chooseMediaFileButton.addActionListener(setChooseMediaFileButton());
        _extractAudioButton.addActionListener(setExtractAudioButton());
        
        _volumeBar.addChangeListener(setVolumeBar());
        _timeBar.addChangeListener(setTimeBar());
        
        _playButton.setEnabled(false);
        _forwardButton.setEnabled(false);
		_rewindButton.setEnabled(false);
		_extractAudioButton.setEnabled(false);

        _vamixWindow.setLocation(100, 100);
        _vamixWindow.setSize(1050, 600);
        _vamixWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _vamixWindow.setVisible(true);

        _mediaPlayer.setVisible(true);
        _mediaPlayer.getMediaPlayer().setVolume(_volumeBar.getValue());
    }

	private ActionListener setPlayButton() {
    	return new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			if (_mediaPlayer.getMediaPlayer().isPlaying()) {
    				_playButton.setText("Play");
    				_mediaPlayer.getMediaPlayer().pause();
    				_timeCount.stop();
    			} else {
    				_playButton.setText("Pause");
    				_mediaPlayer.getMediaPlayer().play();
    				_timeCount.start();
    			}
    		}
    	};
    }
    
    private ActionListener setMuteButton() {
    	return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (_mediaPlayer.getMediaPlayer().isMute()) {
					_muteButton.setText("Mute");
					_mediaPlayer.getMediaPlayer().mute(false);
				} else {
					_mediaPlayer.getMediaPlayer().mute();
					_muteButton.setText("Unmute");
				}
			}	
    	};
    }
    
    private ActionListener setForwardButton() {
    	return new ActionListener() {
    		
    		Timer timer = new Timer(100, new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					_mediaPlayer.getMediaPlayer().skip(1000);	
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
					_mediaPlayer.getMediaPlayer().skip(-1000);	
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
				int currentTime = (int) _mediaPlayer.getMediaPlayer().getTime()/1000;
				_timeLabel.setText("" + currentTime);
				_timeBar.setValue(currentTime);
			}
        	
        };
    }
    
    private ChangeListener setTimeBar() {
    	return new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// small problem here, causes glitch because repeatedly updating position
				//_mediaPlayer.getMediaPlayer().setTime((long)_timeBar.getValue()*1000);
			}
		};
	}

	private ChangeListener setVolumeBar() {
		return new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				_mediaPlayer.getMediaPlayer().setVolume(_volumeBar.getValue());
			}
		};
	}

	private ActionListener setExtractAudioButton() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
				String fileName = JOptionPane.showInputDialog("Please input audio file name");
				System.out.println(fileName);
				
				String cmd = "avconv -i " + _currentPlayingFile + " -vn " + fileName;
				
				ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				pb.redirectErrorStream(true);
				Process p = pb.start();
				
				InputStream stdout = p.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(
						stdout));
				
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
					File inputFile = new File(chooser.getSelectedFile().getPath());
					_currentPlayingFile = inputFile.getPath();
					_fileLabel.setText(inputFile.getName());
					_playButton.setEnabled(true);
					_forwardButton.setEnabled(true);
					_rewindButton.setEnabled(true);
					_extractAudioButton.setEnabled(true);
					_mediaPlayer.getMediaPlayer().prepareMedia(_currentPlayingFile);
					_mediaPlayer.getMediaPlayer().parseMedia();
					_timeBar.setMaximum((int)_mediaPlayer.getMediaPlayer().getMediaMeta().getLength()/1000);
				}
			}
    	};
	}

}
