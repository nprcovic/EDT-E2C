package control.consultation;

import javafx.scene.control.Control;
import model.Formateur;
import model.Discipline;
import model.Salle;
import model.SousGroupe;

interface NameControllers {
	Control nameController(Formateur formateur);
	Control nameController(SousGroupe sousGroupe);
	Control nameController(Salle salle);
	Control nameController(Discipline discipline);
}
