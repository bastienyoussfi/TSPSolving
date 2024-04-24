import applications.tsp.*;
import MIP.*;

public class TestTSP {
		
	public static void main(String[] args) {
				
		ParseurTSP p = new ParseurTSP("att48.tsp");
		TSPInstance tdata = p.parse();
		
		TSP ex0 = new TSP(tdata);
		
		ex0.solve(true);
		
		double a = ex0.getValue();
		
		System.out.println(a + "   Noeuds:  " + ex0.getNbNodes());
	}
}