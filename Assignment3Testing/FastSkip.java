package se206_a3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class FastSkip {

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    
    private JButton playButton, forwardButton, rewindButton;
    
    private String fileName;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FastSkip(args);
            }
        });
    }

    private FastSkip(String[] args) {
    	fileName = args[0];
    	
        JFrame frame = new JFrame("vlcj Tutorial");
        
        JPanel panel = new JPanel(new BorderLayout());
        
        frame.add(panel);
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        panel.add(mediaPlayerComponent, BorderLayout.CENTER);
        
        playButton = new JButton("Play");
        playButton.addActionListener(setPlayButton());
        panel.add(playButton, BorderLayout.NORTH);
        
        JButton muteButton = new JButton("Mute");
        muteButton.addActionListener(setMuteButton());
        //panel.add(muteButton, BorderLayout.NORTH);
        
        forwardButton = new JButton("Forward");
        forwardButton.addActionListener(setForwardButton());
        panel.add(forwardButton, BorderLayout.EAST);
        
        rewindButton = new JButton("Rewind");
        rewindButton.addActionListener(setRewindButton());
        panel.add(rewindButton, BorderLayout.WEST);
        
        final JLabel timeLabel = new JLabel("");
        Timer getCurrentTime = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int currentTime = (int) mediaPlayerComponent.getMediaPlayer().getTime()/1000;
				timeLabel.setText("" + currentTime);
			}
        	
        });
        getCurrentTime.start();
        panel.add(timeLabel, BorderLayout.SOUTH);

        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //mediaPlayerComponent.getMediaPlayer().playMedia(args[0]);
    }
    
    private ActionListener setPlayButton() {
    	return new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
    				playButton.setText("Play");
    				mediaPlayerComponent.getMediaPlayer().pause();
    			} else {
    				playButton.setText("Pause");
    				mediaPlayerComponent.getMediaPlayer().playMedia(fileName);
    			}
    		}
    	};
    }
    
    private ActionListener setMuteButton() {
    	return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().mute();
			}
    		
    	};
    }
    
    private ActionListener setForwardButton() {
    	return new ActionListener() {
    		
    		Timer timer = new Timer(100, new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					mediaPlayerComponent.getMediaPlayer().skip(100);	
				}
				
			});
    		
    		public void actionPerformed(ActionEvent arg0) {
    			if (forwardButton.getText() == "Forward") {
    				forwardButton.setText("Resume");
    				timer.start();
    			} else if (forwardButton.getText() == "Resume") {
    				forwardButton.setText("Forward");
    				timer.stop();
    			}
    		}
    	};
    }
    
    private ActionListener setRewindButton() {
    	return new ActionListener() {
    		Timer timer = new Timer(100, new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					mediaPlayerComponent.getMediaPlayer().skip(-100);	
				}
				
			});
    		
    		public void actionPerformed(ActionEvent arg0) {
    			if (rewindButton.getText() == "Rewind") {
    				rewindButton.setText("Resume");
    				timer.start();
    			} else if (rewindButton.getText() == "Resume") {
    				rewindButton.setText("Rewind");
    				timer.stop();
    			}
    		}
    	};
    }
}    
