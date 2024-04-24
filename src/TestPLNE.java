import applications.tsp.*;
import MIP.*;

public class TestPLNE {
		
	public static void main(String[] args) {
				
		ParseurTSP p = new ParseurTSP("rd400.tsp");
		TSPInstance tdata = p.parse();		
		
		TwoMatching ex0 = new TwoMatching(tdata, 1);
		TwoMatching ex1 = new TwoMatching(tdata, 1);
		TwoMatching ex2 = new TwoMatching(tdata, 2);
		
		ex0.solve(true);
		ex1.solve(true);
		ex2.solve(true);
		
		double a = ex0.getValue();
		double b = ex1.getValue();
		double c = ex2.getValue();
		
		System.out.println(a + " " + b + " " + c + "   Noeuds:  " + ex0.getNbNodes());
		
		ex0.show();
	}
}