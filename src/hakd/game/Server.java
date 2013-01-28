package hakd.game;

import hakd.Hakd;
import hakd.fxgui.FxGameGui;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import ai.pathfinder.Node;

public class Server {
	private ArrayList<File>				files		= new ArrayList<File>();
	private static ArrayList<Integer>	ips			= new ArrayList<Integer>();
	private final ArrayList<Server>		connections	= new ArrayList<Server>();	// allowable connections

	private static final int			radius		= 12;
	private Circle						circle;
	private int							xCoord;
	private int							yCoord;
	private int							ip;
	private Node						node;

	public Server(int x, int y, boolean flag) {
		xCoord = x;
		yCoord = y;

		do {
			ip = (int) (Math.random() * 64 + 1); // lets say these are on the same network, and only have 64 possible addresses instead of 256
			if (ips.contains(ip)) {
				System.out.println("Address taken");
			}
		} while (ips.contains(ip));
		ips.add(ip);

		node = new Node(xCoord, yCoord);
		Map.getPathNodes().add(node);

		int r = (int) (Math.random() * File.getFileData()[0].length);
		for (int i = 0; i < r; i++) {
			files.add(new File(false));
		}

		if (flag == true) {
			files.add(new File(true));
		}

		if (Hakd.isJava7()) { // create server icon
			circle = new Circle();
			circle.setCenterX(x);
			circle.setCenterY(y);
			circle.setRadius(radius);
			changeStance(0);
			Tooltip.install(circle, new Tooltip(ip + ""));
			FxGameGui.getNodes().add(circle);
			FxGameGui.update();

			circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouse) {
					if (mouse.isAltDown()) {
						Clipboard clipboard = Clipboard.getSystemClipboard();
						ClipboardContent content = new ClipboardContent();
						content.putString(ip + "");
						clipboard.setContent(content);
					} else {
						Commands.connect(ip + "");
					}
				}
			});
		} else {

		}
	}

	// finds the server at the given ip/address
	public static Server findServer(int ip) {
		for (Server s : Map.getServers()) {
			if (s.ip == ip) {
				return s;
			}
		}
		return null;
	}

	// changes the color of the server based on the stance of the player
	public void changeStance(int stance) { // occupied by: 0 == none(neutral), 1 == enemy, 2 == friendly, 3 == player
		if (Hakd.isJava7()) {
			if (stance == 0) {
				circle.setId("neutral-network");
			} else if (stance == 1) {
				circle.setId("enemy-network");
			} else if (stance == 2) {
				circle.setId("friendly-network");
			} else if (stance == 3) {
				circle.setId("player-network");
			}
		} else {
			// awt stuff
		}
	}

	ArrayList<File> getFiles() {
		return files;
	}

	int getxCoord() {
		return xCoord;
	}

	int getyCoord() {
		return yCoord;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	public static ArrayList<Integer> getIps() {
		return ips;
	}

	public static void setIps(ArrayList<Integer> ips) {
		Server.ips = ips;
	}

	public int getIp() {
		return ip;
	}

	public void setIp(int ip) {
		this.ip = ip;
	}

	public static double getRadius() {
		return radius;
	}

	public ArrayList<Server> getConnections() {
		return connections;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
