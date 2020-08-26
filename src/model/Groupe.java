package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Groupe extends Observable implements Serializable, Observer{
	/**
	 * Observateur de SousGroupe, observé par Formateur
	 */
	private static final long serialVersionUID = 1181695328782022811L;

	private String name;
	//private String specificity;
	private ArrayList<SousGroupe> sousGroupes;
	
	public static Groupe INDIFFERENT = new Groupe(null, null);

	public Groupe(String name, ArrayList<SousGroupe> sousGroupes) {
		super();
		setName(name);
		setSousGroupes(sousGroupes);
	}

	public Groupe() {
		this("", new ArrayList<SousGroupe>());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SousGroupe> getSousGroupes() {
		return sousGroupes;
	}

	public void setSousGroupes(ArrayList<SousGroupe> sousGroupes) {
		if(this.sousGroupes != null)
			for(SousGroupe sousGroupe : this.sousGroupes)
				sousGroupe.deleteObserver(this);
		this.sousGroupes = sousGroupes;
		if(sousGroupes != null)
			for(SousGroupe sousGroupe : sousGroupes)
				sousGroupe.addObserver(this);
	}

	public int nbSousGroupes() {
		return sousGroupes.size();
	}

	@Override
	public String toString() {
		//return "Groupe [name=" + name + ", sousGroupes=" + sousGroupes + "]";
		return getName();// + "#" + getId();
	}

	@Override
	public void update(Observable o, Object arg) {
		getSousGroupes().remove(o);
	}
	

}
