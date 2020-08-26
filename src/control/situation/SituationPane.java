package control.situation;

import application.EDTApp;
import control.EDTMainPane;
import control.EdtHBox;
import control.EdtVBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Activite;
import model.Formateur;
import model.Groupe;
import model.Discipline;
import model.Probleme;
import model.ProgrammeActivites;
import model.Salle;
import model.SousGroupe;
import model.TypeSalle;

public class SituationPane extends ScrollPane implements EDTMainPane{

	private EDTApp app;
	//private ScrollPane pane;
	private HBox mainHBox = new EdtHBox();

	/***
	 * Crée un formulaire de situation à partir d'une instance
	 * 
	 * @param instance
	 */
	public SituationPane(EDTApp app) {
		//instance = Probleme.load("situationTest");
		//instance.setProfilsActivites(new ArrayList<ProgrammeActivites>());
		this.app = app;
		//pane = new ScrollPane();
		initWindowContents();
	}

	/***
	 * 
	 */
	public void initWindowContents() {
		mainHBox.getChildren().clear();
		setContent(mainHBox);

		TreeItem<Object> situationTreeRoot = new TreeItem<Object>("Situation");
		situationTreeRoot.setExpanded(true);

		TreeItem<Object> formateursTreeItem = createFormateursItems();
		TreeItem<Object> groupesTreeItem = createGroupesItems();
		TreeItem<Object> sallesTreeItem = createSallesItems();
		TreeItem<Object> disciplinesTreeItem = createDisciplinesItems();
		//TreeItem<Object> creneauxTreeItem = new TreeItem<Object>("Creneaux");
		TreeItem<Object> typesSallesTreeItem = createTypesSallesItems();
		TreeItem<Object> profilsActivitesTreeItem = createProfilsActivitesItems();
		// TreeItem<String> TreeItem = new TreeItem<String>("");


		VBox colonneGauche = new VBox();

		TreeView<Object> situationTreeView = new TreeView<Object>(situationTreeRoot);
		situationTreeRoot.getChildren().addAll(formateursTreeItem, groupesTreeItem, sallesTreeItem, disciplinesTreeItem,
				/*creneauxTreeItem,*/ typesSallesTreeItem, profilsActivitesTreeItem);

		
		/*Button saveButton = new Button("Sauvegarde situation");
		saveButton.setOnAction( e ->{
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if(file != null)
				getInstance().save(file);
		});*/
		
		colonneGauche.getChildren().addAll(situationTreeView/*, saveButton*/);
		
		mainHBox.getChildren().addAll(colonneGauche, new Label(""));
		
		situationTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			mainHBox.getChildren().clear();
			mainHBox.getChildren().add(colonneGauche);
			if (newValue.getValue().getClass() == TypeSalle.class)
				mainHBox.getChildren().add(createTypeSalleBox(newValue));
			else if (newValue.getValue().getClass() == Formateur.class)
				mainHBox.getChildren().add(createFormateurBox(newValue));
			else if (newValue.getValue().getClass() == Groupe.class)
				mainHBox.getChildren().add(createGroupeBox(newValue));
			else if (newValue.getValue().getClass() == SousGroupe.class)
				mainHBox.getChildren().add(createSousGroupeBox(newValue));
			else if (newValue.getValue().getClass() == Discipline.class)
				mainHBox.getChildren().add(createDisciplineBox(newValue));
			else if (newValue.getValue().getClass() == Salle.class)
				mainHBox.getChildren().add(createSalleBox(newValue));
			else if (newValue.getValue().getClass() == ProgrammeActivites.class)
				mainHBox.getChildren().add(createProfilActivitesBox(newValue));
			else if (newValue.getValue().equals("Types de salles"))
				mainHBox.getChildren().add(createTypesSallesBox(typesSallesTreeItem));
			else if (newValue.getValue().equals("Formateurs"))
				mainHBox.getChildren().add(createFormateursBox(formateursTreeItem));
			else if (newValue.getValue().equals("Groupes"))
				mainHBox.getChildren().add(createGroupesBox(groupesTreeItem));
			else if (newValue.getValue().equals("Salles"))
				mainHBox.getChildren().add(createSallesBox(sallesTreeItem));
			else if (newValue.getValue().equals("Disciplines"))
				mainHBox.getChildren().add(createDisciplinesBox(disciplinesTreeItem));			
			else if (newValue.getValue().equals("Profils d'activités"))
				mainHBox.getChildren().add(createProfilsActivitesBox(profilsActivitesTreeItem));
			/*else
				mainHBox.getChildren().add(new Label("autre"));*/
		});
	}

	/*** 
	 * LES FORMATEURS
	 */
	
	/***
	 * crée la liste des items des formateurs
	 * 
	 * @return
	 */
	private TreeItem<Object> createFormateursItems() {
		TreeItem<Object> formateursTreeItem = new TreeItem<Object>("Formateurs");
		for (Formateur formateur : getInstance().getFormateurs()) {
			formateursTreeItem.getChildren().add(new TreeItem<Object>(formateur));
		}
		return formateursTreeItem;
	}

	/***
	 * Crée le formulaire des formateurs
	 * @param typesSallesTreeItem
	 * @return
	 */
	private VBox createFormateursBox(TreeItem<Object> formateursTreeItem) {
		VBox formateursBox = new EdtVBox();

		Button addFormateurButton = new Button("Ajout d'un formateur");
		addFormateurButton.setOnAction(e -> {
			Formateur formateur = new Formateur();
			getInstance().getFormateurs().add(formateur);
			formateursTreeItem.getChildren().add(new TreeItem<Object>(formateur));
		});
		formateursBox.getChildren().add(addFormateurButton);
		return formateursBox;
	}

	/***
	 * crée le formulaire d'un formateur
	 * @param formateur
	 * @return
	 */
	private HBox createFormateurBox(TreeItem<Object> formateurTreeItem) {
		Formateur formateur = (Formateur)formateurTreeItem.getValue();
		//Le nom
		HBox nameHBox = new EdtHBox();
		TextField nameTextField = new TextField(formateur.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> formateur.setName(observable.getValue()));
		nameHBox.getChildren().addAll(new Label("Nom :"), nameTextField);

		// Les groupes associés

		GridPane groupesGridPane = new GridPane();
		/*groupesGridPane.setPadding(new Insets(20));
		groupesGridPane.setHgap(25);
		groupesGridPane.setVgap(15);*/
		groupesGridPane.add(new Label("Groupes"), 0, 0);

		int i = 0;
		for(Groupe groupe : getInstance().getGroupes()) {
			//groupesGridPane.add(new Label(groupe.getName()) , 0, i + 1);
			CheckBox groupeCheckBox = new CheckBox(groupe.getName());
			groupeCheckBox.setId("" + i);
			groupeCheckBox.setSelected(formateur.getGroupes().contains(groupe));
			groupeCheckBox.setOnAction(e -> {
				if(formateur.getGroupes().contains(groupe))
					formateur.getGroupes().remove(groupe);
				else
					formateur.addGroupe(groupe);
			});
			groupesGridPane.add(groupeCheckBox, 0, 1 + i);
			i++;
		}

		// Les disciplines enseignées

		GridPane disciplinesGridPane = new GridPane();

		disciplinesGridPane.add(new Label("Disciplines"), 0, 0);
		
		i = 0;
		for(Discipline discipline : getInstance().getDisciplines()) {
			CheckBox groupeCheckBox = new CheckBox(discipline.getName());
			groupeCheckBox.setBackground(new Background(new BackgroundFill(discipline.getColor(), null, null)));
			groupeCheckBox.setId("" + i);
			groupeCheckBox.setSelected(formateur.getDisciplines().contains(discipline));
			groupeCheckBox.setOnAction(e -> {
				if(formateur.getDisciplines().contains(discipline))
					formateur.getDisciplines().remove(discipline);
				else
					formateur.addDiscipline(discipline);
			});
			disciplinesGridPane.add(groupeCheckBox, 0, 1 + i);
			i++;
		}

		
		// Les disponibilités
		GridPane disponibilitesGridPane = new GridPane();
		disponibilitesGridPane.setPadding(new Insets(20));
		disponibilitesGridPane.setHgap(25);
		disponibilitesGridPane.setVgap(15);
		disponibilitesGridPane.addRow(0,
				new Label("Disponibilités"),
				new Label("Lundi"),
				new Label("Mardi"),
				new Label("Mercredi"),
				new Label("Jeudi"),
				new Label("Vendredi"));

		for(i = 1; i < 5; i++) 
			disponibilitesGridPane.add(new Label("sequence " + i) , 0, i);
		for(i = 0 ; i < 20; i++) {
			CheckBox dispoCheckBox = new CheckBox();
			dispoCheckBox.setId("" + i);
			dispoCheckBox.setSelected(formateur.getDisponibilite(i));
			dispoCheckBox.setOnAction(e -> formateur.toggleDisponibilite(Integer.parseInt(dispoCheckBox.getId())));
			disponibilitesGridPane.add(dispoCheckBox, 1 + i / 4, 1 + i % 4);
		}
		// La suppression
		Button supprFormateurButton = new Button("Supprimer");
		supprFormateurButton.setOnAction(e -> {
			//System.out.println(getInstance());
			formateur.notifyObservers();
			getInstance().getFormateurs().remove(formateur);
			//System.out.println(getInstance());
			//System.out.println(formateurTreeItem.getParent());
			formateurTreeItem.getParent().getChildren().remove(formateurTreeItem);
			//System.out.println(formateurTreeItem.getParent());
		});
		
		VBox colonneGauche = new EdtVBox();
		colonneGauche.getChildren().addAll(nameHBox, groupesGridPane, supprFormateurButton);
		HBox formateurBox = new EdtHBox();
		formateurBox.getChildren().addAll(colonneGauche, disciplinesGridPane, disponibilitesGridPane);
		
		return formateurBox;
	}

	/***
	 * LES GROUPES
	 */
	
	/***
	 * crée la liste des items des groupes
	 * 
	 * @return
	 */
	private TreeItem<Object> createGroupesItems() {
		TreeItem<Object> groupesTreeItem = new TreeItem<Object>("Groupes");
		for (Groupe groupe : getInstance().getGroupes()) {
			groupesTreeItem.getChildren().add(createSousGroupesItems(groupe));
		}
		return groupesTreeItem;
	}


	/***
	 * Crée le formulaire des groupes
	 * @param typesSallesTreeItem
	 * @return
	 */
	private VBox createGroupesBox(TreeItem<Object> groupesTreeItem) {
		VBox groupesBox = new EdtVBox();

		Button addGroupeButton = new Button("Ajout d'un groupe");
		addGroupeButton.setOnAction(e -> {
			//Creation du groupe
			Groupe groupe = new Groupe();
			getInstance().getGroupes().add(groupe);
			//TreeItem<Object> groupeTreeItem = new TreeItem<Object>(groupe);
			//Creation de 3 sous-groupes
			SousGroupe sousGroupe1 = new SousGroupe(groupe, 1, 15, null);
			SousGroupe sousGroupe2 = new SousGroupe(groupe, 2, 15, null);
			SousGroupe sousGroupe3 = new SousGroupe(groupe, 3, 15, null);
			groupe.getSousGroupes().add(sousGroupe1);
			groupe.getSousGroupes().add(sousGroupe2);
			groupe.getSousGroupes().add(sousGroupe3);
			TreeItem<Object> groupeTreeItem = createSousGroupesItems(groupe);
			groupesTreeItem.getChildren().add(groupeTreeItem);
			
		});
		groupesBox.getChildren().add(addGroupeButton);
		return groupesBox;
	}

	/***
	 * crée le formulaire d'un groupe
	 * @param groupe
	 * @return
	 */
	private VBox createGroupeBox(TreeItem<Object> groupeTreeItem) {
		VBox groupeBox = new EdtVBox();
		Groupe groupe = (Groupe)groupeTreeItem.getValue();
		
		HBox nameHBox = new EdtHBox();
		TextField nameTextField = new TextField(groupe.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> groupe.setName(observable.getValue()));
		nameHBox.getChildren().addAll(new Label("Nom"), nameTextField);
		/* */
		Button supprGroupeButton = new Button("Supprimer");
		supprGroupeButton.setOnAction(e -> {
			//System.out.println(getInstance());
			groupe.notifyObservers();
			getInstance().getGroupes().remove(groupe);
			//System.out.println(getInstance());
			//System.out.println(groupeTreeItem.getParent());
			groupeTreeItem.getParent().getChildren().remove(groupeTreeItem);
			//System.out.println(groupeTreeItem.getParent());
		});
		groupeBox.getChildren().addAll(nameHBox, supprGroupeButton);
		
		return groupeBox;
	}
	
	/***
	 * LES SOUS-GROUPES
	 */
	
	/***
	 * crée la liste des items des sous-groupes
	 * 
	 * @return
	 */
	private TreeItem<Object> createSousGroupesItems(Groupe groupe) {
		TreeItem<Object> sousGroupesTreeItem = new TreeItem<Object>(groupe);
		for (SousGroupe sousGroupe : groupe.getSousGroupes()) {
			sousGroupesTreeItem.getChildren().add(new TreeItem<Object>(sousGroupe));
		}
		return sousGroupesTreeItem;
	}

	/***
	 * Crée le formulaire des sous-groupes
	 * @param typesSallesTreeItem
	 * @return
	 */
	private VBox createSousGroupesBox(TreeItem<Object> sousGroupesTreeItem, Groupe groupe) {
		VBox sousGroupesBox = new VBox();

		Button addSousGroupeButton = new Button("Ajout d'un sous-groupe");
		addSousGroupeButton.setOnAction(e -> {
			//Creation du groupe
			SousGroupe sousGroupe = new SousGroupe(groupe, groupe.getSousGroupes().size() + 1, 15, null);
			groupe.getSousGroupes().add(sousGroupe);
			sousGroupesTreeItem.getChildren().add(new TreeItem<Object>(sousGroupe));
			
		});
		sousGroupesBox.getChildren().add(addSousGroupeButton);
		return sousGroupesBox;
	}

	/***
	 * crée le formulaire d'un sous-groupe
	 * @param groupe
	 * @return
	 */
	private HBox createSousGroupeBox(TreeItem<Object> sousGroupeTreeItem) {
		SousGroupe sousGroupe = (SousGroupe)sousGroupeTreeItem.getValue();
		
		HBox nameHBox = new EdtHBox();
		nameHBox.getChildren().addAll(new Label("Nom : " + sousGroupe.getName()));

		// La capacité
		HBox capaciteBox = new EdtHBox();
		Spinner<Integer> capaciteSpinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, sousGroupe.getCapacity());
        capaciteSpinner.setEditable(true);
        capaciteSpinner.setValueFactory(valueFactory);
        capaciteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> sousGroupe.setCapacity(newValue));
        capaciteBox.getChildren().addAll(new Label("Capacité"), capaciteSpinner);

        // Le référent
		HBox referentBox = new EdtHBox();
		ComboBox<Formateur> referentComboBox = new ComboBox<Formateur>();
		referentComboBox.getItems().addAll(getInstance().getFormateurs());
		referentComboBox.setOnAction(e -> {
			sousGroupe.setReferent(referentComboBox.getValue());
		});
		if(sousGroupe.getReferent() != null)
			referentComboBox.setValue(sousGroupe.getReferent());
		referentBox.getChildren().addAll(new Label("Référent : "), referentComboBox);

		// Le choix du profil
		HBox choixProfilBox = new EdtHBox();
		ComboBox<ProgrammeActivites> choixProfilComboBox = new ComboBox<ProgrammeActivites>();
		choixProfilComboBox.getItems().addAll(getInstance().getProfilsActivites());
		choixProfilComboBox.setOnAction(e -> {
			sousGroupe.setProfilActivites(choixProfilComboBox.getValue());
			sousGroupe.initProgrammeWithProfil();
			mainHBox.getChildren().remove(1);			
			mainHBox.getChildren().add(createSousGroupeBox(sousGroupeTreeItem));
		});
		if(sousGroupe.getProfilActivites() != null)
			choixProfilComboBox.setValue(sousGroupe.getProfilActivites());
		choixProfilBox.getChildren().addAll(new Label("Profil : "), choixProfilComboBox);
		
		// Choix des activites
		GridPane choixActivitesGridPane = new GridPane();
		int i = 0;
		for(Activite activite : sousGroupe.getProgrammeActivites().getProgramme()) {
			placeActivite(choixActivitesGridPane, i++, activite);
		}

		
        // Suppression
		Button supprSousGroupeButton = new Button("Supprimer");
		supprSousGroupeButton.setOnAction(e -> {
			//System.out.println(getInstance());
			sousGroupe.notifyObservers();
			sousGroupe.getGroupe().getSousGroupes().remove(sousGroupe);
			//System.out.println(getInstance());
			//System.out.println(sousGroupeTreeItem.getParent());
			sousGroupeTreeItem.getParent().getChildren().remove(sousGroupeTreeItem);
			//System.out.println(sousGroupeTreeItem.getParent());
		});
		
		VBox colonneGaucheBox = new EdtVBox();
		colonneGaucheBox.getChildren().addAll(nameHBox, capaciteBox, referentBox, supprSousGroupeButton);
		VBox colonneDroiteBox = new EdtVBox();
		colonneDroiteBox.getChildren().addAll(choixProfilBox, choixActivitesGridPane);
		HBox groupeBox = new EdtHBox();
		groupeBox.getChildren().addAll(colonneGaucheBox, colonneDroiteBox);
		
		return groupeBox;
	}

	/***
	 * 
	 * @param gridPane
	 * @param i
	 * @param discipline
	 * @param profilActivites
	 */
	private void placeActivite(GridPane gridPane, int rowIndex, Activite activite) {
		Label label = new Label(activite.getDiscipline().getName());
		label.setBackground(new Background(new BackgroundFill(activite.getDiscipline().getColor(), null, null)));
		gridPane.add(label, 0, rowIndex);
		Spinner<Integer> occurrencesSpinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, activite.getOccurences());
        occurrencesSpinner.setEditable(true);
        occurrencesSpinner.setValueFactory(valueFactory);
        occurrencesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
        				activite.setOccurences(newValue);
        });
        gridPane.add(occurrencesSpinner, 1, rowIndex);		
	}


	/***
	 * LES SALLES
	 */
	
	/***
	 * crée la liste des items des salles
	 * 
	 * @return
	 */
	private TreeItem<Object> createSallesItems() {
		TreeItem<Object> sallesTreeItem = new TreeItem<Object>("Salles");
		for (Salle salle : getInstance().getSalles()) {
			sallesTreeItem.getChildren().add(new TreeItem<Object>(salle));
		}
		return sallesTreeItem;
	}


	/***
	 * Crée le formulaire des salles
	 * @param sallesTreeItem
	 * @return
	 */
	private VBox createSallesBox(TreeItem<Object> sallesTreeItem) {
		VBox sallesBox = new EdtVBox();

		Button addSalleButton = new Button("Ajout d'une salle");
		addSalleButton.setOnAction(e -> {
			Salle salle = new Salle();
			getInstance().getSalles().add(salle);
			sallesTreeItem.getChildren().add(new TreeItem<Object>(salle));
		});
		sallesBox.getChildren().add(addSalleButton);
		return sallesBox;
	}
	
	/***
	 * crée le formulaire d'une salle
	 * @param salle
	 * @return
	 */
	private VBox createSalleBox(TreeItem<Object> salleTreeItem) {
		Salle salle = (Salle)salleTreeItem.getValue();
		
		// Le nom
		HBox nameBox = new EdtHBox();
		TextField nameTextField = new TextField(salle.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> salle.setName(observable.getValue()));
		nameBox.getChildren().addAll(new Label("Nom"), nameTextField);
		
		// Le type de salle
		HBox typeSalleBox = new EdtHBox();
		ComboBox<TypeSalle> choixTypeComboBox = new ComboBox<TypeSalle>();
		choixTypeComboBox.getItems().addAll(getInstance().getTypesSalles());
		choixTypeComboBox.setOnAction(e -> {
			salle.setType(choixTypeComboBox.getValue());
		});
		if(salle.getType() != null)
			choixTypeComboBox.setValue(salle.getType());
		typeSalleBox.getChildren().addAll(new Label("Type de salle : "), choixTypeComboBox);
		
		// Les groupes associés

		GridPane groupesGridPane = new GridPane();
		/*groupesGridPane.setPadding(new Insets(20));
		groupesGridPane.setHgap(25);
		groupesGridPane.setVgap(15);*/
		groupesGridPane.add(new Label("Groupes"), 0, 0);

		int i = 0;
		for(Groupe groupe : getInstance().getGroupes()) {
			//groupesGridPane.add(new Label(groupe.getName()) , 0, i + 1);
			CheckBox groupeCheckBox = new CheckBox(groupe.getName());
			groupeCheckBox.setId("" + i);
			groupeCheckBox.setSelected(salle.getGroupes().contains(groupe));
			groupeCheckBox.setOnAction(e -> {
				if(salle.getGroupes().contains(groupe))
					salle.getGroupes().remove(groupe);
				else
					salle.addGroupe(groupe);
			});
			groupesGridPane.add(groupeCheckBox, 0, 1 + i);
			i++;
		}

		
		// La capacité
		HBox capaciteBox = new EdtHBox();
		Spinner<Integer> capaciteSpinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, salle.getCapacity());
        capaciteSpinner.setEditable(true);
        capaciteSpinner.setValueFactory(valueFactory);
        capaciteSpinner.valueProperty().addListener((observable, oldValue, newValue) -> salle.setCapacity(newValue));
        capaciteBox.getChildren().addAll(new Label("Capacité"), capaciteSpinner);
        
		// Suppression de la salle
		Button supprSalleButton = new Button("Supprimer");
		supprSalleButton.setOnAction(e -> {
			getInstance().getSalles().remove(salle);
			salleTreeItem.getParent().getChildren().remove(salleTreeItem);
		});
		
		VBox salleBox = new EdtVBox();
		salleBox.getChildren().addAll(nameBox, typeSalleBox, groupesGridPane, capaciteBox, supprSalleButton);
		
		return salleBox;
	}

	/***
	 * LES DISCIPLINES
	 */
	
	/***
	 * crée la liste des items des disciplines
	 * 
	 * @return
	 */
	private TreeItem<Object> createDisciplinesItems() {
		TreeItem<Object> disciplinesTreeItem = new TreeItem<Object>("Disciplines");
		for (Discipline discipline : getInstance().getDisciplines()) {
			disciplinesTreeItem.getChildren().add(new TreeItem<Object>(discipline));
		}
		return disciplinesTreeItem;
	}

	/***
	 * Crée le formulaire des disciplines
	 * @param disciplinesTreeItem
	 * @return
	 */
	private VBox createDisciplinesBox(TreeItem<Object> disciplinesTreeItem) {
		VBox disciplinesBox = new EdtVBox();

		Button addDisciplineButton = new Button("Ajout d'une discipline");
		addDisciplineButton.setOnAction(e -> {
			Discipline discipline = new Discipline();
			getInstance().getDisciplines().add(discipline);
			disciplinesTreeItem.getChildren().add(new TreeItem<Object>(discipline));
		});
		disciplinesBox.getChildren().add(addDisciplineButton);
		return disciplinesBox;
	}


	/***
	 * crée le formulaire d'une discipline
	 * @param discipline
	 * @return
	 */
	private HBox createDisciplineBox(TreeItem<Object> disciplineTreeItem) {
		Discipline discipline = (Discipline)disciplineTreeItem.getValue();
		//System.out.println(discipline);
		if(discipline.getCreneaux() == null)
			discipline.setCreneaux(new Boolean [] {true, true, true, true, true,true, true, true, true, true,true, true, true, true, true,true, true, true, true, true});
		
		// Le nom
		HBox nameHBox = new EdtHBox();
		TextField nameTextField = new TextField(discipline.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> discipline.setName(observable.getValue()));
		nameHBox.getChildren().addAll(new Label("Nom"), nameTextField);
				
		// Les types de salles associés

		GridPane typesSallesGridPane = new GridPane();
		/*typesSallesGridPane.setPadding(new Insets(20));
		typesSallesGridPane.setHgap(25);
		typesSallesGridPane.setVgap(15);*/
		typesSallesGridPane.add(new Label("Types de salles"), 0, 0);

		int i = 0;
		for(TypeSalle typeSalle : getInstance().getTypesSalles()) {
			//typesSallesGridPane.add(new Label(typeSalle.getName()) , 0, i + 1);
			CheckBox typeSalleCheckBox = new CheckBox(typeSalle.getName());
			typeSalleCheckBox.setId("" + i);
			typeSalleCheckBox.setSelected(discipline.getTypesSalle().contains(typeSalle));
			typeSalleCheckBox.setOnAction(e -> {
				if(discipline.getTypesSalle().contains(typeSalle))
					discipline.getTypesSalle().remove(typeSalle);
				else
					discipline.addTypeSalle(typeSalle);
			});
			typesSallesGridPane.add(typeSalleCheckBox, 0, 1 + i);
			i++;
		}
		
		// Les séquences possibles
		GridPane sequencesGridPane = new GridPane();
		/*sequencesGridPane.setPadding(new Insets(20));
		sequencesGridPane.setHgap(25);
		sequencesGridPane.setVgap(15);*/
		sequencesGridPane.addRow(0,
				new Label("Séquences"),
				new Label("Lundi"),
				new Label("Mardi"),
				new Label("Mercredi"),
				new Label("Jeudi"),
				new Label("Vendredi"));

		for(i = 1; i < 5; i++) 
			sequencesGridPane.add(new Label("sequence " + i) , 0, i);
		for(i = 0 ; i < 20; i++) {
			CheckBox sequenceCheckBox = new CheckBox();
			sequenceCheckBox.setId("" + i);
			sequenceCheckBox.setSelected(discipline.hasCreneau(i));
			sequenceCheckBox.setOnAction(e -> discipline.toggleCreneau(Integer.parseInt(sequenceCheckBox.getId())));
			sequencesGridPane.add(sequenceCheckBox, 1 + i / 4, 1 + i % 4);
		}

		// Nécessité d'un formateur ou pas
		CheckBox necessiteFormateurCheckBox = new CheckBox("Formateur nécessaire");
		necessiteFormateurCheckBox.setSelected(discipline.getNecessiteFormateur());
		necessiteFormateurCheckBox.selectedProperty()
				.addListener((observable, oldValue, newValue) -> discipline.setNecessiteFormateur(observable.getValue()));

		// Choix de la couleur
		ColorPicker colorPicker = new ColorPicker(discipline.getColor());
		colorPicker.setOnAction(e -> discipline.setColor(colorPicker.getValue()));
		
		// Suppression de la discipline
		Button supprDisciplineButton = new Button("Supprimer");
		supprDisciplineButton.setOnAction(e -> {
			discipline.notifyObservers();
			getInstance().getDisciplines().remove(discipline);
			disciplineTreeItem.getParent().getChildren().remove(disciplineTreeItem);
			/*for(SousGroupe sousGroupe : getInstance().getSousGroupes())
				sousGroupe.getProgrammeActivites().setProgramme((ArrayList<Activite>) sousGroupe.getProgrammeActivites().getProgramme().stream().filter(activite -> activite.getDiscipline() != discipline).collect(Collectors.toList()));
			for(ProgrammeActivites profil : getInstance().getProfilsActivites())
				profil.setProgramme((ArrayList<Activite>) profil.getProgramme().stream().filter(activite -> activite.getDiscipline() != discipline).collect(Collectors.toList()));*/				
		});
		
		VBox colonneGaucheBox = new EdtVBox();
		colonneGaucheBox.getChildren().addAll(nameHBox, necessiteFormateurCheckBox, colorPicker, supprDisciplineButton);
		HBox disciplineBox = new EdtHBox();
		disciplineBox.getChildren().addAll(colonneGaucheBox, typesSallesGridPane, sequencesGridPane);
		
		return disciplineBox;
	}

	/***
	 * LES SALLES
	 */
	
	/***
	 * crée la liste des items des types de salles
	 * 
	 * @return
	 */
	private TreeItem<Object> createTypesSallesItems() {
		TreeItem<Object> typesSallesTreeItem = new TreeItem<Object>("Types de salles");
		for (TypeSalle typeSalle : getInstance().getTypesSalles()) {
			typesSallesTreeItem.getChildren().add(new TreeItem<Object>(typeSalle));
		}
		return typesSallesTreeItem;
	}

	/***
	 * Crée le formulaire des types de salles
	 * @param typesSallesTreeItem
	 * @return
	 */
	private VBox createTypesSallesBox(TreeItem<Object> typesSallesTreeItem) {
		VBox typesSallesBox = new EdtVBox();

		Button addTypeSalleButton = new Button("Ajout d'un type de Salle");
		addTypeSalleButton.setOnAction(e -> {
			TypeSalle typeSalle = new TypeSalle();
			getInstance().getTypesSalles().add(typeSalle);
			typesSallesTreeItem.getChildren().add(new TreeItem<Object>(typeSalle));
		});
		typesSallesBox.getChildren().add(addTypeSalleButton);
		return typesSallesBox;
	}

	/***
	 * crée le formulaire d'un type de salle
	 * @param typeSalle
	 * @return
	 */
	private VBox createTypeSalleBox(TreeItem<Object> typeSalleTreeItem) {
		TypeSalle typeSalle = (TypeSalle)typeSalleTreeItem.getValue();
		TextField nameTextField = new TextField(typeSalle.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> typeSalle.setName(observable.getValue()));
		
		CheckBox partageableCheckBox = new CheckBox("partageable");
		partageableCheckBox.setSelected(typeSalle.getPartageable());
		partageableCheckBox.selectedProperty()
				.addListener((observable, oldValue, newValue) -> typeSalle.setPartageable(observable.getValue()));
		
		Button supprTypeSalleButton = new Button("Supprimer");
		supprTypeSalleButton.setOnAction(e -> {
			//System.out.println(getInstance());
			typeSalle.notifyObservers();
			getInstance().getTypesSalles().remove(typeSalle);
			//System.out.println(getInstance());
			//System.out.println(typeSalleTreeItem.getParent());
			typeSalleTreeItem.getParent().getChildren().remove(typeSalleTreeItem);
			//System.out.println(typeSalleTreeItem.getParent());
		});
		
		VBox typeSalleBox = new EdtVBox();
		typeSalleBox.getChildren().addAll(nameTextField, partageableCheckBox, supprTypeSalleButton);
		
		return typeSalleBox;
	}

	/***
	 * LES PROFILS D'ACTIVITES
	 */
	
	/***
	 * crée la liste des items des profils d'activites
	 * 
	 * @return
	 */
	private TreeItem<Object> createProfilsActivitesItems() {
		TreeItem<Object> profilsActivitesTreeItem = new TreeItem<Object>("Profils d'activités");
		for (ProgrammeActivites profilActivites: getInstance().getProfilsActivites()) {
			profilsActivitesTreeItem.getChildren().add(new TreeItem<Object>(profilActivites));
		}
		return profilsActivitesTreeItem;
	}

	/***
	 * Crée le formulaire des profils d'Activites
	 * @param profilsActivitesTreeItem
	 * @return
	 */
	private VBox createProfilsActivitesBox(TreeItem<Object> profilsActivitesTreeItem) {
		VBox profilsActivitesBox = new EdtVBox();

		Button addProfilActivitesButton = new Button("Ajout d'un profil d'activités");
		addProfilActivitesButton.setOnAction(e -> {
			ProgrammeActivites profilActivites = new ProgrammeActivites();
			getInstance().getProfilsActivites().add(profilActivites);
			profilsActivitesTreeItem.getChildren().add(new TreeItem<Object>(profilActivites));
		});
		profilsActivitesBox.getChildren().add(addProfilActivitesButton);
		return profilsActivitesBox;
	}

	/***
	 * crée le formulaire d'un profil d'activités
	 * @param discipline
	 * @return
	 */
	private HBox createProfilActivitesBox(TreeItem<Object> profilActivitesTreeItem) {
		ProgrammeActivites profilActivites = (ProgrammeActivites)profilActivitesTreeItem.getValue();
		
		// Le nom
		HBox nameBox = new EdtHBox();
		TextField nameTextField = new TextField(profilActivites.getName());
		nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> profilActivites.setName(observable.getValue()));
		nameBox.getChildren().addAll(new Label("Nom"), nameTextField);
				
		Label sommeLabel = new Label("0");
		// Choix des occurences de chaque matière
		GridPane choixOccurencesDisciplinesGridPane = new GridPane();
		int i = 0;
		//System.out.println(profilActivites.getProgramme());
		for(Discipline discipline : getInstance().getDisciplines()) {
			placeDisciplineOccurence(choixOccurencesDisciplinesGridPane, i++, discipline, profilActivites);
		}

		// Suppression du profil d'activité
		Button supprProfilActiviteButton = new Button("Supprimer");
		supprProfilActiviteButton.setOnAction(e -> {
			profilActivites.notifyObservers();
			getInstance().getProfilsActivites().remove(profilActivites);
			profilActivitesTreeItem.getParent().getChildren().remove(profilActivitesTreeItem);
		});
		
		VBox colonneGauche = new EdtVBox();
		colonneGauche.getChildren().addAll(nameBox, sommeLabel,  supprProfilActiviteButton);
		HBox profilActivitesBox = new EdtHBox();
		profilActivitesBox.getChildren().addAll(colonneGauche, choixOccurencesDisciplinesGridPane);
		
		return profilActivitesBox;
	}

	/***
	 * 
	 * @param gridPane
	 * @param i
	 * @param discipline
	 * @param profilActivites
	 */
	private void placeDisciplineOccurence(GridPane gridPane, int rowIndex, Discipline discipline, ProgrammeActivites profilActivites) {
		Label label = new Label(discipline.getName());
		label.setBackground(new Background(new BackgroundFill(discipline.getColor(), null, null)));
		gridPane.add(label, 0, rowIndex);
		int nbOcc = -1;
		for(Activite activite :  profilActivites.getProgramme())
			if(activite.getDiscipline() == discipline)
				nbOcc = activite.getOccurences();
		Spinner<Integer> occurrencesSpinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 20, nbOcc);
        occurrencesSpinner.setEditable(true);
        occurrencesSpinner.setValueFactory(valueFactory);
        occurrencesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
        	//System.out.println("A traiter" + oldValue + newValue);
        	if(oldValue.intValue() == -1)
        		profilActivites.getProgramme().add(new Activite(discipline, 0));
        	else if(newValue.intValue() == -1) {
        		Activite aRetirer = null;
        		for(Activite activite :  profilActivites.getProgramme())
        			if(activite.getDiscipline() == discipline)
        				aRetirer = activite; 
        		profilActivites.getProgramme().remove(aRetirer);
        	}
        	else
        		for(Activite activite :  profilActivites.getProgramme())
        			if(activite.getDiscipline() == discipline)
        				activite.setOccurences(newValue);
        });
        gridPane.add(occurrencesSpinner, 1, rowIndex);		
	}
	

	public Probleme getInstance() {
		return app.getInstance();
	}

	public void setInstance(Probleme instance) {
		app.setInstance(instance);
	}

}
