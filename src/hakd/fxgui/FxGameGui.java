package hakd.fxgui;

import hakd.Hakd;
import hakd.game.Commands;
import hakd.game.Player;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class FxGameGui {
	private static AnchorPane		game	= new AnchorPane();		// the game GUI pane

	private static TextArea			display;
	private static TextField		input;
	private static Label			scoreLabel;
	private static Rectangle		scoreBox;

	private static ArrayList<Node>	nodes	= new ArrayList<Node>();

	public static void start() {
		nodes.clear();
		Commands.getLines().clear();
		Commands.setRecall(0);

		FxGuiController.getScene().setRoot(game);
		FxMenuGui.setMenuRunning(false);

		display = new TextArea();
		input = new TextField();
		display.setEditable(false);
		display.setPromptText("Type \"help\" to get started.");
		display.getStyleClass().add("terminal-text");
		input.getStyleClass().add("terminal-text");
		input.setId("terminal-input");
		input.setText(">");
		input.requestFocus();
		input.end();
		nodes.add(display);
		nodes.add(input);

		AnchorPane.setBottomAnchor(display, 20.0);
		AnchorPane.setLeftAnchor(display, 0.0);
		AnchorPane.setRightAnchor(display, 0.0);
		AnchorPane.setTopAnchor(display, 403.0);
		AnchorPane.setBottomAnchor(input, 0.0);
		AnchorPane.setLeftAnchor(input, 0.0);
		AnchorPane.setRightAnchor(input, 0.0);

		Line l = new Line(380, 0, 380, 400);
		l.setStroke(Paint.valueOf("grey"));
		l.setStrokeWidth(2);
		Rectangle r = new Rectangle(1, 1, 758, 399);
		r.setFill(Paint.valueOf("white"));
		r.setStroke(Paint.valueOf("blue"));
		nodes.add(r);
		nodes.add(l);

		scoreBox = new Rectangle(1, 1, 90, 18);
		scoreBox.setFill(Paint.valueOf("white"));
		scoreBox.setStroke(Paint.valueOf("blue"));
		scoreBox.setStrokeWidth(2);
		scoreLabel = new Label();
		scoreLabel.setId("score");
		scoreLabel.setTextFill(Paint.valueOf("grey"));
		scoreLabel.setTextAlignment(TextAlignment.CENTER);
		scoreLabel.setLayoutX(2);
		scoreLabel.setLayoutY(2);
		nodes.add(scoreBox);
		nodes.add(scoreLabel);
		updateScore();
		update();

		input.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyInput) {
				if (keyInput.getCode() == KeyCode.ENTER) {
					display.setText(display.getText() + "\n" + Commands.input(input.getText()));
					input.setText(Player.getUser().getServer().getIp() + ">");
					input.end();
					display.end();
				} else if (keyInput.getCode() == KeyCode.UP || keyInput.getCode() == KeyCode.DOWN) {
					input.setText(Commands.history(keyInput, input.getText()));
					input.end();
					display.end();
				} else if (keyInput.getCode() == KeyCode.ESCAPE) {
					input.setText(Player.getUser().getServer().getIp() + ">");
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

	public static void update() {
		game.getChildren().clear();
		game.getChildren().addAll(nodes);
	}

	public static void updateScore() {
		if (Hakd.getScore0() == (int) Hakd.getScore1()) {
			scoreLabel.setText("Score = " + Hakd.getScore0());
		} else {
			scoreLabel.setText("Hacker :P"); // more like cheater
		}
	}

	public static AnchorPane getGame() {
		return game;
	}

	public static void setGame(AnchorPane game) {
		FxGameGui.game = game;
	}

	public static ArrayList<Node> getNodes() {
		return nodes;
	}
}
