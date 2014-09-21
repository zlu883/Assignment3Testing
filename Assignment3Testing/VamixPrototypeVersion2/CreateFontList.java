package VamixPrototypeVersion2;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateFontList {

	private ArrayList<String> fontPaths;
	private ArrayList<File> fontFiles;
	private ArrayList<Font> fontsBase = new ArrayList<Font>();
	private Font[] fontsReal;
	
	private HashMap<String, File> fontMap;

	CreateFontList() {
		fontMap = new HashMap<String, File>();
		
		fontFiles = new ArrayList<File>();
		fontFiles = createFontFilesList(fontFiles, new File("/usr/share/fonts/truetype"));
		
		try {
			for (File file : fontFiles) {
				Font newFont = Font.createFont(Font.TRUETYPE_FONT, file);
				fontsBase.add(newFont);
				
				fontMap.put(newFont.getName(), file);
			}
			
			fontsReal = fontsBase.toArray(new Font[fontsBase.size()]);
			for (Font font : fontsReal) {
				font = font.deriveFont(Font.PLAIN, 16);
			}
			
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<File> createFontFilesList(ArrayList<File> files, File dir) {
		if (files == null)
	        files = new ArrayList<File>();

	    if (!dir.isDirectory())
	    {
	    	files.add(dir);
	    	return files;
	    }

	    for (File file : dir.listFiles()) {
	        createFontFilesList(files, file);
	    }
	    return files;
		
	}
	
	public Font[] getFontsReal() {
		return fontsReal;
	}
	
	public HashMap<String, File> getMap() {
		return fontMap;
	}
}
