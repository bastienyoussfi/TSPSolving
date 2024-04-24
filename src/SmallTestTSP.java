import applications.tsp.*;
import MIP.*;
import lib.*;

public class SmallTestTSP {
	public static void main(String[] args) {
		
		TSPInstance tdata = new TSPInstance(8);
		TSPLoader tspLoader = new TSPLoader(0, tdata);
		
		TSP ex = new TSP(tdata);
		
		ex.solve(true);
		ex.show();
		
		System.out.println(ex.getValue());
	}
}
