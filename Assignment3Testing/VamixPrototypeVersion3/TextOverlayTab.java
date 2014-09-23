import javax.swing.JButton;
import javax.swing.JLabel;


public class TextOverlayTab extends TextOverlayPanel{
	private TextOverlay textComponent;
	
	private JLabel pathToVideoLabel;
	
	private JButton previewButton;

	public TextOverlayTab(TextOverlay textComponent) {
		super();
		this.textComponent = textComponent;
		
		setVisible(true);
		
		pathToVideoLabel = new JLabel(textComponent.pathToVideo);
		
		previewButton = new JButton("Preview");
		
	}
}
