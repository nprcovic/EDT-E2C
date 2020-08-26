package model;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class Discipline extends Observable implements Serializable, Observer{
	/**
	 * Observateur de TypeSalle, observé par Formateur et Activite
	 */
	private static final long serialVersionUID = -8094163201016174348L;

	static public Discipline TEMPS_LIBRE = new Discipline("", new ArrayList<TypeSalle>(), new Boolean [] {true, true, true, true, true,true, true, true, true, true,true, true, true, true, true,true, true, true, true, true}, false);

	private String name;
	private ArrayList<TypeSalle> typesSalle;
	private Boolean [] creneaux;
	private boolean necessiteFormateur;
	private Color color = Color.WHITE;

	public Discipline(String name, ArrayList<TypeSalle> typesSalle, Boolean[] creneaux, boolean necessiteFormateur) {
		super();
		setName(name);
		setTypesSalle(typesSalle);
		setCreneaux(creneaux);
		setNecessiteFormateur(necessiteFormateur);
	}


	public Discipline() {
		this("", new ArrayList<TypeSalle>(), new Boolean [] {true, true, true, true, true,true, true, true, true, true,true, true, true, true, true,true, true, true, true, true}, true);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<TypeSalle> getTypesSalle() {
		return typesSalle;
	}

	public void setTypesSalle(ArrayList<TypeSalle> typesSalle) {
		if(this.typesSalle != null)
			for(TypeSalle typeSalle : this.typesSalle)
				typeSalle.deleteObserver(this);
		this.typesSalle = typesSalle;
		if(typesSalle != null)
			for(TypeSalle typeSalle : typesSalle)
				typeSalle.addObserver(this);
	}

	public void addTypeSalle(TypeSalle typeSalle) {
		getTypesSalle().add(typeSalle);
		typeSalle.addObserver(this);
	}

	public Boolean []  getCreneaux() {
		return creneaux;
	}

	public void setCreneaux(Boolean[]  creneaux) {
		this.creneaux = creneaux;
	}

	public Boolean hasCreneau(int i) {
		return creneaux[i];
	}
	
	public void setCreneau(int i, Boolean autorise) {
		creneaux[i] = autorise;
	}
	
	public void toggleCreneau(int i) {
		creneaux[i] = ! creneaux[i];
	}

	public Boolean getNecessiteFormateur() {
		return necessiteFormateur;
	}

	public void setNecessiteFormateur(Boolean necessiteFormateur) {
		this.necessiteFormateur = necessiteFormateur;
	}

	public void ToggleNecessiteFormateur() {
		setNecessiteFormateur(! getNecessiteFormateur());
	}
	

	public javafx.scene.paint.Color getColor() {
		return new javafx.scene.paint.Color(color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0, color.getAlpha()/255.0);
	}


	public void setColor(javafx.scene.paint.Color color) {
		this.color = new Color((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getOpacity());
	}


	@Override
	public String toString() {
	//	return "Discipline [id=" + id + ", name=" + name + ", typesSalle=" + typesSalle + ", creneaux="
	//			+ Arrays.toString(creneaux) + ", necessiteFormateur=" + necessiteFormateur + "]";
		return getName();
	}

	@Override
	public void update(Observable o, Object arg) {
		getTypesSalle().remove(o);
	}

}
