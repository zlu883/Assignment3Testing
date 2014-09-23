import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class TextOverlayWindow extends JFrame {
	private static TextOverlayWindow instance = new TextOverlayWindow();
	
	private TextOverlayPanel TOpanel;
	
	private JTabbedPane tabPane;
	
	private TextOverlayWindow() {
		// Set size and location of window
		setTitle("Text Overlay");
		
		setLocation(150, 150);
		setSize(400, 400);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// Create the container to hold the tabs
		tabPane = new JTabbedPane();
		
		TOpanel = new TextOverlayPanel();
		
		tabPane.add("Text Overlay", TOpanel);
		
		this.add(tabPane);
	}
	
	public static TextOverlayWindow getInstance() {
		return instance;
	}
	
	public void addTab(String tabName, JPanel tab) {
		tabPane.add(tabName, tab);
	}
	
	public void removeTab(int index) {
		tabPane.remove(index);
	}
	
	public void showGUI() {
		this.setVisible(true);
	}
	
	public TextOverlayPanel getTOpanel() {
		return TOpanel;
	}
}
