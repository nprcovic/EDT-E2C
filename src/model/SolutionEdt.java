package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class SolutionEdt implements Serializable {

	private static final long serialVersionUID = 1350092881346723535L;

	private int[][] heures, formateurs, salles;
	private int nbSousGroupes;
	private Sequence [][] sequences;
	private Probleme instance;
	private CaseEdTSousGroupe[][] solutionPourSousGroupes;
	private CaseEdTFormateur[][] solutionPourFormateurs;
	private CaseEdTSalle[][] solutionPourSalles;

	public SolutionEdt(Solveur solveur, int[][] heures, int[][] formateurs, int[][] salles) {
		this.sequences = solveur.getSequences();
		this.instance = solveur.getInstance();
		this.heures = heures;
		this.formateurs = formateurs;
		this.salles = salles;
		this.nbSousGroupes = instance.getNbSousGroupes();
		solutionPourSousGroupes = createSolutionPourSousGroupes();
		solutionPourFormateurs = createSolutionPourFormateurs();
		solutionPourSalles = createSolutionPourSalles();
	}

	/***
	 * 
	 * @return La solution du point de vue des sous-groupes
	 */
	private CaseEdTSousGroupe[][] createSolutionPourSousGroupes() {
		CaseEdTSousGroupe[][] solution = new CaseEdTSousGroupe[instance.getNbSousGroupes()][20];
		for (int i = 0; i < instance.getNbSousGroupes(); i++) {
			for (int j = 0; j < 20; j++) {
				Discipline discipline = sequences[i][j].getDiscipline();
				solution[i][heures[i][j]] = new CaseEdTSousGroupe(discipline, instance.getSalle(salles[i][j]),
						instance.getFormateur(formateurs[i][j]));
			}
		}
		return solution;
	}
	/***
	 * 
	 * @return La solution du point de vue des formateurs
	 */
	private CaseEdTFormateur[][] createSolutionPourFormateurs() {
		CaseEdTFormateur[][] solution = new CaseEdTFormateur[instance.nbFormateurs()][20];
		for (int i = 0; i < instance.getNbSousGroupes(); i++) {
			for (int j = 0; j < 20; j++) {
				Discipline discipline = sequences[i][j].getDiscipline();
				solution[formateurs[i][j]][heures[i][j]] = new CaseEdTFormateur(discipline, instance.getSalle(salles[i][j]),
						instance.getSousGroupes().get(i));
			}
		}
		return solution;
	}

	/***
	 * 
	 * @return La solution du point de vue des salles
	 */
	private CaseEdTSalle[][] createSolutionPourSalles() {
		CaseEdTSalle[][] solution = new CaseEdTSalle[instance.nbSalles()][20];
		for (int i = 0; i < instance.getNbSousGroupes(); i++) {
			for (int j = 0; j < 20; j++) {
				Discipline discipline = sequences[i][j].getDiscipline();
				solution[salles[i][j]][heures[i][j]] = new CaseEdTSalle(discipline,
						instance.getSousGroupes().get(i),
						instance.getFormateur(formateurs[i][j]));
			}
		}
		return solution;
	}

	public int[][] getHeures() {
		return heures;
	}

	public int[][] getFormateurs() {
		return formateurs;
	}

	public int[][] getSalles() {
		return salles;
	}

	public int getNbSousGroupes() {
		return nbSousGroupes;
	}

	public void setHeures(int[][] heures) {
		this.heures = heures;
	}

	public void setFormateurs(int[][] formateurs) {
		this.formateurs = formateurs;
	}

	public void setSalles(int[][] salles) {
		this.salles = salles;
	}

	public void setNbGroupes(int nbGroupes) {
		this.nbSousGroupes = nbGroupes;
	}

	/***
	 * accès Sequence
	 * @return
	 */
	public Sequence[][] getSequences() {
		return sequences;
	}

	public void setSequences(Sequence[][] sequences) {
		this.sequences = sequences;
	}

	public Sequence getSequence(int i, int j) {
		return sequences[i][j];
	}

	public void printPourSousGroupes() {
		for (int i = 0; i < nbSousGroupes; i++) {
			System.out.println("SousGroupe : " + (i + 1));
			for (int j = 0; j < 20; j++) {
				System.out.println(Creneau.getName(j) + " : " + solutionPourSousGroupes[i][j]);
			}
		}
	}

	public void printPourFormateurs() {
		for (int i = 0; i < instance.nbFormateurs(); i++) {
			System.out.println("Formateur : " + instance.getFormateur(i));
			for (int j = 0; j < 20; j++) {
				System.out.println(Creneau.getName(j) + " : " + solutionPourFormateurs[i][j]);
			}
		}
	}
	public void printPourSalles() {
		for (int i = 0; i < instance.nbSalles(); i++) {
			System.out.println("Salle : " + instance.getSalle(i));
			for (int j = 0; j < 20; j++) {
				System.out.println(Creneau.getName(j) + " : " + solutionPourSalles[i][j]);
			}
		}
	}
	/***
	 * Sauve la solution sous le nom 'name'
	 * @param name
	 */
	public void save(File probFile) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(probFile));
			oos.writeObject(this);
			System.out.println("Solution sauvegardée");
		} catch (IOException e) {
			e.printStackTrace();
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
	 * Charge la solution dont le nom est 'name'
	 * @param name
	 * @return
	 */
	static public SolutionEdt load(String name) {
		File fichier = new File("src/Solutions_Serialisees/" + name);
		return load(fichier);
	}
	
	/***
	 * Charge la solution stocké dans le fichier
	 * @param fichier
	 * @return
	 */
	static public SolutionEdt load(File fichier) {
			ObjectInputStream ois = null;
			SolutionEdt solution = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(fichier));
				solution = (SolutionEdt)ois.readObject();
				System.out.println("Solution chargée");
				ois.close();
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			return solution;
	}

	public Probleme getInstance() {
		return instance;
	}

	public void setInstance(Probleme instance) {
		this.instance = instance;
	}

	public CaseEdTSousGroupe[][] getSolutionPourSousGroupes() {
		return solutionPourSousGroupes;
	}

	public CaseEdTSousGroupe getSolutionPourSousGroupes(int i, int j) {
		return solutionPourSousGroupes[i][j];
	}

	public void setSolutionPourSousGroupes(CaseEdTSousGroupe[][] solutionPourSousGroupes) {
		this.solutionPourSousGroupes = solutionPourSousGroupes;
	}

	public CaseEdTFormateur[][] getSolutionPourFormateurs() {
		return solutionPourFormateurs;
	}

	public CaseEdTFormateur getSolutionPourFormateurs(int i, int j) {
		return solutionPourFormateurs[i][j];
	}

	public void setSolutionPourFormateurs(CaseEdTFormateur[][] solutionPourFormateurs) {
		this.solutionPourFormateurs = solutionPourFormateurs;
	}

	public CaseEdTSalle[][] getSolutionPourSalles() {
		return solutionPourSalles;
	}

	public CaseEdTSalle getSolutionPourSalles(int i, int j) {
		return solutionPourSalles[i][j];
	}

	public void setSolutionPourSalles(CaseEdTSalle[][] solutionPourSalles) {
		this.solutionPourSalles = solutionPourSalles;
	}

	public void setNbSousGroupes(int nbSousGroupes) {
		this.nbSousGroupes = nbSousGroupes;
	}
	

}
