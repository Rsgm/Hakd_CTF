package hakd.game;

import java.util.ArrayList;

public class File {

	// just some random stuff to use to fill files with
	private final static String[][]	fileData	=
														{
			{ "passwords.txt", "database.data", "private.md5", "sys.dll", "log.txt", "favorites.fav", "properties.ini", },
			{
			"facebook - password\ngoogle - 12345678\nyoutube - letmein\nreddit - 1337h4x0r",
			"Nz7meoh5znS6BP5EUTGe3PtJlYZoXF2yh9utYvnIVzAeJFsarKy3AfVQ1mrY\nt1sRkL8SOMrbVsUfRk7jjK2QtFCZpEgifPvzS4mfqXMBDqCoaytMmblb9g4r",
			"c0ab9d93efb23f024b65c292aa7d3d37\nfb8d98be1265dd88bac522e1b2182140", // sorry if the first one is unreadable
			"51 75 6f 74 68 20 74 68 65 20 73 65 72 76 65 72 20 22 34 30 34 22",
			"<Stormrider> I should bomb something\n<Stormrider> ...and it's off the cuff remarks like that that are the reason I don't log chats\n<Stormrider> Just in case the FBI ever needs anything on me\n<Elzie_Ann> I'm sure they can just get it from someone who DOES log chats.\n*** FBI has joined #gamecubecafe\n<FBI> We saw it anyway.\n*** FBI has quit IRC (Quit: )",
			"google.com\nfacebook.com\nreddit.com\n4chan.org\n9gag.com",
			"DisplayWidth=5760\nDisplayHeight=1080\nRefreshRate=120\nMultiSampleType=2\nVSync=1\nEnvironmentQuality=2\nTextureQuality=3\nShadowQuality=3" } };
	// I wish I had that big of a screen set up

	private String					name;
	private String					data;
	private final int				size;

	public File(boolean flag) {
		if (flag == true) {
			name = "flag";
			data = "guard this with your life";
		} else {
			int r = (int) (Math.random() * fileData[0].length);
			name = fileData[0][r];// names.get(math.random());
			data = fileData[1][r];// data.get(math.random());
		}
		size = (int) (Math.random() * 64) * 64;
	}

	// returns a file with the given name
	public static File findFile(ArrayList<File> files, String name) {
		for (File f : files) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getSize() {
		return size;
	}

	public static String[][] getFileData() {
		return fileData;
	}
}
