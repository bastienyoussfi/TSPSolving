package lib;

import applications.tsp.TSPInstance;
import bench.Point;

public class TSPLoader {
	
	private int inf = 1000000;
	
	public TSPLoader(int n, TSPInstance tdata) {
		if (n == 0) {
			assert tdata.getNbCities() == 8;
			// Exemple du livre sur 8 sommets
			Point coord[] = {new Point(0, 1), new Point(1.75, 2), new Point(1.75, 0), new Point(3.5, 1), new Point(5.5, 1), new Point(7.25, 2), new Point(7.25, 0), new Point(9, 1)};
			int weights[][] = {{0, 1, 1, inf, inf, inf, inf, inf}, {1, 0, 1, 2, inf, 5, inf, inf}, {1, 1, 0, 2, inf, inf, 4, inf}, {inf, 2, 2, 0, 1, inf, inf, inf}, {inf, inf, inf, 1, 0, 2, 2, inf}, {inf, 5, inf, inf, 2, 0, 1, 1}, {inf, inf, 4, inf, 2, 1, 0, 1}, {inf, inf, inf, inf, inf, 1, 1, 0}};
			tdata.setCoordinates(coord);
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		} else if (n == 1) {
			assert tdata.getNbCities() == 6;
			// Exemple sur K6
			Point coord[] = {new Point(1, 3), new Point(4, 3), new Point(0, 1.5), new Point(5, 1.5), new Point(1, 0), new Point(4, 0)};
			int weights[][] = {{0, 12, 29, 22, 13, 24}, {12, 0, 19, 3, 25, 6}, {29, 19, 0, 21, 23, 28}, {22, 3, 21, 0, 4, 5}, {13, 25, 23, 4, 0, 16}, {24, 6, 28, 5, 16, 0}};
			
			tdata.setCoordinates(coord);
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		} else if (n == 2) {
			assert tdata.getNbCities() == 5;
			// Exemple sur K5
			Point coord[] = {new Point(1.5, 2), new Point(0, 1), new Point(3, 1), new Point(0.3, 0), new Point(2.7, 0)};
			int weights[][] = {{0, 14, 15, 4, 9}, {14, 0, 18, 5, 13}, {15, 18, 0, 19, 10}, {4, 5, 19, 0, 12}, {9, 13, 10, 12, 0}};
			
			tdata.setCoordinates(coord);
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		} else if (n == 3) {
			assert tdata.getNbCities() == 5;
			// Autre exemple sur K5
			Point coord[] = {new Point(1.5, 2), new Point(0, 1), new Point(3, 1), new Point(0.3, 0), new Point(2.7, 0)};
			int weights[][] = {{0, 12, 10, 19, 8}, {12, 0, 3, 7, 2}, {10, 3, 0, 6, 20}, {19, 7, 6, 0, 4}, {8, 2, 20, 4, 0}};
			
			tdata.setCoordinates(coord);
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		} else if (n == 4) {
			assert tdata.getNbCities() == 4;
			// Exemple sur K4
			Point coord[] = {new Point(1, 2), new Point(0, 1), new Point(2, 1), new Point(1, 0)};
			int weights[][] = {{0, 12, 14, 17}, {12, 0, 15, 18}, {14, 15, 0, 29}, {17, 18, 29, 0}};
			
			tdata.setCoordinates(coord);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		} else if (n == 5) {
			assert tdata.getNbCities() == 4;
			// Exemple sur K4
			Point coord[] = {new Point(1, 2), new Point(0, 1), new Point(2, 1), new Point(1, 0)};
			int weights[][] = {{0, 10, 15, 20}, {10, 0, 35, 25}, {15, 35, 0, 30}, {20, 25, 30, 0}};
			
			tdata.setCoordinates(coord);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					tdata.setDistance(i, j, weights[i][j]);
				}
			}
		}
	}
}
