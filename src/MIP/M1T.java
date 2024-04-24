package MIP;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import applications.tsp.TSPInstance;
import bench.Point;
import bench.StdDraw;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;

public class M1T extends PLNE {
	
	private TwoMatching graph;
	private Point[] coord;
	private int nbCut;
	private double[][][] currentRes;
	private double[][] currentCut;
	
	public M1T(TSPInstance tdata) {
		super(tdata);
		int n = tdata.getNbCities();
		coord = new Point[n];
		TwoMatching graph2 = new TwoMatching(tdata, 1);
		graph = graph2;
		for (int i = 0; i < n; i++) {
			coord[i] = getCoord(i);
		}
		nbCut = 0;
	}
	
	public void solveCutPlane() {
		try {
			int nbVertex = getNbVertex();
			int card;
			currentRes = new double[100000][nbVertex][nbVertex];
			currentCut = new double[100000][nbVertex];
			double[][] printList = new double[nbVertex][nbVertex];
			ArrayList<Integer> indices = new ArrayList<Integer>();
			boolean cut = true;
			int k = 0;
			int x = 0;
			int y = 0;
			while (cut) {
				// On résout le 2-matching fractionnaire
				if (k == 0) {
					graph.solve(true);
				} else {
					for (int i = 0; i < nbVertex; i++) {
						for (int j = 0; j < nbVertex; j++) {
							graph.setRes(i, j, 0);
						}
					}
					graph.solve(false);
					for (int i = 0; i < nbVertex; i++) {
						for (int j = 0; j < nbVertex; j++) {
						}
					}
				}
				
				for (int i = 0; i < nbVertex; i++) {
					for (int j = 0; j < nbVertex; j++) {
						currentRes[k][i][j] = graph.getRes(i, j);
						if (graph.getRes(i, j) > 0) {
			    			 printList[i][j] = graph.getRes(i, j);
			    		} else {
			    			printList[i][j] = 0;
			    		}
					}
				}
				
				// On regarde si une coupe existe
				TSPInstance tdata = new TSPInstance(nbVertex);
				tdata.setCoordinates(coord);
				for (int i = 0; i < nbVertex; i++) {
					for (int j = 0; j < nbVertex; j++) {
						tdata.setDistance(i, j, 1);
					}
				}
				MinCut coupe = new MinCut(tdata, printList);
				coupe.solve(true);
				
				// On regarde si on a une coupe min de capacité inférieure à 2
				if (coupe.getValue() < 2) {
					nbCut++;
					card = 0;
					IloLinearNumExpr expr = cplex.linearNumExpr();
					for (int i = 0; i < nbVertex; i++) {
						// Ajoute tous les sommets de la coupe dans la liste indices
						if (coupe.cplex.getValue(coupe.getVar1D(0, i)) > 0) {
							currentCut[k][i] = 1;
							card++;
							indices.add(i);
						} else {
							currentCut[k][i] = 0;
						}
					}
					// On ajoute des conditions sur les sommets qui violent la condition
					for (int i = 0; i < card; i++) {
						x = indices.get(i);
						for (int j = i; j < card; j++) {
							y = indices.get(j);
							if (x!=y) {
								expr.addTerm(1.0, graph.getVar2D(0, x, y));
							}
						}
					}
					graph.cplexAddCond("Le", expr, card - 1);
					coupe.end();
					indices.clear();
				} else {
					cut = false;
				}
				k++;
			}
			setValue(graph.getValue());
			graph.end();
		} catch (IloException e) {
            e.printStackTrace();
        }
	}
	
	public double getNbCut() {
		return nbCut;
	}
	
	@Override
	public void show() {
		graph.show();
	}
	
	public void showSteps() {
		try {
			for (int s = 0; s < nbCut + 1; s++) {
				StdDraw.setXscale(getMinXCoord(), getMaxXCoord());
		        StdDraw.setYscale(getMinYCoord(), getMaxYCoord());
		    	for (int i = 0; i < getNbVertex(); i++) {
		        	for (int j = 0; j<getNbVertex(); j++) {
		        		if (i!=j) {
		    				if (currentRes[s][i][j] > 0) {
		        				if (currentCut[s][i] > 0) {
		        					StdDraw.setPenRadius(0.015);
		        					StdDraw.setPenColor(StdDraw.RED);
		        				} else {
		        					StdDraw.setPenRadius(0.005);
		        					StdDraw.setPenColor(StdDraw.BLUE);
		        				}
		        				StdDraw.point(graph.getXCoord(i), graph.getYCoord(i));
		        				StdDraw.point(graph.getXCoord(j), graph.getYCoord(j));
		        				StdDraw.setPenRadius(currentRes[s][i][j]*currentRes[s][i][j]*0.0005);
		        		        StdDraw.setPenColor(StdDraw.BLACK);
		        				StdDraw.line(graph.getXCoord(i), graph.getYCoord(i), graph.getXCoord(j), graph.getYCoord(j));
		    				}
		        		}
		        	}
		    	}
				TimeUnit.SECONDS.sleep(2);
				if (s < nbCut) {
					StdDraw.clear();
				}
			}
		} catch (InterruptedException e) {
            e.printStackTrace(); 
        }
	}
	
	@Override
	public void cplexAddVars() {
	}

	@Override
	public void cplexAddObj() {
	}

	@Override
	public void cplexAddConds() {
	}
}