package hakd.game;

import hakd.Hakd;
import hakd.fxgui.FxGameGui;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class Player {
	private static int					maxPlayer	= 6;						// for now, maybe
	private static ArrayList<Player>	players		= new ArrayList<Player>();
	private static Player				user;
	// sorry this is a bit confusing, but this class has the word "player" all over the place. I thought it would help a little.

	private final int					stance;								// 1 == enemy, 2 == friendly, 3 == user/player
	private Server						server;
	private int							ai;									// player ai: 0 == player, 1 == defensive, 2 == offensive
	private boolean						hasFlag;

	public Player(int stance, int ai) {
		this.stance = stance;
		this.ai = ai;
		hasFlag = false;

		switch (stance) { // sometimes starts a player on the same server as another, there is and won't be anything restricting this
			case 1: // enemy
				// picks from the back half of the server array
				server = Map.getServers().get((int) (Math.random() * (Hakd.getServers() / 2) + (Hakd.getServers() / 2)));
				server.changeStance(stance); // for debug purposes keep this, later only you can see them if they are on the same server
				break;
			case 2: // friendly
				// first half of the server array
				server = Map.getServers().get((int) (Math.random() * (Hakd.getServers() / 2)));
				server.changeStance(stance);

				break;
			case 3: // user
				// again, the first half of the server array
				server = Map.getServers().get((int) (Math.random() * (Hakd.getServers() / 2)));
				server.changeStance(stance);
				ai = 0;
				user = this;
				break;
		}

		// these should run the player updates
		if (ai == 1) { // I really hate these, but they work
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					while (Hakd.isRunning()) {
						try {
							Thread.sleep((int) (Math.random() * 2500 + 2000));
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage("Cancelled");
								break;
							}
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								updateDefense();
							}
						});
					}
					return null;
				}
			};
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
		} else if (ai == 2) {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					while (Hakd.isRunning()) {
						try {
							Thread.sleep((int) (Math.random() * 2500 + 2000));
						} catch (InterruptedException interrupted) {
							if (isCancelled()) {
								updateMessage("Cancelled");
								break;
							}
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								updateOffense();
							}
						});
					}
					return null;
				}
			};
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
		}

	}

	// moves/updates defensive players on either side
	public void updateDefense() {
		Server s = null;
		int loopCounter = 0; // used in the while loop
		Server enemy = null; // if its on the wrong side not if its close by

		checkEnemy(); // this stops an error from not being able to path find to them while they are on the same server

		for (Player p : players) { // check if an enemy is on this side
			if ((p.getServer().getxCoord() < 380 && p.getStance() == 1 && stance == 2)
					|| (p.getServer().getxCoord() >= 380 && (p.getStance() == 2 || p.getStance() == 3) && stance == 1)) {
				enemy = p.getServer();
			}
		}

		if (enemy != null && Math.random() >= 0.6) { // if there is an enemy then go towards it
			s = Map.pathFind(server, enemy);
		} else { // if no player on side its side, then randomly move
			do {
				loopCounter++;
				s = server.getConnections().get((int) (Math.random() * server.getConnections().size())); // if null pointer, subtract one from size

				if ((s.getxCoord() >= 380 && stance == 1) || (s.getxCoord() < 380 && stance == 2)) {// makes sure it stays on its side
					break;
				}
			} while (loopCounter < 1000); // an arbitrary, large number
		}

		// this piece of code is very important {
		server.changeStance(0);
		for (Player p : players) { // if there are other players on the old server, set the color to their stance
			if (p.getServer() == server && p != this) {
				server.changeStance(p.getStance());
			}
		}

		s.changeStance(stance);
		server = s;
		checkEnemy(); // } it updates both servers and sets the player to one of them
	}

	// moves/updates offensive players on either side
	public void updateOffense() {
		Server s = null;
		if ((server.getxCoord() >= 380 && stance == 1) || (server.getxCoord() < 380 && stance == 2)) {// if the player is on its side, it can tag an
// enemy
			checkEnemy();
		}

		if (!hasFlag && stance == 1 && server == Map.getFlag2()) {
			hasFlag = true;
		} else if (!hasFlag && stance == 2 && server == Map.getFlag1()) {
			hasFlag = true;
		} else if (hasFlag && stance == 1 && server == Map.getFlag1()) {
			hasFlag = false;
		} else if (hasFlag && stance == 2 && server == Map.getFlag2()) {
			hasFlag = false;
		}

		if (!hasFlag && stance == 1) { // getting flag
			s = Map.pathFind(server, Map.getFlag2());
		} else if (!hasFlag && stance == 2) { // just in case I add other friendly offense players
			s = Map.pathFind(server, Map.getFlag1());
		} else if (hasFlag && stance == 1) { // returning with flag
			s = Map.pathFind(server, Map.getFlag1());
			Hakd.setScore0(Hakd.getScore0() - 1);
			Hakd.setScore1(Hakd.getScore1() - 1);
			FxGameGui.updateScore();
		} else if (hasFlag && stance == 2) {
			s = Map.pathFind(server, Map.getFlag2());
			Hakd.setScore0(Hakd.getScore0() + 1);
			Hakd.setScore1(Hakd.getScore1() + 1);
			FxGameGui.updateScore();
		}

		if (Math.random() >= 0.8) {
			s = server.getConnections().get((int) (Math.random() * server.getConnections().size()));
		}

		if ((s.getxCoord() >= 380 && stance == 1) || (s.getxCoord() < 380 && stance == 2)) {// if the player is on its side, it can tag an enemy
			checkEnemy();
		}

		server.changeStance(0);
		for (Player p : players) { // if there are other players on the old server, set the color to their stance
			if (p.getServer() == server && p != this) {
				server.changeStance(p.getStance());
			}
		}
		server = s;
		server.changeStance(stance);
	}

	// checks for an enemy on the same server, and respawns them
	public void checkEnemy() {
		for (Player p : players) {
			if (p.getServer() == server) {
				if (p.getStance() == 1 && (stance == 2 || stance == 3)) { // if other player is enemy
					p.setServer(Map.getFlag1()); // move them to the spawn
				} else if ((p.getStance() == 2 || p.getStance() == 3) && stance == 1) { // if friendly
					p.setServer(Map.getFlag2());
				}
				p.getServer().changeStance(p.getStance());
			}
		}
	}

	public static int getMaxPlayer() {
		return maxPlayer;
	}

	public static void setMaxPlayer(int maxPlayer) {
		Player.maxPlayer = maxPlayer;
	}

	public static ArrayList<Player> getPlayers() {
		return players;
	}

	public static void setPlayers(ArrayList<Player> players) {
		Player.players = players;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public static Player getUser() {
		return user;
	}

	public int getStance() {
		return stance;
	}

	public int getAi() {
		return ai;
	}

	public void setAi(int ai) {
		this.ai = ai;
	}

	public static void setUser(Player user) {
		Player.user = user;
	}

}
