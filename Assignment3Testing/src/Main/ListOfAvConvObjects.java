package Main;

import java.util.ArrayList;

import AvConvObjects.*;

public class ListOfAvConvObjects {
	private static ListOfAvConvObjects instance = new ListOfAvConvObjects();
	
	private ArrayList<AvConvObject> listAll;
	private RemoveAudio rmAudio;
	private ArrayList<AudioOverlay> audioOverlayList;
	private ReplaceAudio rpAudio;
	private ArrayList<TextOverlay> textOverlayList;
	
	private ListOfAvConvObjects() {
		listAll = new ArrayList<AvConvObject>();
		rmAudio = null;
		audioOverlayList = new ArrayList<AudioOverlay>();
		rpAudio = null;
		textOverlayList = new ArrayList<TextOverlay>();
	}
	
	public static ListOfAvConvObjects getInstance() {
		return instance;
	}

}
