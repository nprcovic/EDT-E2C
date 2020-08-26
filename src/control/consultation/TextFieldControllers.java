package control.consultation;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import model.Formateur;
import model.Discipline;
import model.Salle;
import model.SousGroupe;

public class TextFieldControllers implements NameControllers {

	@Override
	public Control nameController(Formateur formateur) {
		TextField nameTextField = new TextField(formateur.getName());
		/*nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> formateur.setName(observable.getValue()));*/
		return nameTextField;
	}

	@Override
	public Control nameController(SousGroupe sousGroupe) {
		TextField nameTextField = new TextField(sousGroupe.getName());
		/*nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> formateur.setName(observable.getValue()));*/
		return nameTextField;
	}

	@Override
	public Control nameController(Salle salle) {
		TextField nameTextField = new TextField(salle.getName());
		/*nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> formateur.setName(observable.getValue()));*/
		return nameTextField;
	}

	@Override
	public Control nameController(Discipline discipline) {
		TextField nameTextField = new TextField(discipline.getName());
		/*nameTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> formateur.setName(observable.getValue()));*/
		return nameTextField;
	}

}
