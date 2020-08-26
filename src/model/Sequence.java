package model;

import java.io.Serializable;

public class Sequence implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5791962837546584820L;
	private Discipline discipline;
	private int numero;
	
	
	public Sequence(Discipline discipline, int numero) {
		super();
		this.discipline = discipline;
		this.numero = numero;
	}

	public Discipline getDiscipline() {
		return discipline;
	}
	public void setDiscipline(Discipline activite) {
		this.discipline = activite;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}

	@Override
	public String toString() {
		return "Sequence [discipline=" + discipline + ", numero=" + numero + "]";
	}
	
	
}
