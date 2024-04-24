import applications.tsp.*;
import MIP.*;

public class TestM1T {

	public static void main(String[] args) {
		
		ParseurTSP p = new ParseurTSP("att48.tsp");
		TSPInstance tdata = p.parse();
		
		M1T ex = new M1T(tdata);
		
		ex.solveCutPlane();
		System.out.println(ex.getNbCut());
		ex.showSteps();
	}
}