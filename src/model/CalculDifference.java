package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class CalculDifference implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1003883433878539116L;
	private CaseEdTSousGroupe[][] edt;
    private int nbSousGroupes;
    private int nbSolutions;
    private ArrayList<HashSet<Difference>> resultat;
    //private ArrayList<Creneau> creneaux;

    public CalculDifference(CaseEdTSousGroupe[][] edt, int nbSousGroupes, int nbSolutions) {
        this.edt = edt;
        this.nbSousGroupes = nbSousGroupes;
        this.nbSolutions = nbSolutions;
        resultat = new ArrayList<>(nbSousGroupes);
        for (int i = 0; i < nbSousGroupes; i++) {
            resultat.add(new HashSet<>());
        }
    }

    public void calcul(){
        for (int i = 0; i < 20; i++) {
            for(int l = 0; l < nbSousGroupes; l++) {
                for (int j = 0; j < nbSolutions-1; j++) {
                    for (int k = j + 1; k < nbSolutions; k++) {
                        if ( edt[j * nbSousGroupes + l][i] != null && edt[k * nbSousGroupes + l][i] != null) {
                            if (!edt[j * nbSousGroupes + l][i].equals(edt[k * nbSousGroupes + l][i])) {
                                Difference diff = new Difference(edt[j * nbSousGroupes + l][i], edt[k * nbSousGroupes + l][i], j + 1, k + 1, i);
                                resultat.get(l).add(diff);
                            }
                        }
                    }
                }
            }
        }
    }

    public void print(){
        int i = 1;
        for(HashSet<Difference> liste : resultat){
            System.out.println("Diff�rence entre les solutions:");
            System.out.println("SousGroupe: " + i++);
            for(Difference diff : liste){
                System.out.println(diff);
            }
        }
    }
}
