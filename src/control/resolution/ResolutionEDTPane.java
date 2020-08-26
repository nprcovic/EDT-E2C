package control.resolution;

import java.io.File;
import java.util.ArrayList;
import application.EDTApp;
import control.EDTMainPane;
import control.EdtHBox;
import control.EdtVBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Activite;
import model.CaseDomainesSousGroupe;
import model.CaseEdTSousGroupe;
import model.Formateur;
import model.Discipline;
import model.Probleme;
import model.Salle;
import model.SolutionEdt;
import model.SousGroupe;

public class ResolutionEDTPane extends ScrollPane implements EDTMainPane {
	private EDTApp app;
	private int nbSousGroupes;
	
	private CaseDomainesSousGroupe[][] casesDomaines;
	private SolutionEdt solutionDeReference;
	//private SolutionEdt solution;
	private HBox mainHBox = new EdtHBox();

	public ResolutionEDTPane(EDTApp app) {
		super();
		this.app = app;
		this.nbSousGroupes = getInstance().getNbSousGroupes();
		casesDomaines = createCasesDomainesSousGroupes();
		solutionDeReference = null;//SolutionEdt.load("solution3");
		initWindowContents();
	}

	/***
	 * 
	 * @return les cases de domaines remplies
	 */
	private CaseDomainesSousGroupe[][] createCasesDomainesSousGroupes() {
		CaseDomainesSousGroupe[][] casesDomaines = new CaseDomainesSousGroupe[nbSousGroupes][20];
		for (int i = 0; i < nbSousGroupes; i++) {
			// Activites du sous-groupe
			ArrayList<Activite> activites = getInstance().getSousGroupes().get(i).getProgrammeActivites()
					.getProgramme();
			// Formateurs du sous-groupe
			ArrayList<Formateur> formateurs = new ArrayList<Formateur>();
			for (Formateur formateur : getInstance().getFormateurs())
				if (formateur.getGroupes().contains(getInstance().getSousGroupes().get(i).getGroupe()))
					formateurs.add(formateur);
			// Salles du sous-groupe
			ArrayList<Salle> salles = new ArrayList<Salle>();
			for (Salle salle : getInstance().getSalles())
				if (salle.getGroupes().contains(getInstance().getSousGroupes().get(i).getGroupe()))
					salles.add(salle);
			
			for (int j = 0; j < 20; j++) {
				casesDomaines[i][j] = new CaseDomainesSousGroupe();
				ArrayList<Discipline> disciplines = new ArrayList<Discipline>();
				ArrayList<Formateur> formateursDispos = new ArrayList<Formateur>();
				for (Activite activite : activites)
					if (activite.getDiscipline().hasCreneau(j))
						disciplines.add(activite.getDiscipline());
				casesDomaines[i][j].setDisciplines(disciplines);
				for (Formateur formateur : formateurs)
					if (formateur.getDisponibilite(j))
						formateursDispos.add(formateur);
				casesDomaines[i][j].setFormateurs(formateursDispos);
				casesDomaines[i][j].setSalles(salles);
				if (disciplines.size() == 1)
					casesDomaines[i][j].setDisciplineChoisie(disciplines.get(0));
			}
		}
		return casesDomaines;
	}

	/***
	 * 
	 */
	public void initWindowContents() {
		mainHBox.getChildren().clear();
		setContent(mainHBox);

		TreeItem<Object> resolutionTreeRoot = new TreeItem<Object>("Pré-affectations");
		resolutionTreeRoot.setExpanded(true);

		TreeItem<Object> sousGroupesTreeItem = createSousGroupesItems();

		VBox colonneGauche = new EdtVBox();

		TreeView<Object> resolutionTreeView = new TreeView<Object>(resolutionTreeRoot);
		resolutionTreeRoot.getChildren().addAll(sousGroupesTreeItem);

		Button loadSolutionButton = new Button("Charger solution");
		loadSolutionButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				SolutionEdt solution = SolutionEdt.load(file);
				System.out.println(solution);
				initValuesWith(solution);
				initWindowContents();
			}
		});

		Button loadReferenceButton = new Button("Charger solution de référence");
		loadReferenceButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				solutionDeReference = SolutionEdt.load(file);
			}
		});

		/*
		 * Button saveButton = new Button("Sauvegarde solution");
		 * saveButton.setOnAction( e ->{ FileChooser fileChooser = new FileChooser();
		 * File file = fileChooser.showSaveDialog(null); if(file != null)
		 * solution.save(file); });
		 */
		
		Button searchButton = new Button("Chercher solution");
		searchButton.setOnAction(e -> {
			new SearchWindow(getInstance(), solutionDeReference, casesDomaines, app.getPreferences().getDirectory() + "/derniere_solution");
		});
		colonneGauche.getChildren().addAll(resolutionTreeView, loadSolutionButton, loadReferenceButton, searchButton);

		mainHBox.getChildren().addAll(colonneGauche/* , new Label("rien") */);

		resolutionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			mainHBox.getChildren().clear();
			mainHBox.getChildren().add(colonneGauche);
			if (newValue.getValue().getClass() == SousGroupe.class)
				mainHBox.getChildren().add(createSousGroupeBox(newValue));
			else if (newValue.getValue().equals("SousGroupes"))
				mainHBox.getChildren().add(createSousGroupesBox(sousGroupesTreeItem));
			/*
			 * else mainHBox.getChildren().add(new Label("autre"));
			 */
		});
	}


	/***
	 * 
	 * @param solution
	 */
	private void initValuesWith(SolutionEdt solution) {
		CaseEdTSousGroupe[][] cases = solution.getSolutionPourSousGroupes();
		for (int i = 0; i < cases.length; i++)
			for (int j = 0; j < 20; j++) {
				casesDomaines[i][j].setDisciplineChoisie(cases[i][j].getDiscipline());
				casesDomaines[i][j].setFormateurChoisi(cases[i][j].getFormateur());
				casesDomaines[i][j].setSalleChoisie(cases[i][j].getSalle());
				casesDomaines[i][j].setAssigned(true);
			}
	}

	/***
	 * crée la liste des items des sous-groupes
	 * 
	 * @return
	 */
	private TreeItem<Object> createSousGroupesItems() {
		TreeItem<Object> sousGroupesTreeItem = new TreeItem<Object>("Sous-groupes");
		for (SousGroupe sousGroupe : getInstance().getSousGroupes()) {
			sousGroupesTreeItem.getChildren().add(new TreeItem<Object>(sousGroupe));
		}
		return sousGroupesTreeItem;
	}

	/***
	 * crée la table d'un sous-groupe
	 * 
	 * @param sousGroupe
	 * @return
	 */
	private VBox createSousGroupeBox(TreeItem<Object> sousGroupeTreeItem) {
		VBox sousGroupeBox = new EdtVBox();
		SousGroupe sousGroupe = (SousGroupe) sousGroupeTreeItem.getValue();
		int indiceSousGroupe = 0;
		while (getInstance().getSousGroupes().get(indiceSousGroupe) != sousGroupe)
			indiceSousGroupe++;
		GridPane grille = new GridPane();
		/*
		 * grille.setPadding(new Insets(20)); grille.setHgap(25); grille.setVgap(15);
		 */
		grille.addRow(0, new Label(sousGroupe.getName()), new Label("Lundi"), new Label("Mardi"), new Label("Mercredi"),
				new Label("Jeudi"), new Label("Vendredi"));
		int i;
		for (i = 1; i < 5; i++)
			grille.add(new Label("sequence " + i), 0, i);
		for (i = 0; i < 20; i++) {
			grille.add(createCaseSousGroupe(indiceSousGroupe, i), 1 + i / 4, 1 + i % 4);
		}
		sousGroupeBox.getChildren().add(grille);
		return sousGroupeBox;
	}

	/***
	 * 
	 * @param caseSousGroupe
	 * @return
	 */
	private VBox createCaseSousGroupe(int indiceSousGroupe, int i) {
		VBox caseBox = new EdtVBox(10);

		// Choix de la matière
		ComboBox<Discipline> disciplineComboBox = new ComboBox<Discipline>();
		ArrayList<Discipline> disciplines = casesDomaines[indiceSousGroupe][i].getDisciplines();
		disciplineComboBox.getItems().addAll(disciplines);
		disciplineComboBox.setOnAction(e -> {
			casesDomaines[indiceSousGroupe][i].setDisciplineChoisie(disciplineComboBox.getValue());
			caseBox.setBackground(
					new Background(new BackgroundFill(disciplineComboBox.getValue().getColor(), null, null)));
			if(!disciplineComboBox.getValue().getNecessiteFormateur()) {
				((ComboBox)caseBox.getChildren().get(1)).getSelectionModel().select(Formateur.PERSONNE);
				casesDomaines[indiceSousGroupe][i].setFormateurChoisi(Formateur.PERSONNE);
			}
		});
		if (casesDomaines[indiceSousGroupe][i].getDisciplineChoisie() != null) {
			disciplineComboBox.setValue(casesDomaines[indiceSousGroupe][i].getDisciplineChoisie());
			caseBox.setBackground(
					new Background(new BackgroundFill(disciplineComboBox.getValue().getColor(), null, null)));
		}

		// Choix du formateur
		ComboBox<Formateur> formateurComboBox = new ComboBox<Formateur>();
		ArrayList<Formateur> formateurs = casesDomaines[indiceSousGroupe][i].getFormateurs();
		formateurComboBox.getItems().addAll(formateurs);
		formateurComboBox.setOnAction(e -> {
			casesDomaines[indiceSousGroupe][i].setFormateurChoisi(formateurComboBox.getValue());
		});
		if (casesDomaines[indiceSousGroupe][i].getFormateurChoisi() != null)
			formateurComboBox.setValue(casesDomaines[indiceSousGroupe][i].getFormateurChoisi());

		// Choix de la salle
		ComboBox<Salle> salleComboBox = new ComboBox<Salle>();
		ArrayList<Salle> salles = casesDomaines[indiceSousGroupe][i].getSalles();
		salleComboBox.getItems().addAll(salles);
		salleComboBox.setOnAction(e -> {
			casesDomaines[indiceSousGroupe][i].setSalleChoisie(salleComboBox.getValue());
		});
		if (casesDomaines[indiceSousGroupe][i].getSalleChoisie() != null)
			salleComboBox.setValue(casesDomaines[indiceSousGroupe][i].getSalleChoisie());

		caseBox.getChildren().addAll(disciplineComboBox, formateurComboBox, salleComboBox);
		return caseBox;
	}

	/***
	 * Crée la table des sous-groupes
	 * 
	 * @param SousGroupesTreeItem
	 * @return
	 */
	private VBox createSousGroupesBox(TreeItem<Object> groupesTreeItem) {
		VBox sousGroupesBox = new EdtVBox();

		return sousGroupesBox;
	}

/*	public SolutionEdt getSolution() {
		return solution;
	}

	public void setSolution(SolutionEdt solution) {
		this.solution = solution;
	}*/

	public HBox getMainHBox() {
		return mainHBox;
	}

	public void setMainHBox(HBox mainHBox) {
		this.mainHBox = mainHBox;
	}

	public Probleme getInstance() {
		return app.getInstance();
	}

	public void setInstance(Probleme instance) {
		app.setInstance(instance);
	}

}
