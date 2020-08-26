package model;

import java.io.Serializable;

public class Difference implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2371906143461171171L;
	private CaseEdTSousGroupe creneau1;
    private CaseEdTSousGroupe creneau2;
    private int numSol1;
    private int numSol2;
    private int numeroSequence;

    public Difference(CaseEdTSousGroupe creneau1, CaseEdTSousGroupe creneau2, int numSol1, int numSol2, int numeroSequence) {
        this.creneau1 = creneau1;
        this.creneau2 = creneau2;
        this.numSol1 = numSol1;
        this.numSol2 = numSol2;
        this.numeroSequence = numeroSequence;
    }

    @Override
    public String toString() {
        return  this.numeroSequence +"\nSolution: " + numSol1 + "  - " +
        		(creneau1 == null ? "*" : creneau1.getDiscipline()) + ": " + (creneau1 == null ? "*" : creneau1.getSalle()) + " avec " + (creneau1 == null ? "*" : creneau1.getFormateur()) +
                "\nSolution: " + numSol2 + "  - " +
                (creneau2 == null ? "*" : creneau2.getDiscipline()) + ": " + (creneau2 == null ? "*" : creneau2.getSalle()) + " avec " + (creneau2 == null ? "*" : creneau2.getFormateur());
    }
}
