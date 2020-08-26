package control.preferences;

import java.io.File;
import java.io.IOException;

import application.EDTApp;
import control.EDTMainPane;
import control.EdtHBox;
import control.EdtVBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.Discipline;
import model.Formateur;
import model.Groupe;
import model.Probleme;
import model.ProgrammeActivites;
import model.Salle;
import model.SousGroupe;
import model.TypeSalle;

public class PreferenceEDTPane extends ScrollPane implements EDTMainPane {

	private EDTApp app;
	private VBox mainVBox = new EdtVBox();

	public PreferenceEDTPane(EDTApp app) {
		this.app = app;
		initWindowContents();

	}

	@Override
	public void initWindowContents() {
		mainVBox.getChildren().clear();
		setContent(mainVBox);
		
		Button directoryButton = new Button("Choisir le répertoire de sauvegarde");
		directoryButton.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File file = directoryChooser.showDialog(null);
			if(file != null) {
				app.getPreferences().setDirectory(file);
				app.getPreferences().save();
			}
			
		});
		mainVBox.getChildren().addAll(directoryButton);
	}

}
