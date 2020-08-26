package control.consultation;

import java.io.File;

import control.EDTMainPane;
import control.EdtVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.CaseEdTFormateur;
import model.CaseEdTSalle;
import model.CaseEdTSousGroupe;
import model.Formateur;
import model.Probleme;
import model.Salle;
import model.SolutionEdt;
import model.SousGroupe;

public class ConsultationEDTPane extends ScrollPane implements EDTMainPane{
	
	private SolutionEdt solution;
	private HBox mainHBox = new HBox();
	private NameControllers mode = new TextFieldControllers();
	
	public ConsultationEDTPane(SolutionEdt solutionEdt) {
		super();
		this.solution = solutionEdt;
		//setStyle("-fx-font: normal bold 20px 'serif' ");

		initWindowContents();
	}

	public void initWindowContents() {
		mainHBox.getChildren().clear();
		setContent(mainHBox);

		TreeItem<Object> visualisationTreeRoot = new TreeItem<Object>("Solution");
		visualisationTreeRoot.setExpanded(true);

		TreeItem<Object> sousGroupesTreeItem = createSousGroupesItems();
		TreeItem<Object> formateursTreeItem = createFormateursItems();
		TreeItem<Object> sallesTreeItem = createSallesItems();


		VBox colonneGauche = new EdtVBox();

		TreeView<Object> visualisationTreeView = new TreeView<Object>(visualisationTreeRoot);
		visualisationTreeRoot.getChildren().addAll(sousGroupesTreeItem, formateursTreeItem,  sallesTreeItem);

		Button saveButton = new Button("Sauvegarder solution");
		saveButton.setOnAction( e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if(file != null) {
				solution.save(file);
			}
		});
		
		Button editButton = new Button("Editer solution");
		editButton.setOnAction( e -> {
			editSolution();
		});
	
		Button pdfButton = new Button("Générer PDF");
		pdfButton.setOnAction( e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            System.out.println(job);
            if(job != null){
            	job.showPrintDialog(null); 
            	job.printPage(mainHBox.getChildren().get(1));
            	job.endJob();
            }
		});
		
		/*Button saveButton = new Button("Sauvegarde situation");
		saveButton.setOnAction( e ->{
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if(file != null)
				instance.save(file);
		});*/
		
		colonneGauche.getChildren().addAll(visualisationTreeView, saveButton, pdfButton);
		Label rien = new Label("rien");
		//rien.setStyle("-fx-font: normal bold 20px 'arial' ");
		mainHBox.getChildren().addAll(colonneGauche);
		
		visualisationTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			mainHBox.getChildren().clear();
			mainHBox.getChildren().add(colonneGauche);
			if (newValue.getValue().getClass() == Formateur.class)
				mainHBox.getChildren().add(createFormateurBox(newValue));
			else if (newValue.getValue().getClass() == SousGroupe.class)
				mainHBox.getChildren().add(createSousGroupeBox(newValue));
			else if (newValue.getValue().getClass() == Salle.class)
				mainHBox.getChildren().add(createSalleBox(newValue));
			else if (newValue.getValue().equals("Formateurs"))
				mainHBox.getChildren().add(createFormateursBox(formateursTreeItem));
			else if (newValue.getValue().equals("SousGroupes"))
				mainHBox.getChildren().add(createSousGroupesBox(sousGroupesTreeItem));
			else if (newValue.getValue().equals("Salles"))
				mainHBox.getChildren().add(createSallesBox(sallesTreeItem));
			/*else
				mainHBox.getChildren().add(new Label("autre"));*/
		});
	}

	/***
	 * 
	 */
	private void editSolution() {
		
	}

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
	 * crée la table d'un formateur
	 * @param formateur
	 * @return
	 */
	private VBox createFormateurBox(TreeItem<Object> formateurTreeItem) {
		VBox formateurBox = new EdtVBox();
		Formateur formateur = (Formateur)formateurTreeItem.getValue();
		int indiceFormateur = 0;
		while(getInstance().getFormateur(indiceFormateur) != formateur)
			indiceFormateur++;
		GridPane grille = createGrilleEdt(formateur.getName()) ;
		for(int i = 0 ; i < 20; i++) {
			grille.add(createCaseFormateur(solution.getSolutionPourFormateurs(indiceFormateur, i)), 1 + i / 4, 1 + i % 4);
		}
		formateurBox.getChildren().add(grille);
		return formateurBox;
	}
	
	/***
	 * 
	 * @param title
	 * @return le squelette d'une grille d'EDT
	 */
	GridPane createGrilleEdt(String title) {
		GridPane grille = new GridPane();
		//grille.setPrefSize(300, 300);
		ColumnConstraints[] contrainteColonnes  = new ColumnConstraints[6];
		for(int i = 0; i < 6; i++) {
			contrainteColonnes[i] = new ColumnConstraints();
			contrainteColonnes[i].setPercentWidth(20);
		}
		grille.getColumnConstraints().addAll(contrainteColonnes);
		
		//grille.setStyle("-fx-background-insets:10px; -fx-hgap: 10px ; -fx-vgap:10px;");
		grille.addRow(0,
				new Label(title),
				new Label("Lundi"),
				new Label("Mardi"),
				new Label("Mercredi"),
				new Label("Jeudi"),
				new Label("Vendredi"));
		int i;
		for(i = 1; i < 5; i++) 
			grille.add(new Label("sequence " + i) , 0, i);
		return grille;
	}
	/***
	 * 
	 * @param caseFormateur
	 * @return
	 */
	private VBox createCaseFormateur(CaseEdTFormateur caseFormateur) {
		VBox caseBox = new EdtVBox(10);
		//caseBox.setStyle("-fx-background-insets:10px; -fx-spacing: 10px; -fx-text-alignment:center;");
		if(caseFormateur != null) {
			caseBox.setBackground(new Background(new BackgroundFill(caseFormateur.getDiscipline().getColor(), null, null)));
			/*caseBox.getChildren().addAll(new Label(caseFormateur.getDiscipline().getName()),
				new Label(caseFormateur.getSousGroupe().getName()), new Label(caseFormateur.getSalle().getName()));*/
			caseBox.getChildren().addAll(mode.nameController(caseFormateur.getDiscipline()),
					mode.nameController(caseFormateur.getSousGroupe()), mode.nameController(caseFormateur.getSalle()));
		}
		return caseBox;
	}
	
	/***
	 * Crée le formulaire des formateurs
	 * @param typesSallesTreeItem
	 * @return
	 */
	private VBox createFormateursBox(TreeItem<Object> formateursTreeItem) {
		VBox formateursBox = new EdtVBox();

		return formateursBox;
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
	 * @param groupe
	 * @return
	 */
	private VBox createSousGroupeBox(TreeItem<Object> sousGroupeTreeItem) {
		VBox sousGroupeBox = new EdtVBox();
		SousGroupe sousGroupe = (SousGroupe)sousGroupeTreeItem.getValue();
		int indiceSousGroupe = 0;
		while(getInstance().getSousGroupes().get(indiceSousGroupe) != sousGroupe)
			indiceSousGroupe++;
		GridPane grille = createGrilleEdt(sousGroupe.getName()) ;
		for(int i = 0 ; i < 20; i++) {
			VBox caseBox = new EdtVBox(10);
			CaseEdTSousGroupe caseSousGroupe = solution.getSolutionPourSousGroupes(indiceSousGroupe, i);
			//System.out.println(indiceSousGroupe + " " + i + " " + caseSousGroupe);
			if(caseSousGroupe != null) {
				caseBox.setBackground(new Background(new BackgroundFill(caseSousGroupe.getDiscipline().getColor(), null, null)));
				/*caseBox.getChildren().addAll(new Label(caseSousGroupe.getDiscipline().getName()),
					new Label(caseSousGroupe.getFormateur().getName()), new Label(caseSousGroupe.getSalle().getName()));*/
				caseBox.getChildren().addAll(mode.nameController(caseSousGroupe.getDiscipline()),
						mode.nameController(caseSousGroupe.getFormateur()), mode.nameController(caseSousGroupe.getSalle()));
			}
				grille.add(caseBox, 1 + i / 4, 1 + i % 4);
		}
		sousGroupeBox.getChildren().add(grille);
		return sousGroupeBox;
	}

	/***
	 * Crée la table des sous-groupes
	 * @param SousGroupesTreeItem
	 * @return
	 */
	private VBox createSousGroupesBox(TreeItem<Object> groupesTreeItem) {
		VBox sousGroupesBox = new EdtVBox();

		return sousGroupesBox;
	}

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
	 * crée la table d'une salle
	 * @param salleTreeItem
	 * @return
	 */
	private VBox createSalleBox(TreeItem<Object> salleTreeItem) {
		VBox salleBox = new EdtVBox();
		Salle salle = (Salle)salleTreeItem.getValue();
		int indiceSalle = 0;
		while(getInstance().getSalle(indiceSalle) != salle)
			indiceSalle++;
		GridPane grille = createGrilleEdt(salle.getName()) ;
		for(int i = 0 ; i < 20; i++) {
			VBox caseBox = new EdtVBox(10);
			CaseEdTSalle caseSalle = solution.getSolutionPourSalles(indiceSalle, i);
			//System.out.println(indiceSalle + " " + i + " " + caseSalle);
			if(caseSalle != null) {
				caseBox.setBackground(new Background(new BackgroundFill(caseSalle.getDiscipline().getColor(), null, null)));
				/*caseBox.getChildren().addAll(new Label(caseSalle.getDiscipline().getName()),
					new Label(caseSalle.getSousGroupe().getName()), new Label(caseSalle.getFormateur().getName()));*/
				caseBox.getChildren().addAll(mode.nameController(caseSalle.getDiscipline()),
						mode.nameController(caseSalle.getSousGroupe()), mode.nameController(caseSalle.getFormateur()));

			}
				grille.add(caseBox, 1 + i / 4, 1 + i % 4);
		}
		salleBox.getChildren().add(grille);
		return salleBox;
	}


	/***
	 * Crée la table des salles
	 * @param sallesTreeItem
	 * @return
	 */
	private VBox createSallesBox(TreeItem<Object> sallesTreeItem) {
		VBox sallesBox = new EdtVBox();
		return sallesBox;
	}

	public SolutionEdt getSolution() {
		return solution;
	}

	public void setSolution(SolutionEdt solution) {
		this.solution = solution;
	}

	public HBox getMainHBox() {
		return mainHBox;
	}

	public void setMainHBox(HBox mainHBox) {
		this.mainHBox = mainHBox;
	}

	public Probleme getInstance() {
		return solution.getInstance();
	}

	public void setInstance(Probleme instance) {
		solution.setInstance(instance);
	}

}
