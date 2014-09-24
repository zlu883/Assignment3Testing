package se206_a3;

public class CommandManager {

	public CommandManager() {}
	
	public BashCommand overlayAudioCommand(String sourceAudio, String overlayAudio, String outputFile) {
		return new BashCommand("avconv -i " + sourceAudio + " -i " + overlayAudio + 
				" -filter_complex amix=duration=first " + outputFile);		
	}
	
	public BashCommand extractAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -vn " + outputFile);
	}
	
	public BashCommand replaceAudioCommand(String videoFile, String replaceAudio, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -i " + replaceAudio + 
				" -c copy -map 0:v -map 1:a " + outputFile);
	}
	
	public BashCommand removeAudioCommand(String videoFile, String outputFile) {
		return new BashCommand("avconv -i " + videoFile + " -an " + outputFile);
	}
	
	public BashCommand drawtextCommand(String pathToVideo, String pathToFont, String textToOverlay,
			String fontSize, String color, int startSeconds, int endSeconds) {
		return new BashCommand("avconv -i " + pathToVideo
				+ " -strict experimental -vf \"drawtext=" + "fontfile='"
				+ pathToFont + "':text='" + textToOverlay + "':fontsize='"
				+ fontSize + "':" + "fontcolor='" + color
				+ "': draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
				+ ")'\"");
		
	}
}

