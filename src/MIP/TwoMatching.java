package MIP;

import applications.tsp.TSPInstance;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;

public class TwoMatching extends PLNE {
	
	int version;
	
	// Two matching MIP, version = 0 : binary formulation, version = 1 : fraction formulation, version = 2 : integer formulation
	
	public TwoMatching(TSPInstance tdata, int vers) {
		super(tdata);
		version = vers;
	}
	
	public TwoMatching(int n, int vers) {
		super(n);
		version = vers;
	}
	
	@Override
	public void cplexAddVars() {
		int nbVertex = getNbVertex();
		if (version == 0) {
			cplexAddVar(nbVertex, 2, "Bool");
		} else if (version == 1) {
			cplexAddVar(nbVertex, 2, "Float");
		} else {
			cplexAddVar(nbVertex, 2, "Int");
		}
	}
	
	@Override
	public void cplexAddObj() {
		try {
			int nbVertex = getNbVertex();
			IloLinearNumExpr obj = cplex.linearNumExpr();
	        
	        for (int i = 0; i < nbVertex; i++) {
	        	for (int j = i; j < nbVertex; j++) {
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
		try {
			int nbVertex = getNbVertex();
			for (int i = 0; i < nbVertex; i++) {
	        	IloLinearNumExpr expr = cplex.linearNumExpr();
	        	for (int j = 0; j < nbVertex; j++) {
	        		if (i!=j) {
	        			expr.addTerm(1.0, getVar2D(0, i, j));
	        		}
	        	}
	            cplexAddCond("Eq", expr, 2.0);
	        }
	        
	        for (int i = 0; i < nbVertex; i++) {
	        	for (int j = 0; j < nbVertex; j++) {
	        		if (i!=j) {
	                    cplexAddCond("Eq", getVar2D(0, i, j), getVar2D(0, j, i));
	        		}
	        	}
	        }
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
}