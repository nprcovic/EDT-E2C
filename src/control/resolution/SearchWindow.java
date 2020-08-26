package control.resolution;

import control.consultation.ConsultationEDTPane;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.CaseDomainesSousGroupe;
import model.Probleme;
import model.SolutionEdt;
import model.Solveur;

public class SearchWindow {
	public SearchWindow(Probleme instance, SolutionEdt solutionDeReference, CaseDomainesSousGroupe[][] casesDomaines, String path) {

		HBox searchPane = new HBox();
		searchPane.getChildren().add(new Label("Recherche en cours..."));
		Scene searchScene = new Scene(searchPane);
		searchScene.getStylesheets().add("application/application.css");//"src/application/application.css");

		Stage searchWindow = new Stage();
		searchWindow.setWidth(Screen.getPrimary().getBounds().getWidth() * 2 / 3);
	    searchWindow.setHeight(Screen.getPrimary().getBounds().getHeight() * 2 / 3);
	    searchWindow.centerOnScreen();
		searchWindow.setTitle("EDT");
		searchWindow.setScene(searchScene);
		searchWindow.show();

		Solveur solveur = new Solveur(instance, solutionDeReference, casesDomaines);
		final Service<Void> calculateService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                    	try {
                    		solveur.LDS();
                    		solveur.printSolution();
                    	}
                    	catch(Exception e) {
                    		e.printStackTrace();
                    	}
            			return null;
                    }
                };
            }
        };

        calculateService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:System.out.println("Failed");
                case CANCELLED:System.out.println("Cancelled");
                case SUCCEEDED:
                    /*scene.setCursor(oldCursor);
                    calculateItem.setDisable(false);
                    calculateButton.setDisable(false);*/
        			searchPane.getChildren().clear();
        			if(solveur.getSolutionEDT() == null)
        				searchPane.getChildren().add(new Label("Pas de solution"));
        			else {
        				searchPane.getChildren().add(new ConsultationEDTPane(solveur.getSolutionEDT()));
        				solveur.saveSolution(solveur.getSolutionEDT(), path);
        			}
                	System.out.println("Thread terminé");
                    break;
            }
        });
        calculateService.start();
	}

}
