package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Preaffectation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4496307216818896292L;

	private Probleme instance;
	private Sequence[][] sequences;
	private ArrayList<SousGroupe> sousGroupes;
	private int nbSousGroupes;
	private int nbSequences;
	private ArrayList<ArrayList<ArrayList<Integer>>> domainesHeures;
	private ArrayList<ArrayList<ArrayList<Integer>>> domainesFormateurs;
	private ArrayList<ArrayList<ArrayList<Integer>>> domainesSalles;

	public Preaffectation(Solveur solveur) {
		super();
		this.instance = solveur.getInstance();
		sequences = solveur.getSequences();
		sousGroupes = instance.getSousGroupes();
		nbSousGroupes = sousGroupes.size();
		nbSequences = 20;
		domainesHeures = createDomainesHeures();
		// System.out.println(domainesHeures);
		domainesFormateurs = createDomainesFormateurs();
		domainesSalles = createDomainesSalles();
	}

	/***
	 * 
	 * @return les domaines des heures
	 */
	private ArrayList<ArrayList<ArrayList<Integer>>> createDomainesHeures() {
		ArrayList<ArrayList<ArrayList<Integer>>> domainesHeures = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < nbSousGroupes; i++) {
			domainesHeures.add(new ArrayList<ArrayList<Integer>>());
			for (int j = 0; j < nbSequences; j++) {
				domainesHeures.get(i).add(indicesCreneaux(sequences[i][j].getDiscipline()));
				// System.out.println(domainesHeures.get(i).get(j));
			}
		}
		System.out.println(domainesHeures);
		return domainesHeures;
	}

	/***
	 * 
	 * @param discipline
	 * @return les indices des créneaux pouvant accueillir cette discipline
	 */
	private ArrayList<Integer> indicesCreneaux(Discipline discipline) {
		ArrayList<Integer> indicesCreneaux = new ArrayList<Integer>();
		// System.out.println(discipline);
		for (int indiceCreneau = 0; indiceCreneau < nbSequences; indiceCreneau++) {
			if (discipline.hasCreneau(indiceCreneau))
				indicesCreneaux.add(indiceCreneau);
		}
		return indicesCreneaux;
	}

	/***
	 * 
	 * @return les domaines des formateurs
	 */
	private ArrayList<ArrayList<ArrayList<Integer>>> createDomainesFormateurs() {
		ArrayList<ArrayList<ArrayList<Integer>>> domainesFormateurs = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < nbSousGroupes; i++) {
			domainesFormateurs.add(new ArrayList<ArrayList<Integer>>());
			for (int j = 0; j < nbSequences; j++) {
				domainesFormateurs.get(i)
						.add(indicesFormateurs(sousGroupes.get(i), sequences[i][j].getDiscipline()));
				// System.out.println(heures[i][j]);
			}
		}
		System.out.println(domainesFormateurs);
		return domainesFormateurs;
	}

	/***
	 * 
	 * @param groupe
	 * @param discipline
	 * @return les indices des formateurs de ce groupe pouvant enseigner cette
	 *         discipline
	 */
	private ArrayList<Integer> indicesFormateurs(SousGroupe sousGroupe, Discipline discipline) {
		ArrayList<Integer> indicesFormateurs = new ArrayList<Integer>();
		int indiceFormateur = 0;

		if (discipline.getName().equals("Temps référent"))
			for (Formateur formateur : instance.getFormateurs()) {
				if(sousGroupe.getReferent() == formateur) {
					indicesFormateurs.add(indiceFormateur);
					return indicesFormateurs;					
				}
				indiceFormateur++;
			}

		for (Formateur formateur : instance.getFormateurs()) {
			if (formateur.getGroupes().contains(sousGroupe.getGroupe())
					&& formateur.getDisciplines().contains(discipline)) {
				indicesFormateurs.add(indiceFormateur);
			}
			indiceFormateur++;
		}
		if (!discipline.getNecessiteFormateur()) // Pas besoin de formateur pour la discipline
			indicesFormateurs.add(instance.getIndiceFormateur(Formateur.PERSONNE)); // indice de PERSONNE
		// System.out.println(groupe + "," + discipline + ":" + indicesFormateurs);
		return indicesFormateurs;
	}

	/***
	 * 
	 * @return les domaines des salles
	 */
	private ArrayList<ArrayList<ArrayList<Integer>>> createDomainesSalles() {
		ArrayList<ArrayList<ArrayList<Integer>>> domainesSalles = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (int i = 0; i < nbSousGroupes; i++) {
			domainesSalles.add(new ArrayList<ArrayList<Integer>>());
			for (int j = 0; j < nbSequences; j++) {
				domainesSalles.get(i)
						.add(indicesSalles(sousGroupes.get(i).getGroupe(), sequences[i][j].getDiscipline()));
				// System.out.println(heures[i][j]);
			}
		}
		System.out.println(domainesSalles);
		return domainesSalles;
	}

	/***
	 * 
	 * @param groupe
	 * @param discipline
	 * @return les indices des salles de ce groupe pouvant accueillir cette discipline
	 */
	private ArrayList<Integer> indicesSalles(Groupe groupe, Discipline discipline) {
		ArrayList<Integer> indicesSalles = new ArrayList<Integer>();
		int indiceSalle = 0;
		//System.out.println("\n" + groupe.toString() + ", " + discipline);
		for (Salle salle : instance.getSalles()) {
			//System.out.println(salle.toString() + salle.getGroupes() + salle.getType());
			if (salle.getGroupes().contains(groupe) && discipline.getTypesSalle().contains(salle.getType()))
				indicesSalles.add(indiceSalle);
			indiceSalle++;
		}
		 //System.out.println(groupe + "," + discipline + ":" + indicesSalles);
		return indicesSalles;
	}

	public ArrayList<ArrayList<ArrayList<Integer>>> getDomainesHeures() {
		return domainesHeures;
	}

	public ArrayList<ArrayList<ArrayList<Integer>>> getDomainesFormateurs() {
		return domainesFormateurs;
	}

	public ArrayList<ArrayList<ArrayList<Integer>>> getDomainesSalles() {
		return domainesSalles;
	}

	@Override
	public String toString() {
		return "Preaffectation [domainesHeures=" + domainesHeures + ", domainesFormateurs=" + domainesFormateurs
				+ ", domainesSalles=" + domainesSalles + "]";
	}

}
