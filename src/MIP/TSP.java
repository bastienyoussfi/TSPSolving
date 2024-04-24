package MIP;

import applications.tsp.TSPInstance;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;

public class TSP extends PLNE {
	
	public TSP(TSPInstance tdata) {
		super(tdata);
	}
	
	@Override
	public void cplexAddVars() {
		int nbVertex = getNbVertex();
		cplexAddVar(nbVertex, 2, "Bool");
		cplexAddVar(nbVertex, 1, "Double");
	}
	
	@Override
	public void cplexAddObj() {
		int nbVertex = getNbVertex();
		try {
			IloLinearNumExpr obj = cplex.linearNumExpr();            
            for (int i = 0; i < nbVertex; i++) {
            	for (int j = 0; j < nbVertex; j++) {
            		if (i!=j) {
            			obj.addTerm(getWeight(i, j), getVar2D(0, i, j));
            		}
            	}
            }            
            cplex.addMinimize(obj);            
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public void cplexAddConds() {
		int nbVertex = getNbVertex();
		try {
			for (int j = 0; j < nbVertex; j++) {
            	IloLinearNumExpr expr = cplex.linearNumExpr();
            	for (int i = 0; i < nbVertex; i++) {
            		if (i!=j) {
            			expr.addTerm(1.0, getVar2D(0, i, j));
            		}
            	}
                cplexAddCond("Eq", expr, 1.0);
            }                        
            for (int i = 0; i < nbVertex; i++) {
            	IloLinearNumExpr expr = cplex.linearNumExpr();
            	for (int j = 0; j < nbVertex; j++) {
            		if (i!=j) {
            			expr.addTerm(1.0, getVar2D(0, i, j));
            		}
            	}
            	cplexAddCond("Eq", expr, 1.0);
            }
            for (int i = 1; i < nbVertex; i++) {
            	for (int j = 1; j < nbVertex; j++) {
            		if (i!=j) {
            			IloLinearNumExpr expr = cplex.linearNumExpr();
            			expr.addTerm(1.0, getVar1D(0, i));
            			expr.addTerm(-1.0, getVar1D(0, j));
            			expr.addTerm(nbVertex-1, getVar2D(0, i, j));
            			cplexAddCond("Le", expr, nbVertex-2);
            		}
            	}
            }
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
}