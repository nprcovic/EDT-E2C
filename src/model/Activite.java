package model;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;


public class Activite extends Observable implements Serializable, Observer{
	/**
	 * Observateur de Matière, observé par ProgrammeActivites
	 */
	private static final long serialVersionUID = 2098138012914348716L;
	private Discipline discipline;
	private int occurences;
	
	
	public Activite(Discipline discipline, int occurence) {
		super();
		setDiscipline(discipline);
		setOccurences(occurence);
	}


	public Discipline getDiscipline() {
		return discipline;
	}


	public void setDiscipline(Discipline discipline) {
		if(this.discipline != null)
			this.discipline.deleteObserver(this);
		this.discipline = discipline;
		discipline.addObserver(this);
	}


	public int getOccurences() {
		return occurences;
	}


	public void setOccurences(int occurence) {
		this.occurences = occurence;
	}



	@Override
	public String toString() {
		return "Activite{" + discipline.getName() + "," + occurences +
				'}' ;
	}
	
	@Override
	public void update(Observable observable, Object o) {
		notifyObservers();
	}
	
}