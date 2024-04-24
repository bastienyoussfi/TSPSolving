package MIP;

import applications.tsp.TSPInstance;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVarType;

public class MinCut extends PLNE {
	
	private double[][] coeffs;
	private boolean coeff;
	
	public MinCut(TSPInstance tdata) {
		super(tdata);
		int n = tdata.getNbCities();
		coeffs = new double[n][n];
		for (int i = 0; i<n; i++) {
			for (int j = 0; j<n; j++) {
				coeffs[i][j] = 1;
			}
		}
		coeff = false;
	}
	
	public MinCut(TSPInstance tdata, double[][] coefficients) {
		super(tdata);
		int n = tdata.getNbCities();
		coeffs = new double[n][n];
		for (int i = 0; i<n; i++) {
			for (int j = 0; j<n; j++) {
				coeffs[i][j] = coefficients[i][j];
			}
		}
		coeff = true;
	}
	
	@Override
	public void cplexAddVars() {
		int nbVertex = getNbVertex();
		cplexAddVar(nbVertex, 2, "Bool");
		cplexAddVar(nbVertex, 1, "Bool");
		cplexAddCst(2, IloNumVarType.Int);
	}
	
	@Override
	public void cplexAddObj() {
		int nbVertex = getNbVertex();
		try {
			IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int i = 0; i < nbVertex; i++) {
            	for (int j = i; j < nbVertex; j++) {
            		if (i!=j) {
            			if (coeff) {
            				obj.addTerm(coeffs[i][j], getVar2D(0, i, j));
            			} else {
            				obj.addTerm(getWeight(i, j), getVar2D(0, i, j));
            			}
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
			for (int i = 0; i < nbVertex; i++) {
            	for (int j = 0; j < nbVertex; j++) {
            		if (i!=j) {
                        cplexAddCond("Eq", getVar2D(0, i, j), getVar2D(0, j, i));
            		}
            	}
            }
            for (int j = 0; j < nbVertex; j++) {
            	for (int i = 0; i < nbVertex; i++) {
            		if (i!=j) {
            			IloLinearNumExpr expr = cplex.linearNumExpr();
            			expr.addTerm(-1.0, getVar1D(0, i));
            			expr.addTerm(1.0, getVar1D(0, j));
            			cplexAddCond("Le", expr, getVar2D(0, i, j));
            		}
            	}
            }
            for (int j = 0; j < nbVertex; j++) {
            	for (int i = 0; i < nbVertex; i++) {
            		if (i!=j) {
            			IloLinearNumExpr expr = cplex.linearNumExpr();
            			expr.addTerm(1.0, getVar1D(0, i));
            			expr.addTerm(-1.0, getVar1D(0, j));
            			cplex.addLe(expr, getVar2D(0, i, j));
            		}
            	}
            }
            for (int j = 0; j < nbVertex; j++) {
            	for (int i = 0; i < nbVertex; i++) {
            		if (i!=j) {
            			IloLinearNumExpr expr = cplex.linearNumExpr();
            			expr.addTerm(1.0, getVar1D(0, i));
            			expr.addTerm(1.0, getVar1D(0, j));
            			cplex.addGe(expr, getVar2D(0, i, j));
            		}
            	}
            }
            for (int j = 0; j < nbVertex; j++) {
            	for (int i = 0; i < nbVertex; i++) {
            		if (i!=j) {
            			IloLinearNumExpr expr = cplex.linearNumExpr();
            			expr.addTerm(-1.0, getVar1D(0, i));
            			expr.addTerm(-1.0, getVar1D(0, j));
            			expr.addTerm(1.0, getVar1D(1, 0));
            			cplex.addGe(expr, getVar2D(0, i, j));
            		}
            	}
            }
        	IloLinearNumExpr vertices = cplex.linearNumExpr();
            for (int i = 0; i < nbVertex; i++) {
            	vertices.addTerm(1.0, getVar1D(0, i));
            }
            cplexAddCond("Ge", vertices, 1.0);
            cplexAddCond("Le", vertices, nbVertex - 1);
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
	
	public double getRealValue() {
		int nbVertex = getNbVertex();
		double a = getValue();
		System.out.println(a);
		for (int i = 0; i < nbVertex; i++) {
			for (int j = 0; j < nbVertex; j++) {
				if (i!=j) {
					if (getRes(i, j) > 100000) {
						a = a - 1000000;
					}
					
				}
			}
		}
		return a;
	}
}