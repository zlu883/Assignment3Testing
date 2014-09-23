import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.SwingWorker;

public class TextOverlay {
	public String pathToVideo;
	public String textToOverlay;
	public String pathToFont;
	public int fontIndexInFontChooser;
	public String fontSize;
	public String color;
	public String[] startInputs;
	public String[] endInputs;

	public String tempFileVideoName;

	public TextOverlay(String pathToVideo, String textToOverlay,
			String pathToFont, int fontIndex, String fontSize, String color,
			String[] startInputs, String[] endInputs) {
		this.pathToVideo = pathToVideo;
		this.textToOverlay = textToOverlay;
		this.pathToFont = pathToFont;
		this.fontIndexInFontChooser = fontIndex;
		this.fontSize = fontSize;
		this.color = color;
		this.startInputs = startInputs;
		this.endInputs = endInputs;
	}
	
	public String createBashCommand() {
		String color = "0xff" + this.color;
		int startSeconds = Integer.parseInt(startInputs[0]) * 3600
				+ Integer.parseInt(startInputs[1]) * 60
				+ Integer.parseInt(startInputs[2]);
		int endSeconds = Integer.parseInt(endInputs[0]) * 3600
				+ Integer.parseInt(endInputs[1]) * 60
				+ Integer.parseInt(endInputs[2]);

		ArrayList<String> listOfCommands = new ArrayList<String>();

		String cmd = "avconv -i " + pathToVideo
				+ " -strict experimental -vf \"drawtext=" + "fontfile='"
				+ pathToFont + "':text='" + textToOverlay + "':fontsize='"
				+ fontSize + "':" + "fontcolor='" + color
				+ "': draw='gt(t," + startSeconds + ")*lt(t," + endSeconds
				+ ")'\"";
		
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
			int startSeconds = Integer.parseInt(startInputs[0]) * 3600
					+ Integer.parseInt(startInputs[1]) * 60
					+ Integer.parseInt(startInputs[2]);
			int endSeconds = Integer.parseInt(endInputs[0]) * 3600
					+ Integer.parseInt(endInputs[1]) * 60
					+ Integer.parseInt(endInputs[2]);

			ArrayList<String> listOfCommands = new ArrayList<String>();

			String cmd = createBashCommand() + " -y .tempVideoFile.mp4";

			listOfCommands.add(cmd);

			CustomSwingWorker csw = new CustomSwingWorker(listOfCommands);
			csw.execute();
		} catch (Exception e) {
		}
	}

	private class CustomSwingWorker extends SwingWorker<Void, Void> {
		private ArrayList<String> cmdList;

		public CustomSwingWorker(ArrayList<String> cmd) {
			this.cmdList = cmd;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			for (String cmd : cmdList) {
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
				}

				p.destroy();
				this.cancel(true);

			}
			return null;

		}
	}
}
