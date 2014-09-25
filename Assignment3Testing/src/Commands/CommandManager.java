package Commands;
import java.util.ArrayList;

import javax.swing.JFrame;

import AvConvObjects.TextOverlay;

public class CommandManager {

	private CommandManager() {}
	
	public static BashCommand overlayAudioCommand(String sourceAudio, String overlayAudio, String outputFile) {
		return new BashCommand("avconv -i " + sourceAudio + " -i " + overlayAudio + 
				" -filter_complex amix=duration=first -strict experimental " + outputFile);		
	}
	
	public static BashCommand extractAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -vn -strict experimental" + outputFile);
	}
	
	public static BashCommand replaceAudioCommand(String videoFile, String replaceAudio, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -i " + replaceAudio + 
				" -c copy -map 0:v -map 1:a -strict experimental" + outputFile);
	}
	
	public static BashCommand removeAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -an -strict experimental" + outputFile);
	}
	
	public static BashCommand extractDurationCommand(String input, String endTime, String output) {
				return new BashCommand ("avconv -i " + input + " -ss 00:00:00 -t " + endTime + " -c copy -srict experimental" + output);
			}
			
			public static BashCommand concatAudioCommand(String firstAudio, String secondAudio, String output) {
			return new BashCommand("avconv -i \"concat:" + firstAudio + "|" + secondAudio +
						"\" -c copy -strict experimental" + output);
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

