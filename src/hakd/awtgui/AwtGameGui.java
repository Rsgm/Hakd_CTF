package hakd.awtgui;

import hakd.Hakd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class AwtGameGui {
	private static Graphics	g	= Hakd.getAwtGraphics();

	public static void start() {
		g.setColor(Color.black);
		g.drawRect(0, 0, 760, 400);
		drawLine(380, 0, 380, 400);
	}

	public static void drawServer(int x, int y, Integer ip, Color c) {
		g.setColor(c);
		g.fillOval(x, y, 25, 25);
		g.setColor(Color.orange);
		g.setFont(new Font("Default", 0, 13));
		g.drawChars(ip.toString().toCharArray(), 0, ip.toString().length(), x + 3, y + 17);
	}

	public static void drawLine(int x1, int y1, int x2, int y2) {
		Hakd.getAwtGraphics().setColor(Color.gray);
		Hakd.getAwtGraphics().drawLine(x1, y1, x2, y2);
	}

}
