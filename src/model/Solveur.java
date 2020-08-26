package model;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.loop.lns.INeighborFactory;
import org.chocosolver.solver.search.loop.move.MoveLNS;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
	Contraintes à implémenter:
		- Certains cours doivent être donnés par bloc de 2 (Temps referent)

	- Voir pour les domaines de définition de la structure formateurs[][]
	- Transformer le champ groupe en un ensemble pour les structure salle formateur


 */

public class Solveur implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3753670074032228974L;

	// données du problème à résoudre
	private Probleme instance;
	private int nbSequences, nbSousGroupes;

	// restructuration de certaines données
	private Sequence[][] sequences; // séquences indexées par (sous-groupe, activité)
	private ArrayList<SousGroupe> sousGroupes; // tous les sous-groupes de tous les groupes

	private int nbSolutionsDemandees;
	private SolutionEdt solutionDeReference; // solution à approcher
	private SolutionEdt solutionEDT; // solution trouvée
	private IntVar[] finalDataCat;
	private int[] finalDataCatSol;
	private CaseEdTSousGroupe[][] resultat, modele;

	private Model model;
	// Variables Choco Solver
	private IntVar[][] heures, formateurs, salles;
	private IntVar deviation_totale;
	private IntVar[][] deviation;

	public Solveur() {
		this(new Probleme());
	}

	public Solveur(Probleme instance) {
		this(instance, null, null);
		//chargeSolutionDeReference("solution3");
	}

	public Solveur(Probleme instance, SolutionEdt solutionDeReference, CaseDomainesSousGroupe[][] casesDomaines) {
		this.instance = instance;
		this.model = new Model("EDT");
		instance.nbFormateurs();
		instance.nbSalles();
		this.nbSequences = 20;
		this.nbSousGroupes = instance.getNbSousGroupes();
		this.sequences = createSequences();
		this.sousGroupes = instance.getSousGroupes();
		this.nbSolutionsDemandees = instance.getNbSols();
		this.deviation = new IntVar[3][nbSousGroupes * nbSequences];

		this.deviation_totale = model.intVar("deviation_totale", 0, IntVar.MAX_INT_BOUND);
		for (int i = 0; i < 3; i++) {
			this.deviation[i] = IntStream.range(0, nbSousGroupes * nbSequences)
					.mapToObj(j -> model.intVar("deviation #" + j, 0, 1, false)).toArray(IntVar[]::new);
		}

		// On récupère les domaines de définitions des salles et formateurs
		// des différents groupes.
		// ArrayList<int[]> iDSallesGroupes = getIdSallesParGroupes();
		// ArrayList<int[]> iDFormateursGroupes = getIdFormateursParGroupes();

		// On instancie les variables avec les domaines de définitions qui leur
		// correspond
		if(!instance.getFormateurs().contains(Formateur.PERSONNE))
			instance.getFormateurs().add(Formateur.PERSONNE);

		Preaffectation domaines = new Preaffectation(this);
		System.out.println(domaines);
		this.salles = new IntVar[nbSousGroupes][nbSequences];
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				this.salles[i][j] = model.intVar("salle(" + i + ", " + j + ")",
						domaines.getDomainesSalles().get(i).get(j).stream().mapToInt(k -> k).toArray());
				 System.out.println(salles[i][j]);
			}
		}

		this.formateurs = new IntVar[nbSousGroupes][nbSequences];
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				this.formateurs[i][j] = model.intVar("formateurs(" + i + ", " + j + ")",
						domaines.getDomainesFormateurs().get(i).get(j).stream().mapToInt(k -> k).toArray());
				 System.out.println(formateurs[i][j]);
			}
		}

		this.heures = new IntVar[nbSousGroupes][nbSequences];
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				this.heures[i][j] = model.intVar("heure(" + i + ", " + j + ")",
						domaines.getDomainesHeures().get(i).get(j).stream().mapToInt(k -> k).toArray());
				 System.out.println(heures[i][j]);
			}
		}
		this.solutionDeReference = solutionDeReference;
		definirContraintes();

		if (casesDomaines != null && casesDomaines[0][0].isAssigned())
			definirContraintesAffectations(casesDomaines);//definirContraintesPreAffectations(casesDomaines);

	}

	/***
	 * 
	 * @return le tableau des séquences indicé par numéro de sous-groupe et séquence
	 */
	private Sequence[][] createSequences() {
		int sousGroupesIndex, sequenceIndex, occurrenceIndex;
		sousGroupesIndex = 0;
		Sequence[][] sequences = new Sequence[nbSousGroupes][nbSequences];
		for (SousGroupe sousGroupe : instance.getSousGroupes()) {
			sequenceIndex = 0;
			for (Activite activite : sousGroupe.getProgrammeActivites().getProgramme())
				for (occurrenceIndex = 0; occurrenceIndex < activite.getOccurences(); occurrenceIndex++) {
					sequences[sousGroupesIndex][sequenceIndex] = new Sequence(activite.getDiscipline(), occurrenceIndex);
					// System.out.println(sequences[sousGroupesIndex][sequenceIndex]);
					sequenceIndex++;
				}
			Activite tempsLibre = new Activite(Discipline.TEMPS_LIBRE, nbSequences - sequenceIndex);
			for (occurrenceIndex = 0; occurrenceIndex < tempsLibre.getOccurences(); occurrenceIndex++) {
				sequences[sousGroupesIndex][sequenceIndex] = new Sequence(tempsLibre.getDiscipline(), occurrenceIndex);
				// System.out.println(sequences[sousGroupesIndex][sequenceIndex]);
				sequenceIndex++;
			}
			sousGroupesIndex++;
		}
		return sequences;
	}

	private void chargeSolutionDeReference(String path) {
		File fichier = new File(path);
		chargeSolutionDeReference(fichier);
	}

	private void chargeSolutionDeReference(File fichier) {
		ObjectInputStream ois = null;
		//this.solutionDeReference = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichier));
			this.solutionDeReference = (SolutionEdt) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void contrainteCalculDeviation() {
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				deviation[0][i * nbSequences + j].eq(heures[i][j].sub(this.solutionDeReference.getHeures()[i][j]).abs())
						.post();
			}
		}
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				deviation[1][i * nbSequences + j]
						.eq(formateurs[i][j].sub(this.solutionDeReference.getFormateurs()[i][j]).abs()).post();
			}
		}
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				deviation[2][i * nbSequences + j].eq(salles[i][j].sub(this.solutionDeReference.getSalles()[i][j]).abs())
						.post();
			}
		}

		int[] coeff = new int[3 * nbSousGroupes * nbSequences];
		for (int i = 0; i < 3 * nbSousGroupes * nbSequences; i++) {
			coeff[i] = 1;
		}

		model.scalar(ArrayUtils.append(deviation[0], deviation[1], deviation[2]), coeff, "=", deviation_totale).post();
	}

	public CaseEdTSousGroupe[][] getResultat() {
		return resultat;
	}

	public static CaseEdTSousGroupe[][] fillModele(SolutionEdt solutionEdt, Probleme instance) {
		CaseEdTSousGroupe[][] modele = new CaseEdTSousGroupe[solutionEdt.getNbSousGroupes()][20];
		for (int indiceSousGroupe = 0; indiceSousGroupe < solutionEdt.getNbSousGroupes(); indiceSousGroupe++) {
			for (int indiceSequence = 0; indiceSequence < 20; indiceSequence++) {
				Discipline discipline = solutionEdt.getSequence(indiceSousGroupe, indiceSequence).getDiscipline();
				modele[indiceSousGroupe][solutionEdt
						.getHeures()[indiceSousGroupe][indiceSequence]] = new CaseEdTSousGroupe(discipline,
								instance.getSalle(solutionEdt.getSalles()[indiceSousGroupe][indiceSequence]),
								instance.getFormateur(solutionEdt.getFormateurs()[indiceSousGroupe][indiceSequence]));
			}
		}
		return modele;
	}

	/***
	 * Deux matières ne peuvent avoir lieu dans la même salle non partageable au
	 * même moment
	 */
	private void contrainteActiviteTempsSalle() {
		for (int i = 0; i < nbSequences * nbSousGroupes; i++) {
			for (int j = i + 1; j < nbSequences * nbSousGroupes; j++) {

				if (i != j && (!sequences[i / nbSequences][i % nbSequences].getDiscipline().getTypesSalle().get(0)
						.getPartageable()
						&& !sequences[j / nbSequences][j % nbSequences].getDiscipline().getTypesSalle().get(0)
								.getPartageable())) {
					model.addClauses(LogOp.or(
							model.arithm(heures[i / nbSequences][i % nbSequences], "!=",
									heures[j / nbSequences][j % nbSequences]).reify(),
							(model.arithm(salles[i / nbSequences][i % nbSequences], "!=",
									salles[j / nbSequences][j % nbSequences]).reify())));
					// System.out.println(i + ":" + j);
				}
			}
		}
	}

	/***
	 * Deux Activites de meme groupe ne peuvent pas se passer au meme moment.
	 */
	private void contrainteActiviteMemeGroupeMemeTemps() {
		for (int i = 0; i < nbSousGroupes; i++) {
			model.allDifferent(heures[i]).post();
		}
	}

	/*
	 * private ArrayList<int[]> getIdSallesParGroupes() { ArrayList<int[]> resultat
	 * = new ArrayList<>(); for (int i = 0; i < nbSousGroupes; i++) {
	 * ArrayList<Integer> ligne = new ArrayList<>(); for (int j = 0; j < nbSalles;
	 * j++) { Groupe groupe = this.instance.getGroupes().get(i); if
	 * (this.instance.getSalle(j).getGroupes().contains(groupe) ||
	 * this.instance.getSalle(j).getGroupes().contains(Groupe.INDIFFERENT)) {
	 * ligne.add(this.instance.getSalle(j).getId()); } }
	 * resultat.add(ligne.stream().mapToInt(k -> k).toArray()); } int j = 1; for
	 * (int[] tab : resultat) { System.out.println("SousGroupe" + j); for (int i =
	 * 0; i < tab.length; i++) { System.out.printf("%d; ", tab[i]); }
	 * System.out.println(); j++; System.out.println(); } return resultat; }
	 */

	// Deux Activités différentes ne peuvent pas avoir un meme prof en meme temps.
	private void contrainteProfMemeTemps() {
		for (int i = 0; i < nbSequences * nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences * nbSousGroupes; j++) {
				if (i != j && sequences[i / nbSequences][i % nbSequences].getDiscipline().getNecessiteFormateur() && sequences[j / nbSequences][j % nbSequences].getDiscipline().getNecessiteFormateur()) {
					model.addClauses(LogOp.or(
							model.arithm(heures[i / nbSequences][i % nbSequences], "!=",
									heures[j / nbSequences][j % nbSequences]).reify(),
							model.arithm(formateurs[i / nbSequences][i % nbSequences], "!=",
									formateurs[j / nbSequences][j % nbSequences]).reify()));/*,
							model.arithm(formateurs[i / nbSequences][i % nbSequences], "=", nbFormateurs).reify(),
							model.arithm(formateurs[j / nbSequences][j % nbSequences], "=", nbFormateurs).reify()));*/
				}
			}
		}
	}

	/***
	 * 
	 */
	private void contrainteDisponibilitesFormateurs() {
		for(int indiceSousGroupe = 0 ; indiceSousGroupe < nbSousGroupes; indiceSousGroupe ++) 
			for(int indiceActivite = 0; indiceActivite < nbSequences; indiceActivite++)
				for(int indiceSequence = 0; indiceSequence < nbSequences ; indiceSequence++)
					for(int indiceFormateur = 0; indiceFormateur < instance.getFormateurs().size() ; indiceFormateur++) {
						Formateur formateur = instance.getFormateur(indiceFormateur);
						if(formateur != Formateur.PERSONNE
								&& formateur.getGroupes().contains(sousGroupes.get(indiceSousGroupe).getGroupe())
								&& !formateur.getDisponibilite(indiceSequence)) {
							model.addClauses(LogOp.or(
									model.arithm(heures[indiceSousGroupe][indiceActivite], "!=", indiceSequence).reify(),
									model.arithm(formateurs[indiceSousGroupe][indiceActivite], "!=", indiceFormateur).reify()));
							System.out.println(heures[indiceSousGroupe][indiceActivite] + "!=" +  indiceSequence + "  " + formateurs[indiceSousGroupe][indiceActivite] + "!=" + indiceFormateur);
						}
					}
	}
	
	/***
	 * 
	 */
	private void contrainteSymetrieSequence() {
		for (int i = 0; i < nbSousGroupes; i++)
			for (int j = 0; j < nbSequences - 1; j++)
				if (sequences[i][j].getDiscipline() == sequences[i][j + 1].getDiscipline()) {
					model.arithm(heures[i][j], "<", heures[i][j + 1]).post();
					// System.out.println(i + "," + j + "<" + (j+1));
				}
	}
	// Contrainte symetrie classe
	/*
	 * private void contrainteSymetrieClasses() { // Pour chaque activité (créneau)
	 * for (int i = 0; i < nbSequences; i++) { // Pour tout les groupes deux à deux
	 * for (int j = 0; j < nbSousGroupes - 1; j++) { for (int k = j + 1; k <
	 * nbSousGroupes; k++) { // Si les groupe sont de même nature, on définit un
	 * ordre sur les salles if
	 * (activites.get(j).get(i).getDiscipline().getTypesSalles()
	 * .equals(activites.get(k).get(i).getDiscipline().getTypesSalles())) {
	 * model.arithm(salles[j][i], "<=", salles[k][i]).post(); } } } } }
	 */

	/*
	 * private void contrainteSymetrieFormateurs() { // Pour chaque actvité
	 * (créneau) for (int i = 0; i < nbSequences; i++) { // Pour tout les groupes
	 * deux à deux for (int j = 0; j < nbSousGroupes - 1; j++) { for (int k = j + 1;
	 * k < nbSousGroupes; k++) { // Si les groupe sont de même nature, on définit un
	 * ordre sur les formateurs if
	 * (activites.get(j).get(i).getDiscipline().getTypesSalles()
	 * .equals(activites.get(k).get(i).getDiscipline().getTypesSalles())) {
	 * model.arithm(formateurs[j][i], "<=", formateurs[k][i]).post(); } } } } }
	 */

	/*
	 * private void contrainteSymetrieSallePourUnGroupe() { for (int i = 0; i <
	 * nbSousGroupes; i++) { for (int j = 0; j < nbSequences; j++) { for (int k = j
	 * + 1; k < nbSequences; k++) { if
	 * (activites.get(i).get(j).getDiscipline().getTypesSalles().equals(activites.get(i
	 * ).get(k))) { model.arithm(salles[i][j], "<", salles[i][k]).post(); } } } } }
	 */

	/*
	 * private void contrainteSymetrieProfPourUnGroupe() { for (int i = 0; i <
	 * nbSousGroupes; i++) { for (int j = 0; j < nbSequences; j++) { for (int k = j
	 * + 1; k < nbSequences; k++) { if
	 * (activites.get(i).get(j).getDiscipline().getTypesSalles().equals(activites.get(i
	 * ).get(k))) { model.arithm(formateurs[i][j], "<", formateurs[i][k]).post();
	 * } } } } }
	 */

	private boolean hasCommonDiscipline(ArrayList<Discipline> arr2, ArrayList<String> arr1) {
		for (String discipline : arr1) {
			if (arr2.contains(discipline))
				return true;
		}
		return false;
	}


	// Fonction qui pose toute les contraintes
	public void definirContraintes() {

		// Deux activités ne peuvent avoir lieu dans la même salle au même moment
		this.contrainteActiviteTempsSalle();

		// Deux activités de meme groupe ne peuvent pas se passer en meme temps.
		this.contrainteActiviteMemeGroupeMemeTemps();

		// Deux Activités ne peuvent pas avoir un meme prof en meme temps.
		this.contrainteProfMemeTemps();

		// Contrainte qui s'assure que les cours ayant lieux plusieurs fois se déroule
		// selon la même séquence
		// pour briser les symétries
		this.contrainteSymetrieSequence();

		this.contrainteDisponibilitesFormateurs();
//		this.contrainteSymetrieClasses();
//
//		this.contrainteSymetrieFormateurs();

//		this.contrainteSalleGroupe();
//
//		this.contrainteFormateurGroupe();

		// this.contrainteCalculDeviation();

	}

	/***
	 * 
	 * @param casesDomaines
	 */
	private void definirContraintesAffectations(CaseDomainesSousGroupe[][] casesDomaines) {
		for (int indexSousGroupes = 0; indexSousGroupes < nbSousGroupes; indexSousGroupes++) {
			int indexSequences = 0;
			for (int indexActivites = 0; indexActivites < 20; indexActivites++) {
				if(sequences[indexSousGroupes][indexActivites].getNumero() == 0)
					indexSequences = 0;
				//System.out.println("#" + sequences[indexSousGroupes][indexActivites].getDiscipline().getName());
				while(!casesDomaines[indexSousGroupes][indexSequences].getDisciplineChoisie().getName().equals(sequences[indexSousGroupes][indexActivites].getDiscipline().getName())) {
					indexSequences++;
					//System.out.println(">" + casesDomaines[indexSousGroupes][indexSequences].getDisciplineChoisie().getName());
				}
				//System.out.println("h(" + indexSousGroupes + ", " + indexActivites + ")=" + indexSequences);
				model.arithm(heures[indexSousGroupes][indexActivites], "=", indexSequences).post();
				//System.out.println("e(" + indexSousGroupes + ", " + indexActivites + ")=" + instance.getIndiceFormateur(casesDomaines[indexSousGroupes][indexSequences].getFormateurChoisi()));
				if(!casesDomaines[indexSousGroupes][indexSequences].getFormateurChoisi().getName().contentEquals(Formateur.PERSONNE.getName()))
					model.arithm(formateurs[indexSousGroupes][indexActivites], "=", instance.getIndiceFormateur(casesDomaines[indexSousGroupes][indexSequences].getFormateurChoisi())).post();
				//System.out.println("s(" + indexSousGroupes + ", " + indexActivites + ")=" + instance.getIndiceSalle(casesDomaines[indexSousGroupes][indexSequences].getSalleChoisie()));
				model.arithm(salles[indexSousGroupes][indexActivites], "=", instance.getIndiceSalle(casesDomaines[indexSousGroupes][indexSequences].getSalleChoisie())).post();
				indexSequences++;
			}
		}
	}
	/***
	 * Ajoute les contraintes de pré-affectations (pas utilisé)
	 * 
	 * @param casesDomaines
	 */
	private void definirContraintesPreAffectations(CaseDomainesSousGroupe[][] casesDomaines) {
		for (int indexSousGroupes = 0; indexSousGroupes < nbSousGroupes; indexSousGroupes++) {
			for (int indexSequences = 0; indexSequences < 20; indexSequences++) {
				Formateur formateurChoisi = casesDomaines[indexSousGroupes][indexSequences].getFormateurChoisi();
				System.out.println(formateurChoisi);
				if (formateurChoisi != null)
					contraintesPreAffectationFormateur(indexSousGroupes, indexSequences,
							instance.getIndiceFormateur(formateurChoisi));

				Discipline disciplineChoisie = casesDomaines[indexSousGroupes][indexSequences].getDisciplineChoisie();
				System.out.println(disciplineChoisie);
				if (disciplineChoisie != null)
					contraintesPreAffectationDiscipline(indexSousGroupes, indexSequences, disciplineChoisie);

				Salle salleChoisie = casesDomaines[indexSousGroupes][indexSequences].getSalleChoisie();
				System.out.println(salleChoisie);
				if (salleChoisie != null)
					contraintesPreAffectationSalle(indexSousGroupes, indexSequences,
							instance.getIndiceSalle(salleChoisie));
			}
		}
	}

	/***
	 * 
	 * @param indexSousGroupes
	 * @param indexSequences
	 * @param formateurChoisi
	 */
	private void contraintesPreAffectationFormateur(int indexSousGroupes, int indexSequences, int formateurChoisi) {
		LogOp[] clauseFormateurAffecte = new LogOp[20];
		for (int i = 0; i < 20; i++) {
			clauseFormateurAffecte[i] = LogOp.and(
					model.arithm(formateurs[indexSousGroupes][i], "=", formateurChoisi).reify(),
					model.arithm(heures[indexSousGroupes][i], "=", indexSequences).reify());
			System.out.print(" - e(" + indexSousGroupes + ", " + i + ") = " + formateurChoisi + "/" + indexSequences);
		}
		System.out.println();
		model.addClauses(LogOp.or(clauseFormateurAffecte));
	}

	/***
	 * 
	 * @param indexSousGroupes
	 * @param indexSequences
	 * @param indiceSalle
	 */
	private void contraintesPreAffectationSalle(int indexSousGroupes, int indexSequences, int indiceSalle) {
		LogOp[] clauseSalleAffectee = new LogOp[20];
		for (int i = 0; i < 20; i++) {
			clauseSalleAffectee[i] = LogOp.and(model.arithm(salles[indexSousGroupes][i], "=", indiceSalle).reify(),
					model.arithm(heures[indexSousGroupes][i], "=", indexSequences).reify());
			System.out.print(" - s(" + indexSousGroupes + ", " + i + ") = " + indiceSalle + "/" + indexSequences);
		}
		System.out.println();
		model.addClauses(LogOp.or(clauseSalleAffectee));
	}

	/***
	 * 
	 * @param indexSousGroupes
	 * @param indexSequences
	 * @param disciplineChoisie
	 */
	private void contraintesPreAffectationDiscipline(int indexSousGroupes, int indexSequences, Discipline disciplineChoisie) {
		LogOp[] clauseDisciplineAffectee;
		Activite activite = sousGroupes.get(indexSousGroupes).getProgrammeActivites()
				.getActivite(disciplineChoisie);
		int indiceClause = 0;
		System.out.println(indexSousGroupes + ":" + indexSequences + ":" + disciplineChoisie + ":" + activite);
		clauseDisciplineAffectee = new LogOp[activite.getOccurences()];
		for (int i = 0; i < 20; i++) {
			if (sequences[indexSousGroupes][i].getDiscipline() == disciplineChoisie) {
				clauseDisciplineAffectee[indiceClause] = LogOp
						.or(model.arithm(heures[indexSousGroupes][i], "=", indexSequences).reify());
				indiceClause++;
				System.out.print(" - m(" + indexSousGroupes + ", " + i + ") = " + indexSequences);
			}
		}
		// System.out.println(indexSousGroupes + " " + disciplineChoisie + " " +
		// activite.getOccurences() + " " + );
		System.out.println();
		model.addClauses(LogOp.or(clauseDisciplineAffectee));
	}

	public void solveWithModel() {
		this.nbSolutionsDemandees = 1;
		Solver solver = model.getSolver();
		solver.showShortStatistics();
		solver.findOptimalSolution(deviation_totale, false);
		resultat = new CaseEdTSousGroupe[nbSousGroupes][nbSequences];
		fillResultat(0);
	}

	public void LNS() {
		this.setReferenceSolution();
		Solution solution = new Solution(model, finalDataCat);
		IntStream.range(0, nbSousGroupes * nbSequences * 3)
				.forEach(i -> solution.setIntVal(finalDataCat[i], finalDataCatSol[i]));
		MoveLNS lns = new MoveLNS(model.getSolver().getMove(), INeighborFactory.random(finalDataCat),
				new FailCounter(model, 100));
		lns.loadFromSolution(solution, model.getSolver());
		model.getSolver().setMove(lns);
		this.solve();
	}

	private void setReferenceSolution() {
		IntVar[] dataCat = null;
		int[] dataCatSol = null;	
		for (int i = 0; i < nbSousGroupes; i++) {
			dataCat = ArrayUtils.append(dataCat, heures[i]);
			dataCatSol = ArrayUtils.append(dataCatSol, solutionDeReference.getHeures()[i]);
			System.out.println(i + "," + nbSousGroupes);		
		}
		for (int i = 0; i < nbSousGroupes; i++) {
			dataCat = ArrayUtils.append(dataCat, formateurs[i]);
			dataCatSol = ArrayUtils.append(dataCatSol, solutionDeReference.getFormateurs()[i]);
		}
		for (int i = 0; i < nbSousGroupes; i++) {
			dataCat = ArrayUtils.append(dataCat, salles[i]);
			dataCatSol = ArrayUtils.append(dataCatSol, solutionDeReference.getSalles()[i]);
		}
		finalDataCat = dataCat;
		finalDataCatSol = dataCatSol;
	}

	public boolean LDS() {
		if(getSolutionDeReference() != null) {
			this.setReferenceSolution();
			Map<IntVar, Integer> map = IntStream.range(0, nbSousGroupes * nbSequences * 3).boxed()
				.collect(Collectors.toMap(i -> finalDataCat[i], i -> finalDataCatSol[i]));
			model.getSolver().setSearch(Search.intVarSearch(new InputOrder<>(model), var -> {
			// value selection, choose value from solution if possible
				if (var.contains(map.get(var))) {
					return map.get(var);
				}
				return var.getLB(); // otherwise, select the current lower bound
			}, finalDataCat));
			// this.printModele();
			model.getSolver().setLDS(300); // discrepancy is set to 300
		}
		boolean solutionExiste = this.solve();
		//this.printDifferenceAvecModele();
		return solutionExiste;
	}

	public boolean solve() {
		Solver solver = model.getSolver();
		solver.showSolutions();
		// solver.findAllSolutions();
		resultat = new CaseEdTSousGroupe[nbSousGroupes * nbSolutionsDemandees][nbSequences];
		int k = 0;
		while (k < nbSolutionsDemandees) {
			 System.out.println(k);

			int offset;
			if (solver.solve()) {
				offset = k * nbSousGroupes;

				int[][] heuresSol = new int[nbSousGroupes][nbSequences];
				int[][] formateursSol = new int[nbSousGroupes][nbSequences];
				int[][] sallesSol = new int[nbSousGroupes][nbSequences];
				fillResultat(offset);
				for (int i = 0; i < nbSequences * nbSousGroupes; i++) {
					heuresSol[i / nbSequences][i % nbSequences] = heures[i / nbSequences][i % nbSequences].getValue();
					formateursSol[i / nbSequences][i % nbSequences] = formateurs[i / nbSequences][i % nbSequences]
							.getValue();
					sallesSol[i / nbSequences][i % nbSequences] = salles[i / nbSequences][i % nbSequences].getValue();
				}

				solutionEDT = new SolutionEdt(this, heuresSol, formateursSol, sallesSol);
				solutionEDT.printPourSousGroupes();
				solutionEDT.printPourFormateurs();
				solutionEDT.printPourSalles();
				//saveSolution(solutionEDT, preferences.getDirectory().getAbsolutePath() + "/derniere_solution");

				k++;

			} else {
				nbSolutionsDemandees = k;
				return false;
			}
		}
		return true;
	}

	private void fillResultat(int offset) {
		for (int i = 0; i < nbSousGroupes; i++)
			for (int j = 0; j < nbSequences; j++) {
				Discipline discipline = sequences[i][j].getDiscipline();
				resultat[i + offset][heures[i][j].getValue()] = new CaseEdTSousGroupe(discipline,
						instance.getSalle(salles[i][j].getValue()),
						instance.getFormateur(formateurs[i][j].getValue()));
			}
	}

	public void printModele() {
		System.out.println("Impression du modele:");
		this.modele = fillModele(this.solutionDeReference, this.getInstance());
		for (int i = 0; i < solutionDeReference.getNbSousGroupes(); i++) {
			System.out.println("SousGroupe " + (i + 1));
			for (int j = 0; j < nbSequences; j++) {
				System.out.println(Creneau.getName(j) + " : " + modele[i][j]);
			}
		}
	}

	public void printSolution() {
		System.out.println("Impression solution(s):");
		for (int l = 0; l < nbSolutionsDemandees; l++) {
			System.out.println("Solution n: " + (l + 1));
			for (int i = 0; i < nbSousGroupes; i++) {
				System.out.println("SousGroupe " + (i + 1));
				for (int j = 0; j < nbSequences; j++)
					System.out.println(Creneau.getName(j) + " : " + resultat[i + nbSousGroupes * l][j]);
			}
		}
		System.out.println("\n");
	}

	public void printDifferenceAvecModele() {
		CaseEdTSousGroupe[][] data = new CaseEdTSousGroupe[nbSousGroupes * 2][nbSequences];
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				data[i][j] = resultat[i][j];
			}
		}
		for (int i = 0; i < nbSousGroupes; i++) {
			for (int j = 0; j < nbSequences; j++) {
				data[nbSousGroupes + i][j] = modele[i][j];
			}
		}

		CalculDifference calculDifference = new CalculDifference(data, nbSousGroupes, 2);
		calculDifference.calcul();
		calculDifference.print();
	}

	public void printDifferences() {
		if (nbSolutionsDemandees > 1) {
			CalculDifference calculDifference = new CalculDifference(resultat, nbSousGroupes, nbSolutionsDemandees);
			calculDifference.calcul();
			calculDifference.print();
		}
	}

	private int getIndiceSeq(String disciplineSubject, int nSeq, int groupe) {
		for (int i = 0; i < this.nbSequences; i++) {
			if (disciplineSubject.equals(sequences[groupe][i].getDiscipline().getName())
					&& sequences[groupe][i].getNumero() == nSeq) {
				return i;
			}
		}
		return -1;
	}

	public SolutionEdt getSolutionDeReference() {
		return solutionDeReference;
	}

	public SolutionEdt getSolutionEDT() {
		return solutionEDT;
	}

	public Probleme getInstance() {
		return instance;
	}

	public void setInstance(Probleme instance) {
		this.instance = instance;
	}

	/***
	 * accès Sequence
	 * 
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

	public void saveSolution() {

	}

	public static void saveSolution(SolutionEdt solutionEdt, String path) {
		File fichier = new File(path);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fichier));
			oos.writeObject(solutionEdt);
			System.out.println("solution ecrite");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
