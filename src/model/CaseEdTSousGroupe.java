package model;

import java.io.Serializable;

public class CaseEdTSousGroupe implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7823774094746841315L;
	private Discipline discipline;
	private Salle salle;
	private Formateur formateur;
	
	public CaseEdTSousGroupe(Discipline discipline, Salle salle, Formateur formateur) {
		super();
		this.discipline = discipline;
		this.salle = salle;
		this.formateur = formateur;
	}
	
	public Discipline getDiscipline() {
		return discipline;
	}
	public void setActivite(Discipline activite) {
		this.discipline = activite;
	}
	public Salle getSalle() {
		return salle;
	}
	public void setSalle(Salle salle) {
		this.salle = salle;
	}
	public Formateur getFormateur() {
		return formateur;
	}
	public void setFormateur(Formateur formateur) {
		this.formateur = formateur;
	}
	
	public String toString() {
		return getDiscipline() + " en " + getSalle() + " avec " + getFormateur();
	}
	public boolean equals(CaseEdTSousGroupe autre){
		return (this.getDiscipline().getName().equals(autre.getDiscipline().getName())
				&& ((this.salle == null && autre.salle == null) ||
						(this.salle == null && autre.salle == null && this.salle.getName() == autre.getSalle().getName()))
				&& ((this.formateur == null && autre.formateur == null) ||
						(this.formateur != null && autre.formateur != null && this.formateur.getName().equals(autre.getFormateur().getName()))));
	}
}
