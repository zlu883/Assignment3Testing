import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.SwingWorker;

public class TextOverlay {
	public String pathToVideo;
	public String textToOverlay;
	public String pathToFont;
	public String fontSize;
	public String color;
	public String[] startInputs;
	public String[] endInputs;
	public TextPosition position;

	public String tempFileVideoName;

	public TextOverlay(String pathToVideo, String textToOverlay,
			String pathToFont, String fontSize, String color, TextPosition position, 
			String[] startInputs, String[] endInputs) {
		this.pathToVideo = pathToVideo;
		this.textToOverlay = textToOverlay;
		this.pathToFont = pathToFont;
		this.fontSize = fontSize;
		this.color = color;
		this.startInputs = startInputs;
		this.endInputs = endInputs;
		this.position = position;
	}
	
	public String createBashCommand() {
		String color = "0x" + this.color;
		//System.out.println(color);
		
		int startSeconds = Integer.parseInt(startInputs[0]) * 3600
				+ Integer.parseInt(startInputs[1]) * 60
				+ Integer.parseInt(startInputs[2]);
		int endSeconds = Integer.parseInt(endInputs[0]) * 3600
				+ Integer.parseInt(endInputs[1]) * 60
				+ Integer.parseInt(endInputs[2]);
		
		//System.out.println("" + endSeconds);
		//System.out.println(position.returnPosition());
		
		String pathToTempTextFile = createTempFile().getPath().toString();
		ExportWindow.getInstance().addToTempFiles(pathToTempTextFile);

		/*String cmd = "drawtext=" + "fontfile='"
				+ pathToFont + "':text='" + textToOverlay + "':fontsize='"
				+ fontSize + "':" + "fontcolor='" + color + "':" + position.returnPosition()
				+ ": draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
				+ ")'"; */
		
		String cmd = "drawtext=" + "fontfile='"
				+ pathToFont + "':textfile='" + pathToTempTextFile + "':fontsize='"
				+ fontSize + "':" + "fontcolor='" + color + "':" + position.returnPosition()
				+ ": draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
				+ ")'";
		
		System.out.println(cmd);
		return cmd;
	}

	/**
	 * This method creates and runs the bash processes necessary to overlay the
	 * specified text on the referenced video
	 * 
	 * @throws IOException
	 */
	public void runBashCommand() {
		try {
			String cmd = "avconv " + createBashCommand() + " -y .tempVideoFile.mp4";
		} catch (Exception e) {
		}
	}

	private class CustomSwingWorker extends SwingWorker<Void, Void> {
		private String cmd;
		
		CustomSwingWorker(String cmd) {
			this.cmd = cmd;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
				ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process p;

				pb.redirectErrorStream(true);
				p = pb.start();
				String s;
				BufferedReader stdout = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				StringBuffer sb = new StringBuffer();

				while ((s = stdout.readLine()) != null) {
					// Append avconv's output to a buffer
					sb.append(s + "\n");
					System.out.println(s);

				p.destroy();
				this.cancel(true);

			}
			return null;

		}
	}
	
	public ArrayList<String> getFormatDataList() {
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.add(pathToVideo);
		dataList.add(textToOverlay);
		dataList.add(pathToFont);
		dataList.add(fontSize);
		dataList.add(color);
		dataList.add(position.toString());
		for (int i = 0; i < startInputs.length; i++) {
			dataList.add(startInputs[i]);
		}
		for (int i = 0; i < startInputs.length; i++) {
			dataList.add(endInputs[i]);
		}
		return dataList;
	}
	
	public File createTempFile() {
		try {
		File tempTextFile = new File(System.getProperty("user.dir") + "/." + Math.random() * 10000);
		
		System.out.println(tempTextFile.getPath().toString());
		
		tempTextFile.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempTextFile));
		
		writer.write(textToOverlay);
		
		writer.close();
		
		return tempTextFile;

		
		} catch (IOException e1) {
			System.out.println("yooooo");
			return null;
		}
	}
	
	public enum TextPosition {
		TOPLEFT ("x='0':y='0'"), TOPRIGHT ("x='main_w - text_w':y='0'"), 
		BOTTOMLEFT ("x='0':y='H-h'"), BOTTOMRIGHT ("x='W-w':y='H-h'"), 
		LEFT ("x='0':y='H/2-h/2'"), TOP ("x='W/2-w/2':y='0'"), 
		RIGHT ("x='W-w':y='H/2-h/2'"), BOTTOM ("x='W/2-w/2':y='H-h'"), 
		CENTER ("x='W/2-w/2':y='H/2-h/2'");
		
		private final String position;
		
		TextPosition(String position) {
			this.position = position;
		}
		
		public String returnPosition() {
			return position;
		}
	}
	
}
