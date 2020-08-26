package model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Probleme implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7531016562241606391L;
	private ArrayList<Formateur> formateurs;
	private ArrayList<Salle> salles;
	private ArrayList<Groupe> groupes;
	private ArrayList<Discipline> disciplines;
	private ArrayList<TypeSalle> typesSalles;
	private ArrayList<ProgrammeActivites> profilsActivites;

	private int nbSols;
	
	public Probleme() {
		this.nbSols = 1;
		
		formateurs = new ArrayList<Formateur>();
		salles  = new ArrayList<Salle>();
		groupes = new ArrayList<Groupe>();
		disciplines = new ArrayList<Discipline>();
		typesSalles = new ArrayList<TypeSalle>();
		profilsActivites = new ArrayList<ProgrammeActivites>();
	}
	

	/***
	 * Sauvegarde l'instance
	 * @param probFile
	 */
	public void save(File probFile) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(probFile));
			oos.writeObject(this);
			System.out.println("Instance du problème sauvegardée");
		} catch (IOException e) {
			System.out.println("Instance du problème NON sauvegardée");
		}finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this);
	}
	
	/***
	 * sauve l'instance dans le chemin indiqué
	 * @param path
	 */
	public void save(String path) {
		File fichier = new File(path);
		save(fichier);
	}
	
	/***
	 * Charge le problème stocké dans le fichier
	 * @param fichier
	 * @return
	 */
	static public Probleme load(File fichier) {
			ObjectInputStream ois = null;
			Probleme probleme;
			try {
				ois = new ObjectInputStream(new FileInputStream(fichier));
				probleme = (Probleme)ois.readObject();
				System.out.println(probleme);
				System.out.println("Instance du problème chargée");
				ois.close();
			}
			catch (IOException | ClassNotFoundException e) {
				System.out.println("Instance du problème non trouvée");
				probleme = new Probleme();
			}
			return probleme;
	}
	
	/***
	 * Charge l'instance du problème dont le chemin est 'pathName'
	 * @param pathName
	 * @return
	 */
	static public Probleme load(String pathName) {
		File fichier = new File(pathName);
		return load(fichier);
	}
	
	/***
	 * Affichage textuel du contenu d'une instance
	 */
	/*public void print(){
		System.out.println();
		for (Formateur formateur : getFormateurs()) {
			System.out.print(formateur + ", ");
		}
		System.out.println();
		for (Salle salle : getSalles()) {
			System.out.print(salle + ", ");
		}
		System.out.println();
		for (Groupe groupe : getGroupes()) {
			System.out.print(groupe);
		}
		System.out.println();
		for (Discipline discipline : getDisciplines()) {
			System.out.println(discipline);
		}
		System.out.println();
		for (TypeSalle typeSalle : getTypesSalles()) {
			System.out.println();
		}
		System.out.println();
	}*/

	/***
	 * Accès aux formateurs
	 */
	public ArrayList<Formateur> getFormateurs() {
		return formateurs;
	}

	public void setFormateurs(ArrayList<Formateur> formateurs) {
		this.formateurs = formateurs;
	}

	public int nbFormateurs() {
		return formateurs.size();
	}

	public Formateur getFormateur(int indice) {
		/*if(indice == nbFormateurs())
			return Formateur.PERSONNE;
		else*/
			return formateurs.get(indice);
	}

	public int getIndiceFormateur(Formateur formateur){
		int i = 0;
		for (Formateur autreFormateur : getFormateurs()) {
			if(formateur.getName().contentEquals(autreFormateur.getName())){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public Formateur getFormateurByName(String name) {
		for (Formateur formateur : getFormateurs()) {
			if(formateur.getName() == name){
				return formateur;
			}
		}
		return null;
	}

	/***
	 * Accès aux salles
	 */
	public ArrayList<Salle> getSalles() {
		return salles;
	}

	public void setSalles(ArrayList<Salle> salles) {
		this.salles = salles;
	}

	public int nbSalles() {
		return salles.size();
	}


	public Salle getSalle(int indice) {
		return salles.get(indice);
	}

	
	public int getIndiceSalle(Salle salle){
		int i = 0;
		for (Salle autreSalle : getSalles()) {
			if(salle.getName().contentEquals(autreSalle.getName())){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public Salle getSalleByName(String name){
		for (Salle salle : getSalles()) {
			if(salle.getName() == name){
				return salle;
			}
		}
		return null;
	}
	
	/***
	 * Accès aux groupes
	 */
	
	
	public ArrayList<Groupe> getGroupes() {
		return groupes;
	}

	public void setGroupes(ArrayList<Groupe> groupes) {
		this.groupes = groupes;
	}

	public int nbGroupes() {
		return groupes.size();
	}

	public Groupe getGroupeByName(String name) {
		for (Groupe groupe : getGroupes()) {
			if(groupe.getName().equals(name)){
				return groupe;
			}
		}
		return null;
	}
	public int getNbGroupes() {
		return groupes.size();
	}

	public int getNbSousGroupes() {
		int nbSousGroupes = 0;
		for(Groupe groupe : groupes)
			nbSousGroupes = nbSousGroupes + groupe.nbSousGroupes();
		return nbSousGroupes;
	}
	
	/***
	 * Accès aux matières
	 */
	
	
	public ArrayList<Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(ArrayList<Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	
	public int nbDisciplines() {
		return disciplines.size();
	}
	
	public Discipline getDiscipline(int i) {
		return disciplines.get(i);
	}
	
	public Discipline getDisciplineByName(String name) {
		for (Discipline discipline : getDisciplines()) {
			if(discipline.getName().equals(name)){
				return discipline;
			}
		}
		return null;
	}
	

	/***
	 * Accès aux types de salles
	 */
	

	public ArrayList<TypeSalle> getTypesSalles() {
		return typesSalles;
	}

	public void setTypesSalles(ArrayList<TypeSalle> typesSalles) {
		this.typesSalles = typesSalles;
	}

	public int nbTypesSalles() {
		return typesSalles.size();
	}

	public TypeSalle getTypeSalle(int i) {
		return typesSalles.get(i);
	}

	public TypeSalle getTypeSalleByName(String name) {
		for (TypeSalle typeSalle : getTypesSalles()) {
			if(typeSalle.getName() == name){
				return typeSalle;
			}
		}
		return null;
	}

	public ArrayList<ProgrammeActivites> getProfilsActivites() {
		return profilsActivites;
	}

	public void setProfilsActivites(ArrayList<ProgrammeActivites> profilsActivites) {
		this.profilsActivites = profilsActivites;
	}

	/***
	 * 
	 * @return
	 */
	
	public int getNbSols() {
		return nbSols;
	}

	public ArrayList<SousGroupe> getSousGroupes() {
		ArrayList<SousGroupe> sousGroupes = new ArrayList<SousGroupe>();
		for(Groupe groupe : getGroupes())
			sousGroupes.addAll(groupe.getSousGroupes());
		return sousGroupes;
	}
		

	@Override
	public String toString() {
		return "Probleme [formateurs=" + formateurs + ", salles=" + salles +  ", groupes="
				+ groupes + ", disciplines=" + disciplines + ", typesSalles=" + typesSalles
				+ ", profilsActivites=" + profilsActivites + ", nbSols=" + nbSols + "]";
	}


}
