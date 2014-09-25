import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class CustomSwingWorker extends SwingWorker<Void, Void> {
		private ArrayList<String> cmds;
		
		Process p;
		
		StringBuffer sb;
		
		CustomSwingWorker(ArrayList<String> cmds) {
			this.cmds = cmds;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			for (String cmd : cmds) {
				ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);

				pb.redirectErrorStream(true);
				p = pb.start();
				String s;
				BufferedReader stdout = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				sb = new StringBuffer();

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
		
		protected void done() {
			ExportWindow.getInstance().getProgressBar().setIndeterminate(false);
			
			ExportWindow.getInstance().deleteTempFiles();
			
			if (p.exitValue() == 1) {
				JOptionPane.showMessageDialog(null,
						"An error occured, see below for details: \n" + sb.toString());
			} else {	
				ExportWindow.getInstance().getProgressBar().setValue(100);
				ExportWindow.getInstance().getProgressBar().setStringPainted(true);
				ExportWindow.getInstance().getProgressBar().setString("Export Complete");
			}
		}
	}