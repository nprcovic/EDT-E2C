package control.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Preferences implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2788618850534797210L;
	private File directory;
	private int windowHeight;
	private int windowWidth;
	
	public Preferences() {
		super();
		//directory = new File("");
		windowHeight = 300;
		windowWidth = 500;
	}
	
	public void save() {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(".EDTconfig"));
			oos.writeObject(this);
			System.out.println("Préférences sauvegardées");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this);
	}
	
	/***
	 * Charge les préférences
	 * @param name
	 * @return
	 */
	public static Preferences load() {
		File fichier =  new File(".EDTconfig");
		if(fichier.exists())
			return load(fichier);
		else {
			System.out.println("Fichier de configuration non trouvé");			
			return new Preferences();
		}
	}
	
	/***
	 * Charge les préférences stockée dans le fichier
	 * @param fichier
	 * @return
	 */
	static public Preferences load(File fichier) {
			ObjectInputStream ois = null;
			Preferences preferences;
			try {
				ois = new ObjectInputStream(new FileInputStream(fichier));
				preferences = (Preferences)ois.readObject();
				System.out.println("Préférences chargées");
				ois.close();
			}
			catch (IOException | ClassNotFoundException e) {
				System.out.println("Fichier de configuration non chargé");
				preferences = new Preferences();
			}
			return preferences;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	@Override
	public String toString() {
		return "Preferences [directory=" + directory + ", windowHeight=" + windowHeight + ", windowWidth=" + windowWidth
				+ "]";
	}

	
	
}
