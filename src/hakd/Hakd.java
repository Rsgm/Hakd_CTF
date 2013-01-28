package hakd;

import hakd.fxgui.FxGameGui;
import hakd.fxgui.FxGuiController;
import hakd.fxgui.FxMenuGui;
import hakd.game.Map;
import hakd.game.Player;
import hakd.game.Server;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Hakd extends JApplet {// the ctf version of hak'd designed for facebook
	private static final long	serialVersionUID	= 1L;

	private static Graphics		awtGraphics;
	private static JFXPanel		fxPane;
	private static boolean		java7;
	private static boolean		running				= false;

	// these values drastically change the map, default is minR = 60 and maxR = 100
	private final static int	minR				= 60;		// the closest a server can be to another
	private final static int	maxR				= 100;		// the farthest a server can me from another, and still be able to connect
	private static int			servers;

	private static int			classLoopCounter	= 0;

	private static int			score0				= 0;
	private static double		score1				= 0;		// ;)

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame("hakd");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JApplet applet = new Hakd();
				applet.init();

				frame.setContentPane(applet.getContentPane());

				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				applet.start();
			}
		});
	}

	@Override
	public void init() {
		awtGraphics = getGraphics();
// if (System.getProperty("java.version").startsWith("1.7.")) {
		java7 = true; // this may work in java 1.6, so i will leave it in javafx mode
// } else {
// java7 = false;
// }
	}

	@Override
	public void start() {
		super.start();
		running = true;
		if (java7 == true) {
			fxPane = new JFXPanel();
			getContentPane().add(fxPane);
			getContentPane().setVisible(true);

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					startFx(fxPane);
				}
			});
		} else {

		}
	}

	private static void startFx(JFXPanel fxPane) { // This method is invoked on JavaFX thread
		FxMenuGui.setMenu(new AnchorPane());
		FxGuiController.setScene(new Scene(FxMenuGui.getMenu(), 760, 600));
		fxPane.setScene(FxGuiController.getScene());
		fxPane.setVisible(true);
		FxGuiController.start();
	}

	// resets everything and starts a new game
	public static void newGame() {
		System.out.println("new map " + classLoopCounter);

		Map.getServers().clear();
		Server.getIps().clear();

		ArrayList<Integer> xCoords = new ArrayList<Integer>();
		ArrayList<Integer> yCoords = new ArrayList<Integer>();

		xCoords.clear();
		yCoords.clear();

		for (int i = 0; i < servers; i++) {
			boolean tooClose;
			boolean serverConnected;
			int x1;
			int y1;
			int loopCounter = 0;

			do {
				loopCounter++;
				System.out.println("new points " + loopCounter); // if this prints something like ...22 ...23 ...1 ...2 that means it found a spot
				if (loopCounter >= 10000) { // arbitrarily large number that should still take under a second to get to
					classLoopCounter++;
					if (classLoopCounter >= 100) { // arbitrary number that should still take under a second to get to
						if (java7) {
							FxMenuGui.getDisplay().setText(
									FxMenuGui.getDisplay().getText()
											+ "\nThe number you choose is either too big or too small, I recommend 20 to 50 servers.");
						} else {

						}
						return;
					}
					newGame();
					return;
				} else {
					tooClose = false;
				}
				serverConnected = false;

				if (i <= servers / 2 - 1) {
					x1 = (int) (Math.random() * (370 - Server.getRadius()) + Server.getRadius() + 5); // 5<=x<=380
				} else {
					x1 = (int) (Math.random() * (370 - Server.getRadius()) + Server.getRadius() + 375); // 380<=x<=755
				}
				y1 = (int) (Math.random() * (380 - Server.getRadius()) + Server.getRadius() + 5); // 5<=x<=395

				for (int j = 0; j < xCoords.size(); j++) {
					int x2 = xCoords.get(j);
					int y2 = yCoords.get(j);

					if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= minR) {
						System.out.println("too close to another network");
						tooClose = true;
						break;
					}
				}

				for (int j = 0; j < xCoords.size(); j++) {
					// this checks to make sure there are at least n amount of servers within maxR of each server(i)
					if (tooClose == true) {
						break;
					}

					int x2 = xCoords.get(j);
					int y2 = yCoords.get(j);

					if (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= maxR && (x1 != x2 || y1 != y2)) {
						// if there is at least 1 network closer than minR
						serverConnected = true;
						break;
					}
				}
			} while (i != 0 && (tooClose == true || serverConnected == false));
			// if this server is too close to another or has no connections then repeat
			xCoords.add(x1);
			yCoords.add(y1);
		}
		if (java7) {
			FxGameGui.start();
		} else {
			/*AwtGameGui.start()*/
		}

		int flag1 = xCoords.indexOf(Collections.max(xCoords)); // for now the flags will be in the farthest servers
		int flag2 = xCoords.indexOf(Collections.min(xCoords)); // I may change this to randomly choose for difficulty
		for (int i = 0; i < servers; i++) {
			if (i == flag1) {
				Server s = new Server(xCoords.get(i), yCoords.get(i), true);
				Map.getServers().add(s);
				Map.setFlag1(s);
			} else if (i == flag2) {
				Server s = new Server(xCoords.get(i), yCoords.get(i), true);
				Map.getServers().add(s);
				Map.setFlag2(s);
			} else {
				Server s = new Server(xCoords.get(i), yCoords.get(i), false);
				Map.getServers().add(s);
			}
		}
		Map.createConnections();

		Player.getPlayers().add(new Player(1, 1));
		Player.getPlayers().add(new Player(1, 1));
		Player.getPlayers().add(new Player(1, 2));
		Player.getPlayers().add(new Player(2, 1));
		Player.getPlayers().add(new Player(2, 1));
		Player.getPlayers().add(new Player(3, 0));

	}

	public static boolean isJava7() {
		return java7;
	}

	public static Graphics getAwtGraphics() {
		return awtGraphics;
	}

	public static void setAwtGraphics(Graphics awtGraphics) {
		Hakd.awtGraphics = awtGraphics;
	}

	public static int getMinr() {
		return minR;
	}

	public static int getMaxr() {
		return maxR;
	}

	public static int getServers() {
		return servers;
	}

	public static void setServers(int servers) {
		Hakd.servers = servers;
	}

	public static boolean isRunning() {
		return running;
	}

	public static int getScore0() {
		return score0;
	}

	public static void setScore0(int score0) {
		Hakd.score0 = score0;
	}

	public static double getScore1() {
		return score1;
	}

	public static void setScore1(double score1) {
		Hakd.score1 = score1;
	}
}

// -----ideas-----
// server with the flag file is the farthest server from the middle, done.

// can only transfer a file or move one server at a time

// 3 vs 3, (2 ai 1 player) vs (3 ai), kind of done

// can only see your own team, enemy has to be on the same server as you, changed my mind
