package AvConvObjects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Misc.VamixException;


/**
 * Class that manages operations in regard to saving and reading files that contain data on 
 * user activity in the current session.
 */
public class SaveFileManager {
	
	public SaveFileManager() {}
	
	/**
	 * Create and save a data file on edits made in current session.
	 * 
	 * @param textOverlays a list of edits to save in this file
	 * @param outputPath location to save file
	 * @throws IOException
	 * @throws VamixException
	 */
	public void createSaveFile(ArrayList<AvConvObject> objects, String outputPath) throws IOException, VamixException {
		
		File saveFile = new File(outputPath);
		if (saveFile.exists()) {
			//throw new VamixException("File already exists");
		}
		saveFile.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
		writer.write("==VAMIX DATA FILE==");
		writer.newLine();
		for (AvConvObject t : objects) {
			for (String s: t.getFormatDataList()) {
				writer.write(s);
				writer.write("<VAMIX><DATA>");
			}
			writer.newLine();
		}
		writer.close();
		
	}
	
	/**
	 * Reads a save data file to retrieve relevant information.
	 * 
	 * @param filePath the path of file to read
	 * @return a list of data read from the file
	 * @throws VamixException
	 * @throws IOException
	 */
	public ArrayList<AvConvObject> readSaveFile(String filePath) throws VamixException, IOException {
		
		File saveFile = new File(filePath);
		if (!saveFile.exists() || !saveFile.isFile()) {
			throw new VamixException("File does not exist");
		}
		
		ArrayList<AvConvObject> savedObjectList = new ArrayList<AvConvObject>();
		
		BufferedReader reader = new BufferedReader(new FileReader(saveFile));
		String line;
		ArrayList<String> lineList = new ArrayList<String>();
		if (!(line = reader.readLine()).equals("==VAMIX DATA FILE==")) {
			reader.close();
			throw new VamixException("Corrupted file");
		}
		while ((line = reader.readLine()) != null) {
			lineList.add(line);
		}
		reader.close();
		
		for (String s: lineList) {
			String[] dataList = s.split("<VAMIX><DATA>");
			System.out.print(dataList[0]);
			String header = dataList[0];
			if (header.equals(">TEXTOVERLAY")) {
				savedObjectList.add(readTextOverlay(dataList));
			} else if (header.equals(">OVERLAYAUDIO")) {
				savedObjectList.add(readOverlayAudio(dataList));
			} else if (header.equals(">REPLACEAUDIO")) {
				savedObjectList.add(readReplaceAudio(dataList));
			} else if (header.equals(">REMOVEAUDIO")) {
				savedObjectList.add(readRemoveAudio(dataList));
			} else {
				throw new VamixException("Corrupted save file");
			}
		}
		
		return savedObjectList;
	}

	private AvConvObject readRemoveAudio(String[] dataList) {
		// TODO Auto-generated method stub
		return null;
	}

	private AvConvObject readReplaceAudio(String[] dataList) {
		// TODO Auto-generated method stub
		return null;
	}

	private AvConvObject readOverlayAudio(String[] dataList) {
		// TODO Auto-generated method stub
		return null;
	}

	private AvConvObject readTextOverlay(String[] dataList) {
		String pathToVideo = null;
		String textToOverlay = null;
		String pathToFont = null;
		String fontSize = null;
		String color = null;
		String[] startInputs = new String[3];
		String[] endInputs = new String[3];
		String positionEnum = null;
		for (int i = 1; i < dataList.length; i++) {
			if (i == 1) {
				pathToVideo = dataList[i];
			} else if (i == 2) {
				textToOverlay = dataList[i];
			} else if (i == 3) {
				pathToFont = dataList[i];
			} else if (i == 4) {
				fontSize = dataList[i];
			} else if (i == 5) {
				color = dataList[i];
			} else if (i == 6) {
					positionEnum = dataList[i];	
			} else if (i == 7) {
				startInputs[0] = dataList[i];
			} else if (i == 8) {
				startInputs[1] = dataList[i];
			} else if (i == 9) {
				startInputs[2] = dataList[i];
			} else if (i == 10) {
				endInputs[0] = dataList[i];
			} else if (i == 11) {
				endInputs[1] = dataList[i];
			} else if (i == 12) {
				endInputs[2] = dataList[i];
			} else if (i == 6) {
				positionEnum = dataList[i];
			}		
		}
		return new TextOverlay(pathToVideo, textToOverlay, pathToFont, fontSize, color, TextOverlay.TextPosition.valueOf(positionEnum),  
				startInputs, endInputs);
	}
}
