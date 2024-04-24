package MIP;

import java.util.ArrayList;
import applications.tsp.TSPInstance;
import bench.StdDraw;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import bench.Point;

public abstract class PLNE {
	
	private int nbVertex;
	private int nbNodes;
	private double minXCoord;
	private double maxXCoord;
	private double minYCoord;
	private double maxYCoord;
	private double CPUTime;
	private double value;
	private double[] xcoord;
	private double[] ycoord;
	private double[][] weights;
	private double[][] res;
	private Point[] coord;
	IloCplex cplex;
	private ArrayList<IloNumVar[]> varList1D;
	private ArrayList<IloNumVar[][]> varList2D;
	
	public PLNE(TSPInstance tdata) {
		nbVertex = tdata.getNbCities();
		coord = new Point[nbVertex];
		xcoord = new double[nbVertex];
		ycoord = new double[nbVertex];
		weights = new double[nbVertex][nbVertex];
		res = new double[nbVertex][nbVertex];
		varList1D = new ArrayList<IloNumVar[]>();
		varList2D = new ArrayList<IloNumVar[][]>();
		for (int i = 0; i < nbVertex; i++) {
        	xcoord[i] = tdata.getCityPoint(i).getX();
        	ycoord[i] = tdata.getCityPoint(i).getY();
        }
		for (int i = 0; i < nbVertex; i++) {
        	for (int j = 0; j < nbVertex; j++) {
        		weights[i][j] = tdata.getDist(i, j);
        	}
        }
		for (int i = 0; i < nbVertex; i++) {
			coord[i] = tdata.getCityPoint(i);
		}
		minXCoord = getMin(xcoord, nbVertex);
		maxXCoord = getMax(xcoord, nbVertex);
		minYCoord = getMin(ycoord, nbVertex);
		maxYCoord = getMax(ycoord, nbVertex);
		try {
			cplex = new IloCplex();
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
	
	public PLNE(int n) {
		TSPInstance tdata = new TSPInstance(n);
		tdata.genereateRandom(0, true);
		nbVertex = tdata.getNbCities();
		xcoord = new double[nbVertex];
		ycoord = new double[nbVertex];
		weights = new double[nbVertex][nbVertex];
		res = new double[nbVertex][nbVertex];
		varList1D = new ArrayList<IloNumVar[]>();
		varList2D = new ArrayList<IloNumVar[][]>();
		for (int i = 0; i < nbVertex; i++) {
        	xcoord[i] = tdata.getCityPoint(i).getX();
        	ycoord[i] = tdata.getCityPoint(i).getY();
        }
		for (int i = 0; i < nbVertex; i++) {
        	for (int j = 0; j < nbVertex; j++) {
        		weights[i][j] = tdata.getDist(i, j);
        	}
        }
		minXCoord = getMin(xcoord, nbVertex);
		maxXCoord = getMax(xcoord, nbVertex);
		minYCoord = getMin(ycoord, nbVertex);
		maxYCoord = getMax(ycoord, nbVertex);
		try {
			cplex = new IloCplex();
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
	

	public abstract void cplexAddVars();
	public abstract void cplexAddObj();
	public abstract void cplexAddConds();
	
	public void cplexAddCst(double val, IloNumVarType type) {
		try {
			IloNumVar c[] = cplex.numVarArray(1, val, val, type);
			varList1D.add(c);
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	/* Add a variable of length n in dimension 1 or 2 of the type chosen. */
	public void cplexAddVar(int n, int dim, String type) {
		try {
			if (dim == 1) {
				if (type == "Bool") {
					IloNumVar x[] = cplex.boolVarArray(n);
					varList1D.add(x);
				} else if (type == "Float") {
					IloNumVar x[] = cplex.numVarArray(n, 0, 1, IloNumVarType.Float);
					varList1D.add(x);
				} else if (type == "Int") {
					IloNumVar x[] = cplex.numVarArray(n, 0, Integer.MAX_VALUE, IloNumVarType.Int);
					varList1D.add(x);
				} else if (type == "Double") {
					IloNumVar x[] = cplex.numVarArray(n, 0, Double.MAX_VALUE, IloNumVarType.Float);
					varList1D.add(x);
				} else {
					System.out.println("Wrong type.");
				}
			} else if (dim == 2) {
        		IloNumVar[][] x = new IloNumVar[n][n];
				if (type == "Bool") {
	            	for (int i = 0; i < nbVertex; i++) {
	                	x[i] = cplex.boolVarArray(nbVertex);
	            	}
					varList2D.add(x);
				} else if (type == "Float") {
					for (int i = 0; i < nbVertex; i++) {
	                	x[i] = cplex.numVarArray(nbVertex, 0, 1, IloNumVarType.Float);
	                }
					varList2D.add(x);
				} else if (type == "Int") {
					for (int i = 0; i < nbVertex; i++) {
	                	x[i] = cplex.numVarArray(nbVertex, 0, Integer.MAX_VALUE, IloNumVarType.Int);
	                }
					varList2D.add(x);
				} else {
					System.out.println("Wrong type.");
				}
			} else {
				System.out.println("Wrong dimension.");
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public void cplexAddCond(String type, IloNumVar x, double d) {
		try {
			if (type == "Ge") {
				cplex.addGe(x, d);
			} else if (type == "Le") {
				cplex.addLe(x, d);
			} else if (type == "Eq") {
				cplex.addEq(x, d);
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public void cplexAddCond(String type, IloNumVar x, IloNumVar y) {
		try {
			if (type == "Ge") {
				cplex.addGe(x, y);
			} else if (type == "Le") {
				cplex.addLe(x, y);
			} else if (type == "Eq") {
				cplex.addEq(x, y);
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public void cplexAddCond(String type, IloLinearNumExpr x, double d) {
		try {
			if (type == "Ge") {
				cplex.addGe(x, d);
			} else if (type == "Le") {
				cplex.addLe(x, d);
			} else if (type == "Eq") {
				cplex.addEq(x, d);
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public void cplexAddCond(String type, IloLinearNumExpr x, IloLinearNumExpr y) {
		try {
			if (type == "Ge") {
				cplex.addGe(x, y);
			} else if (type == "Le") {
				cplex.addLe(x, y);
			} else if (type == "Eq") {
				cplex.addEq(x, y);
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public void cplexAddCond(String type, IloLinearNumExpr x, IloNumVar y) {
		try {
			if (type == "Ge") {
				cplex.addGe(x, y);
			} else if (type == "Le") {
				cplex.addLe(x, y);
			} else if (type == "Eq") {
				cplex.addEq(x, y);
			}
		} catch (IloException e) {
            e.printStackTrace();
		}
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double val) {
		value = val;
	}
	
	public double getCPUTime() {
		return CPUTime;
	}	
	
	public void setCPUTime(double time) {
		CPUTime = time;
	}
	
	public int getNbVertex() {
		return nbVertex;
	}
	
	public double getWeight(int i, int j) {
		return weights[i][j];
	}
	
	public void setWeight(int i, int j, double val) {
		weights[i][j] = val;
	}
	
	public double getRes(int i, int j) {
		return res[i][j];
	}
	
	public void setRes(int i, int j, double val) {
		res[i][j] = val;
	}
	
	public double getXCoord(int i) {
		return xcoord[i];
	}
	
	public void setXCoord(int i, double val) {
		xcoord[i] = val;
	}
	
	public double getYCoord(int i) {
		return ycoord[i];
	}
	
	public void setYCoord(int i, double val) {
		ycoord[i] = val;
	}
	
	public double getMinXCoord() {
		return minXCoord;
	}
	
	public double getMaxXCoord() {
		return maxXCoord;
	}
	
	public double getMinYCoord() {
		return minYCoord;
	}
	
	public double getMaxYCoord() {
		return maxYCoord;
	}
	
	public int getNbNodes() {
		return nbNodes;
	}
	
	public IloNumVar getVar1D(int indice, int i) {
		return varList1D.get(indice)[i];
	}
	
	public IloNumVar getVar2D(int indice, int i, int j) {
		return varList2D.get(indice)[i][j];
	}
	
	private double getMax(double[] list, int n) {
		double max = list[0];
		for (int i = 1; i < n; i++) {
        	if (list[i] > max) {
        		max = list[i];
        	}
		}
		return max;
	}
	
	private double getMin(double[] list, int n) {
		double min = list[0];
		for (int i = 1; i < n; i++) {
        	if (list[i] < min) {
        		min = list[i];
        	}
		}
		return min;
	}
	
	public Point getCoord(int i) {
		return coord[i];
	}
	
	public boolean solve(boolean add) {
		if (add) {
			cplexAddVars();
			cplexAddObj();
			cplexAddConds();
		}
		try {
			if(cplex.solve()) {
				CPUTime = cplex.getCplexTime();
	        	value = cplex.getObjValue() ;
	            for (int i = 0; i < nbVertex; i++) {
	            	for (int j = 0; j < nbVertex; j++) {
	            		if (i!=j) {
		            		if (cplex.getValue(getVar2D(0, i, j)) > 0) {
		            			res[i][j] = cplex.getValue(getVar2D(0, i, j));
		            		}
	            		} else {
	            			res[i][j] = 0;
	            		}
	            	}
	            }
	        }
			this.nbNodes = cplex.getIncumbentNode();
			return true;
		} catch (IloException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/* Show the result graph. */
	public void show() {
		StdDraw.setXscale(minXCoord, maxXCoord);
        StdDraw.setYscale(minYCoord, maxYCoord);
    	for (int i = 0; i < nbVertex; i++) {
        	for (int j = 0; j<nbVertex; j++) {
        		if (i!=j) {
    				if (res[i][j] > 0) {
    					//System.out.println(i + " " + j + " " + res[i][j] + " " + getWeight(i, j));
        				StdDraw.setPenRadius(0.005);
        		        StdDraw.setPenColor(StdDraw.BLUE);
        				StdDraw.point(xcoord[i], ycoord[i]);
        				StdDraw.point(xcoord[j], ycoord[j]);
        				StdDraw.setPenRadius(res[i][j]*res[i][j]*0.0005);
        		        StdDraw.setPenColor(StdDraw.BLACK);
        				StdDraw.line(xcoord[i], ycoord[i], xcoord[j], ycoord[j]);
    				}
        		}
        	}
    	}
	}
	
	public void end() {
		cplex.end();
	}
}
