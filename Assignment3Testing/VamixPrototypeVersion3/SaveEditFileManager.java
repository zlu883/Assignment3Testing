import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that manages operations in regard to saving and reading files that contain data on 
 * multiple user-defined text overlays.
 */
public class SaveEditFileManager {
	
	public SaveEditFileManager() {}
	
	/**
	 * Create and save a data file on text overlays.
	 * 
	 * @param textOverlays a list of text overlays to save in this file
	 * @param outputPath location to save file
	 * @throws IOException
	 * @throws VamixException
	 */
	public void createSaveFile(ArrayList<TextOverlay> textOverlays, String outputPath) throws IOException, VamixException {
		
		File saveFile = new File(outputPath);
		if (saveFile.exists()) {
			throw new VamixException("File already exists");
		}
		saveFile.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
		writer.write("==VAMIX TEXT OVERLAY DATA FILE==");
		writer.newLine();
		for (TextOverlay t : textOverlays) {
			for (String s: t.getFormatDataList()) {
				writer.write(s);
				writer.newLine();
			}
			writer.write(">");
			writer.newLine();
		}
		writer.close();
		
	}
	
	/**
	 * Reads a text overlay data file to retrieve relevant information.
	 * 
	 * @param filePath the path of file to read
	 * @return a list of text overlays read from the file
	 * @throws VamixException
	 * @throws IOException
	 */
	public ArrayList<TextOverlay> readSaveFile(String filePath) throws VamixException, IOException {
		
		File saveFile = new File(filePath);
		if (!saveFile.exists() || !saveFile.isFile()) {
			throw new VamixException("File does not exist");
		}
		
		ArrayList<TextOverlay> overlayList = new ArrayList<TextOverlay>();
		String pathToVideo = null;
		String textToOverlay = null;
		String pathToFont = null;
		String fontSize = null;
		String color = null;
		String[] startInputs = new String[3];
		String[] endInputs = new String[3];
		
		BufferedReader reader = new BufferedReader(new FileReader(saveFile));
		String line;
		ArrayList<String> lineList = new ArrayList<String>();
		if (!(line = reader.readLine()).equals("==VAMIX TEXT OVERLAY DATA FILE==")) {
			reader.close();
			throw new VamixException("Corrupted file");
		}
		while ((line = reader.readLine()) != null) {
			lineList.add(line);
		}
		reader.close();
		
		if (lineList.size() % 12 != 0) {
			throw new VamixException("Corrupted file");
		}
		int c;
		for (int i = 0; i < lineList.size(); i++) {
			c = (i + 1) % 12;
			if (c == 1) {
				pathToVideo = lineList.get(i);
			} else if (c == 2) {
				textToOverlay = lineList.get(i);
			} else if (c == 3) {
				pathToFont = lineList.get(i);
			} else if (c == 4) {
				fontSize = lineList.get(i);
			} else if (c == 5) {
				color = lineList.get(i);
			} else if (c == 6) {
				startInputs[0] = lineList.get(i);
			} else if (c == 7) {
				startInputs[1] = lineList.get(i);
			} else if (c == 8) {
				startInputs[2] = lineList.get(i);
			} else if (c == 9) {
				endInputs[0] = lineList.get(i);
			} else if (c == 10) {
				endInputs[1] = lineList.get(i);
			} else if (c == 11) {
				endInputs[2] = lineList.get(i);
			} else if (c == 0) {
				if (!lineList.get(i).equals(">")) {
					throw new VamixException("Corrupted file");
				} else {
					overlayList.add(new TextOverlay(pathToVideo, textToOverlay, pathToFont, fontSize, color, 
							startInputs, endInputs));
				}
			}
		}
		
		return overlayList;
	}

}
