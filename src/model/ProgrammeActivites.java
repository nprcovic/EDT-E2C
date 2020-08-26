package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class ProgrammeActivites extends Observable implements Serializable, Observer {
	/**
	 * Observateur de Activite, observé par SousGroupe
	 */
	private static final long serialVersionUID = -131924516282828362L;
	private String name;
	private ArrayList<Activite> programme;

	
	
	public ProgrammeActivites(String name, ArrayList<Activite> programme) {
		super();
		setName(name);
		setProgramme(programme);
	}

	public ProgrammeActivites(ArrayList<Activite> programme) {
		this("", programme);
	}

	public ProgrammeActivites() {
		this("", new ArrayList<Activite>());
	}

	public Activite getActivite(Discipline discipline) {
		for(Activite activite : getProgramme()) {
			System.out.println(activite);
			if(activite.getDiscipline().equals(discipline))
				return activite;
		}
		return null;
	}
	
	public ArrayList<Activite> getProgramme() {
		return programme;
	}

	public void setProgramme(ArrayList<Activite> programme) {
		if(this.programme != null)
			for(Activite activite : this.programme)
				activite.deleteObserver(this);
		this.programme = programme;
		if(programme != null)
			for(Activite activite : programme)
				activite.addObserver(this);
	}
	
	public void addActivite(Activite activite) {
		getProgramme().add(activite);
		activite.addObserver(this);
	}
	
	public int nbActivites() {
		int nb = 0;
		for(Activite activite : getProgramme())
			nb += activite.getOccurences();
		return nb;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void update(Observable o, Object arg) {
		getProgramme().remove(o);		
	}
	
	
}
