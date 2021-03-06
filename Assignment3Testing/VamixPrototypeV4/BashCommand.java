import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.SwingWorker;

public class BashCommand {
	
	private String _cmd;
	
	public BashCommand(String cmd) {
		_cmd = cmd;
	}
	
	public void run() {
		new BashWorker().execute();
	}
	
	private class BashWorker extends SwingWorker<Integer, Void> {

		@Override
		protected Integer doInBackground() throws Exception {
			int exitValue = -1;
			try {
				ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", _cmd);
				pb.redirectErrorStream(true);
				Process p = pb.start();
				
				InputStream stdout = p.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
				
				String s = null;
				while ((s = reader.readLine()) != null) {
					System.out.println(s);
				}
				exitValue = p.exitValue();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return exitValue;
		}
		
	}
}
