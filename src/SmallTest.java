import applications.tsp.*;
import MIP.*;
import lib.TSPLoader;

public class SmallTest {
	public static void main(String[] args) {
		
		TSPInstance tdata = new TSPInstance(8);
		TSPLoader tspLoader = new TSPLoader(0, tdata);
		
		TwoMatching ex0 = new TwoMatching(tdata, 0);
		TwoMatching ex1 = new TwoMatching(tdata, 1);
		TwoMatching ex2 = new TwoMatching(tdata, 2);
		
		ex0.solve(true);
		ex1.solve(true);
		ex2.solve(true);
		
		double a = ex0.getValue();
		double b = ex1.getValue();
		double c = ex2.getValue();
		
		System.out.println(a+ " " + b + " " + c);
	}
}
