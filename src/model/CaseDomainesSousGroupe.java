package model;

import java.util.ArrayList;

public class CaseDomainesSousGroupe {
	private ArrayList<Discipline> disciplines;
	private ArrayList<Salle> salles;
	private ArrayList<Formateur> formateurs;
	private Discipline disciplineChoisie;
	private Salle salleChoisie;
	private Formateur formateurChoisi;
	private boolean assigned = false;
	
	
	public CaseDomainesSousGroupe() {
		super();
		this.disciplines = new ArrayList<Discipline>();
		this.salles = new ArrayList<Salle>();
		this.formateurs = new ArrayList<Formateur>();
	}
	
	public ArrayList<Discipline> getDisciplines() {
		return disciplines;
	}
	public void setDisciplines(ArrayList<Discipline> disciplines) {
		this.disciplines = disciplines;
	}
	public ArrayList<Salle> getSalles() {
		return salles;
	}
	public void setSalles(ArrayList<Salle> salles) {
		this.salles = salles;
	}
	public ArrayList<Formateur> getFormateurs() {
		return formateurs;
	}
	public void setFormateurs(ArrayList<Formateur> formateurs) {
		this.formateurs = formateurs;
	}

	public Discipline getDisciplineChoisie() {
		return disciplineChoisie;
	}

	public void setDisciplineChoisie(Discipline disciplineChoisie) {
		this.disciplineChoisie = disciplineChoisie;
	}

	public Salle getSalleChoisie() {
		return salleChoisie;
	}

	public void setSalleChoisie(Salle salleChoisie) {
		this.salleChoisie = salleChoisie;
	}

	public Formateur getFormateurChoisi() {
		return formateurChoisi;
	}

	public void setFormateurChoisi(Formateur formateurChoisi) {
		this.formateurChoisi = formateurChoisi;
	}

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	
}
