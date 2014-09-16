 package vlcj.tutorial2;

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

    public class Tutorial2B {

        private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
        
        private JButton playButton;
        
        private String fileName;

        public static void main(final String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Tutorial2B(args);
                }
            });
        }

        private Tutorial2B(String[] args) {
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
            
            JButton forwardButton = new JButton("Forward");
            forwardButton.addActionListener(setForwardButton());
            panel.add(forwardButton, BorderLayout.EAST);
            
            JButton rewindButton = new JButton("Rewind");
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
        				playButton.setText("Pause");
        				mediaPlayerComponent.getMediaPlayer().pause();
        			} else {
        				playButton.setText("Play");
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
        		public void actionPerformed(ActionEvent arg0) {
        			mediaPlayerComponent.getMediaPlayer().skip(10000);
        		}
        	};
        }
        
        private ActionListener setRewindButton() {
        	return new ActionListener() {
        		public void actionPerformed(ActionEvent  arg0) {
        			mediaPlayerComponent.getMediaPlayer().skip(-10000);
        		}
        	};
        }
    }