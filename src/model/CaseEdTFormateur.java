package model;

import java.io.Serializable;

public class CaseEdTFormateur implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3986462521295726407L;
	private Discipline discipline;
	private Salle salle;
	private SousGroupe sousGroupe;
	
	public CaseEdTFormateur(Discipline discipline, Salle salle, SousGroupe sousGroupe) {
		super();
		this.discipline = discipline;
		this.salle = salle;
		this.sousGroupe = sousGroupe;
	}

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline activite) {
		this.discipline = activite;
	}

	public Salle getSalle() {
		return salle;
	}

	public void setSalle(Salle salle) {
		this.salle = salle;
	}

	public SousGroupe getSousGroupe() {
		return sousGroupe;
	}

	public void setSousGroupe(SousGroupe sousGroupe) {
		this.sousGroupe = sousGroupe;
	}

	@Override
	public String toString() {
		return  discipline + " en " + salle + " avec " + sousGroupe;
	}
	
	
}
