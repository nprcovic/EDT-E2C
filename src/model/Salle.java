package model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class Salle implements Serializable, Observer{
	/**
	 * Observateur de TypeSalle et Groupe
	 */
	private static final long serialVersionUID = 2249511825711881821L;

	private String name;
	private ArrayList<Groupe> groupes;
	private TypeSalle type;
	private int capacity;

	public Salle(ArrayList<Groupe> groupes, String name, TypeSalle type, int capacity) {
		super();
		setGroupes(groupes);
		setName(name);
		setType(type);
		setCapacity(capacity);
	}

	public Salle() {
		this(new ArrayList<Groupe>() , "", null, 15);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TypeSalle getType() {
		return type;
	}

	public void setType(TypeSalle type) {
		this.type = type;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		//return "Salle [groupes=" + groupes + ", name=" + name + ", type=" + type + ", capacity=" + capacity + "]";
		return getName();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof Groupe)
			getGroupes().remove(o);
		if(o instanceof TypeSalle)
			setType(null);
	}

}
