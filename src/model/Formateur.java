package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class Formateur extends Observable implements Serializable, Observer {
	/**
	 * Observateur de Discipline et de Groupe, observé par SousGroupe
	 */
	private static final long serialVersionUID = 821443388435811573L;
	public static Formateur PERSONNE = new Formateur();
	private String name;
	private ArrayList<Groupe> groupes;
	private ArrayList<Discipline> disciplines;
	private Boolean [] disponibilites;

	public Formateur(String name, ArrayList<Groupe> groupes, ArrayList<Discipline> disciplines,
			Boolean [] disponibilites) {
		super();
		setName(name);
		setGroupes(groupes);
		setDisciplines(disciplines);
		setDisponibilites(disponibilites);
	}

	public Formateur() {
		this("", new ArrayList<Groupe>(), new ArrayList<Discipline>(), new	Boolean [] {true, true, true, true, true,true, true, true, true, true,true, true, true, true, true,true, true, true, true, true});
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Groupe> getGroupes() {
		return groupes;
	}

	public void setGroupes(ArrayList<Groupe> groupes) {
		if(this.groupes != null)
			for(Groupe groupe : this.groupes)
				groupe.deleteObserver(this);
		this.groupes = groupes;
		if(groupes != null)
			for(Groupe groupe : groupes)
				groupe.addObserver(this);
	}
	
	public void addGroupe(Groupe groupe) {
		getGroupes().add(groupe);
		groupe.addObserver(this);
	}

	public ArrayList<Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(ArrayList<Discipline> disciplines) {
		if(this.disciplines != null)
			for(Discipline discipline : this.disciplines)
				discipline.deleteObserver(this);
		this.disciplines = disciplines;
		if(disciplines != null)
			for(Discipline discipline : disciplines)
				discipline.addObserver(this);
	}

	public void addDiscipline(Discipline discipline) {
		getDisciplines().add(discipline);
		discipline.addObserver(this);
	}


	public Boolean[] getDisponibilites() {
		return disponibilites;
	}

	public void setDisponibilites(Boolean[] disponibilites) {
		this.disponibilites = disponibilites;
	}
	
	public Boolean getDisponibilite(int i) {
		return disponibilites[i];
	}

	public void setDisponibilite(int i, Boolean disponible) {
		this.disponibilites[i] = disponible;
	}

	public void toggleDisponibilite(int i) {
		this.disponibilites[i] = !disponibilites[i];
	}

	public String toString() {
		return getName();// + "#" + getId();
	}

	@Override
	public void update(Observable o, Object arg) {
		getGroupes().remove(o);
	}

}
