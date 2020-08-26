package model;

import java.io.Serializable;

public class CaseEdTSalle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3202671315307545349L;
	private Discipline discipline;
	private SousGroupe sousGroupe;
	private Formateur formateur;
	
	public CaseEdTSalle(Discipline discipline, SousGroupe sousGroupe, Formateur formateur) {
		super();
		this.discipline = discipline;
		this.sousGroupe = sousGroupe;
		this.formateur = formateur;
	}

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	public SousGroupe getSousGroupe() {
		return sousGroupe;
	}

	public void setSousGroupe(SousGroupe sousGroupe) {
		this.sousGroupe = sousGroupe;
	}

	public Formateur getFormateur() {
		return formateur;
	}

	public void setFormateur(Formateur formateur) {
		this.formateur = formateur;
	}

	@Override
	public String toString() {
		return discipline + " pour " + sousGroupe + " par " + formateur;
	}

	
}
