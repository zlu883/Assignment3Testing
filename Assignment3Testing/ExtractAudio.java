 package vlcj.tutorial2;

    import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

    public class ExtractAudio {
    	private JFrame frame;

        private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
        
        private JButton playButton;
        
        private JTextField videoFileName;
        
        private String fileName;

        public static void main(final String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ExtractAudio(args);
                }
            });
        }

        private ExtractAudio(String[] args) {
        	//fileName = args[0];
        	
            frame = new JFrame("vlcj Tutorial");
            
            JPanel panel = new JPanel();
            
            frame.add(panel);
            
            Dimension mPCsize = new Dimension(500, 500);
            
            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            mediaPlayerComponent.setPreferredSize(mPCsize);
            panel.add(mediaPlayerComponent);
            
            playButton = new JButton("Play");
            playButton.addActionListener(setPlayButton());
            panel.add(playButton);
            
            JButton chooseVideoButton = new JButton("Select Video");
            chooseVideoButton.addActionListener(setChooseVideoButton());
            panel.add(chooseVideoButton);
            
            videoFileName = new JTextField(20);
            panel.add(videoFileName);
            
            JButton extractAudioButton = new JButton("Extract Audio");
            extractAudioButton.addActionListener(setExtractAudioButton());
            panel.add(extractAudioButton);
            
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
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
            mediaPlayerComponent.setVisible(true);
        }
        
        private ActionListener setPlayButton() {
        	return new ActionListener() {
        		public void actionPerformed(ActionEvent arg0) {
        			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
        				playButton.setText("Play");
        				mediaPlayerComponent.getMediaPlayer().pause();
        			} else {
        				playButton.setText("Stop");
        				mediaPlayerComponent.getMediaPlayer().playMedia(videoFileName.getText());
        			}
        		}
        	};
        }
        
        private ActionListener setChooseVideoButton() {
        	return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File inputFile = new File(chooser.getSelectedFile()
								.getPath());
						videoFileName.setText(inputFile.getPath());
					}
				}

        	};
        }
        
        private ActionListener setExtractAudioButton() {
        	
        	return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
					String fileName = JOptionPane.showInputDialog("Please input audio file name");
					System.out.println(fileName);
					
					String cmd = "avconv -i " + videoFileName.getText() + " -vn " + fileName;
					
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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
        		
        	};
        }
    }