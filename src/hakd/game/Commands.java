package hakd.game;

import hakd.Hakd;
import hakd.fxgui.FxGameGui;
import hakd.fxgui.FxMenuGui;

import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Commands {

	private static int					recall	= 0;						// holds the position of the ArrayList
	private static ArrayList<String>	lines	= new ArrayList<String>();	// holds previously used commands for easy access

	// takes the input and returns the output of the command
	public static String input(String input) {
		String text = new String();

		lines.add(input);
		System.out.println(input);
		text = input + "\n" + commands(input);
		recall = lines.size();

		return text;
	}

	// recalls the previous typed inputs and returns that input
	public static String history(KeyEvent keyInput, String input) { // make a second method with this name with a different event argument for awt
		String text = "";

		if (keyInput.getCode() == KeyCode.DOWN && recall < lines.size() - 1) {
			recall++;
			text = lines.get(recall);
		} else if (keyInput.getCode() == KeyCode.UP && recall > 0) {
			recall--;
			text = lines.get(recall);
		} else {
			return input;
		}
		return text;
	}

	public static String history(/*event keyInput,*/String input) { // TODO work on this after awt is ready
		String text = "";

// if (keyInput.getCode() == KeyCode.DOWN && recall < lines.size() - 1) {
// recall++;
// text = lines.get(recall);
// } else if (keyInput.getCode() == KeyCode.UP && recall > 0) {
// recall--;
// text = lines.get(recall);
// } else {
// return input;
// }
		return text;
	}

	// chooses and runs the specified command
	private static String commands(String command) {
		String text = "";
		ArrayList<String> args = new ArrayList<String>();

		if (command.matches(".*>.+")) {
			Scanner scanner = new Scanner(command);

			scanner.useDelimiter("\\s+");
			scanner.skip(".*>");
			for (int i = 0; i < command.length(); i++) {
				if (scanner.hasNext()) {
					args.add(scanner.next());
				} else {
					break;
				}
			}
			scanner.close();
		} else if (command.matches(".*>$")) { // these two really messes with the parser
			return "Type a command.";
		} else {
			return "Please do not modify the address.";
		}

		if (FxMenuGui.isMenuRunning()/*||AwtMenuGui.isrunning*/) {
			if (args.get(0).equals("start")) {
				if (args.size() < 2 || !args.get(1).matches("^\\d+$")) {
					return error(0);
				}
				Hakd.setServers(Integer.parseInt(args.get(1)));
				Hakd.newGame();
			} else if (args.get(0).equals("credits")) {
				text =
						"This is just a small game, but there are a few people and sites I need to give credit to."
								+ "\nI got the idea for Hak'd from Matt's game Hacknet, thanks for not suing me. Without him this game would not be possible."
								+ "\nAll the simple AI algorithms are my own. The path finding on the other hand is Aaron Steed's path finder library."
								+ "\nI should also mention oracle, because I included a copy of their javaFX library in this.";
			} else if (args.get(0).equals("instructions")) {
				text =
						"How to play - You are the blue dot(server), the red is the enemy, and the light blue are your team members."
								+ "\nOnce in game type help for more commands. The goal is to secure the file by bringing it back to your home base(the one with your flag)."
								+ "\nYou can control your player by typing commands or by clicking on adjecent servers to connect to them.";
			} else if (args.get(0).equals("help")) {
				text =
						"Commands:\nstart [servers] - starts the game generating the specified amount of servers"
								+ "\n	if this number is odd the opposing team gets the extra server"
								+ "\n	I recommend anywhere from 40 through 56 servers for a fun game" + "\nhelp - shows this help messege"
								+ "\ninstructions - how to play" + "\ncredits - awsome people"; //
			} else {
				text = "This is not a recognized command.";
			}
		} else { // if game is running give it different commands than the menu terminal
			if (args.get(0).equals("quit")) {
				FxMenuGui.start();
			} else if (args.get(0).equals("help")) {
				text = help();
			} else if (args.get(0).equals("transfer")) {
				if (args.size() < 3) {
					return error(0);
				}
				text = transfer(args.get(1), args.get(2));
			} else if (args.get(0).equals("connect")) {
				if (args.size() < 2) {
					return error(0);
				}
				return connect(args.get(1));

			} else if (args.get(0).equals("ls")) {
				return ls();

			} else if (args.get(0).equals("open")) {
				if (args.size() < 2) {
					return error(0);
				}
				return open(args.get(1));
			} else if (args.get(0).equals("tag")) {
				if ((Player.getUser().getServer().getxCoord() < 380)) {// if the player is on its side, it can tag an enemy
					Player.getUser().checkEnemy();
				}

			} else {
				text = "This is not a recognized command.";
			}
		}
		return text;
	}

	private static String help() {
		return "Commands:\nconnect [server] - connects you to the specified server at that address,\n	you can also click a server, alt-click will copy the address"
				+ "\nls - list the files on this server"
				+ "\ntransfer [file name] [server] - transfers the specified file to the specified server"
				+ "\nopen [name] - open the specified file on this server"
				+ "\nquit - quits the game and returns to the menu"
				+ "\nhelp - shows this text" + "\ntag - tags an enemy and sends them back to their base";
	}

	private static String error(int number) {
		switch (number) {
			case 0:
				return "command is missing parameters";
			case 1:
				return "server not found";
			case 2:
				return "file not found";
		}
		return "error not found(this is a bug)";
	}

	private static String transfer(String file, String server) {
		if (!file.matches("\\S+") || !server.matches("\\d+")) {
			return error(0);
		}

		Server s = Server.findServer(Integer.parseInt(server));
		File f = File.findFile(Player.getUser().getServer().getFiles(), file);
		if (f != null && Player.getUser().getServer().getConnections().contains(s)) {
			s.getFiles().add(f);
			Player.getUser().getServer().getFiles().remove(f);

			if (s == Map.getFlag2()) {
				Map.getFlag2().getFiles().remove(f);
				Map.getFlag1().getFiles().add(f);
				Hakd.setScore0(Hakd.getScore0() + 1);
				Hakd.setScore1(Hakd.getScore1() + 1);
				FxGameGui.updateScore();
			}
			return "transfer complete";
		} else if (f == null) {
			return error(2);
		} else { // this else statement is not needed but it does add to organization
			return error(1);
		}
	}

	public static String connect(String server) {
		if (!server.matches("\\d+")) {
			return error(0);
		}

		Server newServer = Server.findServer(Integer.parseInt(server));
		Server currentServer = Player.getUser().getServer();
		if (Player.getUser().getServer().getConnections().contains(newServer)) {

			currentServer.changeStance(0);
			for (Player p : Player.getPlayers()) { // if there are other players on the old server, set the color to their stance
				if (p.getServer() == currentServer && p != Player.getUser()) {
					currentServer.changeStance(p.getStance());
				}
			}
			newServer.changeStance(Player.getUser().getStance());
			Player.getUser().setServer(newServer);
			return "connected";
		} else {
			return error(1);
		}
	}

	private static String ls() {
		String text = "";
		for (File f : Player.getUser().getServer().getFiles()) {
			text += f.getName() + "\n";
		}
		return text;
	}

	private static String open(String name) {
		if (!name.matches("\\S+")) {
			return error(0);
		}

		for (File f : Player.getUser().getServer().getFiles()) {
			if (f.getName().equals(name)) {
				return f.getData();
			}
		}
		return error(2);
	}

	public static int getRecall() {
		return recall;
	}

	public static void setRecall(int recall) {
		Commands.recall = recall;
	}

	public static ArrayList<String> getLines() {
		return lines;
	}
}
