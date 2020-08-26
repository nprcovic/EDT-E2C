package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class SousGroupe extends Observable implements Serializable, Observer {
	/**
	 * Observateur de ProgrammeActivites, observé par Groupe
	 */
	private static final long serialVersionUID = -3364477915103004454L;
	static private int idGen = 0;
	private int id;
	private Groupe groupe;
	private int number;
	private int capacity;
	private Formateur referent;
	private ProgrammeActivites programmeActivites;
	private ProgrammeActivites profilActivites;

	public SousGroupe(Groupe groupe, int number, int capacity, Formateur referent) {
		super();
		this.id = idGen++;
		setGroupe(groupe);
		setNumber(number);
		setCapacity(capacity);
		setReferent(referent);
		programmeActivites = new ProgrammeActivites();
		setProfilActivites(new ProgrammeActivites());
	}

	public Groupe getGroupe() {
		return groupe;
	}

	public void setGroupe(Groupe groupe) {
		if(this.groupe != null)
			this.groupe.deleteObserver(this);
		this.groupe = groupe;
		if(groupe != null)
			groupe.addObserver(this);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Formateur getReferent() {
		return referent;
	}

	public void setReferent(Formateur referent) {
		if(this.referent != null)
			this.referent.deleteObserver(this);
		this.referent = referent;
		if(referent != null)
			referent.addObserver(this);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return groupe.getName() + getNumber();
	}
	
	public ProgrammeActivites getProgrammeActivites() {
		return programmeActivites;
	}

	/*public void setProgrammeActivites(ProgrammeActivites programmeActivites) {
		this.programmeActivites = programmeActivites;
	}*/


	public ProgrammeActivites getProfilActivites() {
		return profilActivites;
	}

	public void setProfilActivites(ProgrammeActivites profilActivites) {
		if(this.profilActivites != null)
			this.profilActivites.deleteObserver(this);
		this.profilActivites = profilActivites;
		if(profilActivites != null)
			profilActivites.addObserver(this);
	}

	@Override
	public String toString() {
		//return "SousGroupe [groupe=" + groupe + ", number=" + number + ", capacity=" + capacity + ", referent=" + referent + "]";
		return getName();// + "#" + getId();
	}

	public void initProgrammeWithProfil() {
		ArrayList<Activite> programme = this.getProgrammeActivites().getProgramme();
		for(Activite activite : programme)
			activite.deleteObserver(this.getProgrammeActivites());
		programme.clear();
		for(Activite activite : this.getProfilActivites().getProgramme()) {
			Activite newActivite = new Activite(activite.getDiscipline(), activite.getOccurences());
			programme.add(newActivite);
			newActivite.addObserver(this.getProgrammeActivites());
		}
		//this.getProgrammeActivites().setProgramme(programme);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.setProfilActivites(null);
	}

}
