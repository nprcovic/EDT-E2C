package model;

import java.io.Serializable;

public class Creneau implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4944893611746582401L;
	private Jour jour;
	private int starttime;
	private int endtime;
	
	private static String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
	
	public static String getName(int i) {
		return jours[i / 4] + " seq " + i % 4;
	}
	
	public Creneau(Jour jour, int starttime, int endtime) {
		super();
		this.setJour(jour);
		this.setStarttime(starttime);
		this.setEndtime(endtime);
	}

	public Jour getJour() {
		return jour;
	}

	public void setJour(Jour jour) {
		this.jour = jour;
	}

	public int getStarttime() {
		return starttime;
	}

	public void setStarttime(int starttime) {
		this.starttime = starttime;
	}

	public int getEndtime() {
		return endtime;
	}

	public void setEndtime(int endtime) {
		this.endtime = endtime;
	}
	
	public String toString() {
		return "" + getJour() + " " + getStarttime() / 100 + "h" + getStarttime() % 100;
	}
}
