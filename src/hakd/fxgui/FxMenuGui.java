package hakd.fxgui;

import hakd.game.Commands;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class FxMenuGui {
	private static AnchorPane	menu;			// the menu GUI pane

	private static TextArea		display;
	private static TextField	input;
	private static boolean		menuRunning;

	public static void start() {
		Commands.getLines().clear();
		Commands.setRecall(0);

		menu = new AnchorPane();
		FxGuiController.getScene().setRoot(menu);
		menuRunning = true;

		display = new TextArea();
		input = new TextField();
		display.getStyleClass().add("terminal-text");
		input.getStyleClass().add("terminal-text");
		input.setId("terminal-input");
		input.setMinHeight(25.0);
		display.setEditable(false);
		display.setPromptText("Type \"help\" and press enter to get started.");
		menu.getChildren().addAll(display, input);
		input.setText(">");
		input.requestFocus();
		input.end();

		AnchorPane.setBottomAnchor(display, 20.0);
		AnchorPane.setLeftAnchor(display, 0.0);
		AnchorPane.setRightAnchor(display, 0.0);
		AnchorPane.setTopAnchor(display, 0.0);
		AnchorPane.setBottomAnchor(input, 0.0);
		AnchorPane.setLeftAnchor(input, 0.0);
		AnchorPane.setRightAnchor(input, 0.0);

		// input handlers
		input.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyInput) {
				if (keyInput.getCode() == KeyCode.ENTER) {
					display.setText(display.getText() + "\n" + Commands.input(input.getText()));
					input.setText(">");
					input.end();
					display.end();
				} else if (keyInput.getCode() == KeyCode.UP || keyInput.getCode() == KeyCode.DOWN) {
					input.setText(Commands.history(keyInput, input.getText()));
					input.end();
					display.end();
				} else if (keyInput.getCode() == KeyCode.ESCAPE) {
					input.setText(">");
					input.end();
					display.end();
				}
			}
		});

		display.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse) {
				input.requestFocus();
				input.end();
			}
		});
	}

	public static AnchorPane getMenu() {
		return menu;
	}

	public static void setMenu(AnchorPane menu) {
		FxMenuGui.menu = menu;
	}

	public static TextArea getDisplay() {
		return display;
	}

	public static TextField getInput() {
		return input;
	}

	public static boolean isMenuRunning() {
		return menuRunning;
	}

	public static void setMenuRunning(boolean menuRunning) {
		FxMenuGui.menuRunning = menuRunning;
	}

}
