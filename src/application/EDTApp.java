package application;

import java.io.File;

import control.EDTMainPane;
import control.preferences.PreferenceEDTPane;
import control.preferences.Preferences;
import control.resolution.ResolutionEDTPane;
import control.situation.SituationPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Probleme;

public class EDTApp extends Application {

	private Probleme instance;
	private Preferences preferences;
	
	private Stage stage;
	private Region mainPane;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			MenuBar menuBar = createMenuBar();
			preferences = Preferences.load();
			if(preferences.getDirectory() != null)
				instance = Probleme.load(preferences.getDirectory().getAbsolutePath() + "/derniere_situation");
			else
				instance = new Probleme();
			stage = primaryStage;
			mainPane =  new SituationPane(this);
			
			VBox vbox = new VBox();
			vbox.getChildren().addAll(menuBar, mainPane);
		    Scene scene = new Scene(vbox);
		    primaryStage.setTitle("Emploi du temps E2C");
		    //System.out.println(getClass().getResource("application/application.css").toExternalForm());
			scene.getStylesheets().add("application/application.css");//"src/application/application.css");
		    //primaryStage.setWidth(Screen.getPrimary().getBounds().getWidth() * 2 / 3);
			primaryStage.setWidth(preferences.getWindowWidth());
		    primaryStage.setHeight(preferences.getWindowHeight());
		    primaryStage.centerOnScreen();
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private MenuBar createMenuBar() {
		
		Menu modeMenu = new Menu("Mode");
		MenuItem parametrageSituationMenuItem = new MenuItem("Paramétrer une situation");
		parametrageSituationMenuItem.setOnAction(e -> {
			VBox vbox = (VBox) mainPane.getParent();
			vbox.getChildren().remove(mainPane);
			mainPane = new SituationPane(this);
			vbox.getChildren().add(mainPane);
		});
		
		MenuItem resolutionMenuItem = new MenuItem("Calculer une solution");
		resolutionMenuItem.setOnAction(e -> {
			instance.save(preferences.getDirectory().getAbsolutePath() + "/derniere_situation");
			VBox vbox = (VBox) mainPane.getParent();
			vbox.getChildren().remove(mainPane);
			mainPane = new ResolutionEDTPane(this);
			vbox.getChildren().add(mainPane);
		});
		
		MenuItem preferencesMenuItem = new MenuItem("Préférences");
		preferencesMenuItem.setOnAction(e -> {
			VBox vbox = (VBox) mainPane.getParent();
			vbox.getChildren().remove(mainPane);
			mainPane = new PreferenceEDTPane(this);
			vbox.getChildren().add(mainPane);
		});
		
		MenuItem quitterMenuItem = new MenuItem("Quitter");
		quitterMenuItem.setOnAction(e -> {
			preferences.setWindowHeight((int) stage.getHeight());
			preferences.setWindowWidth((int) stage.getWidth());			
			preferences.save();
			System.exit(0);
		});

		modeMenu.getItems().addAll(parametrageSituationMenuItem, resolutionMenuItem, preferencesMenuItem, quitterMenuItem);

		Menu fileMenu = new Menu("Fichier");
		MenuItem loadSituationMenuItem = new MenuItem("Charger une situation");
		loadSituationMenuItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if(file != null) {
				instance = Probleme.load(file);
				//((EDTMainPane)mainPane).setInstance(instance);
				((EDTMainPane)mainPane).initWindowContents();
			}
		});
		MenuItem saveSituationMenuItem = new MenuItem("Sauvegarder une situation");
		saveSituationMenuItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if(file != null)
				getInstance().save(file);
		});
		
		fileMenu.getItems().addAll(loadSituationMenuItem, saveSituationMenuItem);
		
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(modeMenu, fileMenu);
		return menuBar;
	}

	public Probleme getInstance() {
		return instance;
	}

	public void setInstance(Probleme instance) {
		this.instance = instance;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
}
