import java.util.ArrayList;

import javax.swing.JFrame;

public class CommandManager {

	private CommandManager() {}
	
	public static BashCommand overlayAudioCommand(String sourceAudio, String overlayAudio, String outputFile) {
		return new BashCommand("avconv -i " + sourceAudio + " -i " + overlayAudio + 
				" -filter_complex amix=duration=first " + outputFile);		
	}
	
	public static BashCommand extractAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -vn " + outputFile);
	}
	
	public static BashCommand replaceAudioCommand(String videoFile, String replaceAudio, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -i " + replaceAudio + 
				" -c copy -map 0:v -map 1:a " + outputFile);
	}
	
	public static BashCommand removeAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -an " + outputFile);
	}
	
	public static BashCommand drawtextCommand(String pathToVideo, String outputFile, ArrayList<TextOverlay> textOverlays) {
		String command = "avconv -i " + pathToVideo
				+ " -strict experimental -vf \"";
		
		int count = 0;
		
		for (TextOverlay t : textOverlays) {
			count++;
			
			command = command + t.createBashCommand();
			
			//If the it is not the last text component to add, then add a comma separator to the string
			if (!(count==textOverlays.size())) command = command + ","; 
		}
		
		command = command + "\" " + outputFile.toString();
		
		return new BashCommand(command);
		
	}
	
}

