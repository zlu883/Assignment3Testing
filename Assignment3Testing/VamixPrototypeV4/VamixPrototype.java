import javax.swing.JFrame;

public class VamixPrototype {
	
	private static GuiManager _guiManager;
	private static JFrame _vamixWindow;

	public static void main(String[] args) {
		
		_guiManager = new GuiManager();
		_vamixWindow = _guiManager.createVamixWindow();

	}

}
