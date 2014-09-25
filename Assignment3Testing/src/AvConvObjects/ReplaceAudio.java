package AvConvObjects;

import java.util.ArrayList;

import Commands.BashCommand;

public class ReplaceAudio implements AvConvObject{
	private String pathToVideo;
	private String pathToAudioFile;
	
	public ReplaceAudio(String pathToVideo, String pathToAudioFile) {
		this.pathToVideo = pathToVideo;
		this.pathToAudioFile = pathToAudioFile;
	}

	@Override
	public BashCommand createBashCommand(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getFormatDataList() {
		// TODO Auto-generated method stub
		return null;
	}
}
