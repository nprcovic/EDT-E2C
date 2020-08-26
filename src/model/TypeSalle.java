package model;

import java.io.Serializable;
import java.util.Observable;


public class TypeSalle  extends Observable implements Serializable{
	/**
	 * Observé par Salle
	 */

	private static final long serialVersionUID = -6914023725272721089L;
	
	private String name;
	private Boolean partageable;
	
	
	
	public TypeSalle(String name, Boolean partageable) {
		super();
		this.name = name;
		this.partageable = partageable;
	}

	
	public TypeSalle() {
		this("", false);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getPartageable() {
		return partageable;
	}

	public void setPartageable(Boolean partageable) {
		this.partageable = partageable;
	}

	@Override
	public String toString() {
		//return "TypeSalle [name=" + name + ", partageable=" + partageable + "]";
		return getName();
	}

}
