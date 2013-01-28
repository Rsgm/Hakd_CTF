package hakd.fxgui;

import javafx.scene.Scene;

public class FxGuiController {
	private static Scene	scene;

	public static void start() {
		scene.getStylesheets().add(FxGuiController.class.getResource("Gui.css").toExternalForm());
		FxMenuGui.start();
	}

	public static Scene getScene() {
		return scene;
	}

	public static void setScene(Scene scene) {
		FxGuiController.scene = scene;
	}
}
