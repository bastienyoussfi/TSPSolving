import applications.tsp.*;
import MIP.*;
import lib.TSPLoader;

public class SmallTestM1T {
	public static void main(String[] args) {
		
		TSPInstance tdata = new TSPInstance(8);
		TSPLoader tspLoader = new TSPLoader(0, tdata);
		
		M1T ex = new M1T(tdata);
		
		ex.solveCutPlane();
		System.out.println(ex.getValue() + "  " + ex.getNbCut() + " " + ex.getNbNodes());
		ex.showSteps();
	}
}
