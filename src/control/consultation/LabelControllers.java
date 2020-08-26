package control.consultation;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import model.Formateur;
import model.Discipline;
import model.Salle;
import model.SousGroupe;

public class LabelControllers implements NameControllers {

	@Override
	public Control nameController(Formateur formateur) {
		return new Label(formateur.getName());
	}

	@Override
	public Control nameController(SousGroupe sousGroupe) {
		return new Label(sousGroupe.getName());
	}

	@Override
	public Control nameController(Salle salle) {
		return new Label(salle.getName());
	}

	@Override
	public Control nameController(Discipline discipline) {
		return new Label(discipline.getName());
	}
}
