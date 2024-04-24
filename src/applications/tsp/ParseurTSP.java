package applications.tsp;

import applications.tsp.TSPInstance;
import bench.Point;

import java.io.*;
import java.util.Scanner;

/**
 * Parseur TSP
 */
public class ParseurTSP {

    protected String name;
    protected TSPInstance data;
    protected BufferedReader br;

    public ParseurTSP(String file) {
        this.name = file;
        try {
            br = new BufferedReader(new FileReader(new File(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return name;
    }

    public TSPInstance getData() {
        return data;
    }

    public TSPInstance parse() {
        try {
            int nb_mag = 0;
            //Load the number of products and markets
            String line = br.readLine();
            String edgeType = "EXPLICIT";
            String edgeFormat = "";

            while (!line.startsWith("EDGE_WEIGHT_SECTION") &&
                    !line.startsWith("NODE_COORD_SECTION")) {
                if (line.startsWith("DIMENSION")) {
                    line = line.replace(":", "");
                    line = line.replace("  ", " ");
                    Scanner scan = new Scanner(line);
                    scan.next();
                    nb_mag = scan.nextInt();
                    data = new TSPInstance(nb_mag);
                }
                if (line.startsWith("EDGE_WEIGHT_TYPE")) {
                    line = line.replace(":", "");
                    line = line.replace("  ", " ");
                    Scanner scan = new Scanner(line);
                    scan.next();
                    edgeType = scan.next();
                }
                if (line.startsWith("EDGE_WEIGHT_FORMAT")) {
                    line = line.replace(":", "");
                    line = line.replace("  ", " ");
                    Scanner scan = new Scanner(line);
                    scan.next();
                    edgeFormat = scan.next();
                }

                line = br.readLine();
            }


            while (line != null) {
                if (line.startsWith("EDGE_WEIGHT_SECTION") ||
                        line.startsWith("NODE_COORD_SECTION")) {

                    if (edgeType.equals("EXPLICIT")) {

                        if (edgeFormat.equals("FULL_MATRIX")) {

                            loadFullMatrix(nb_mag);

                        } else if (edgeFormat.equals("LOWER_DIAG_ROW")) {

                            loadLowerMatrix(nb_mag);

                        } else if (edgeFormat.equals("UPPER_ROW")) {

                            loadUpperRowMatrix(nb_mag);

                        }
                    } else if (edgeType.equals("GEO") || edgeType.equals("EUC_2D") || edgeType.equals("ATT")) {

                        loadDistMatrixFromNodesCoords(edgeType, nb_mag);

                    } else {

                        throw new Error("EdgePosition format unknown");

                    }
                }
                line = br.readLine();
            }
            data.init();
            String[] nsplit = name.split("/");
            data.setName(name.split("/")[nsplit.length - 1].replace(".tsp", ""));
            br.readLine(); //skip the OFFER_SECTION:

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    protected void loadFullMatrix(int nb_mag) throws IOException {
        //System.out.println(name + " " + nb_mag);
        String[] flat_matrix = extractFlatMatrix(nb_mag);
        for (int i = 0; i < nb_mag; i++) {
            for (int j = 0; j < nb_mag; j++) {
                data.setDistance(i, j, (i == j) ? 0 : Integer.parseInt(flat_matrix[i * nb_mag + j]));
            }
        }
    }

    private String[] extractFlatMatrix(int nb_mag) throws IOException {
        String line = "";
        int size = 0;
        String brutline = br.readLine();
        do {
            String currentLine = trimLine(brutline);
            if (!currentLine.isEmpty()) {
                size += currentLine.split(" ").length;
                line += currentLine + " ";
            }
            brutline = br.readLine();
        } while (brutline != null && !brutline.contains("SECTION"));
        line = line.trim();
        return line.split(" ");
    }

    public void loadUpperRowMatrix(int nb_mag) throws IOException {
        String[] flatmat = extractFlatMatrix(nb_mag);
        int i = 0;
        int cpt = 0;
        while (i < nb_mag) {
            //String line =
            //int j = (diag) ? i: i+1;
            //int nbExpectedToken = nb_mag - (i+1);
            for (int j = i + 1; j < nb_mag; j++) {
                int dist = Integer.parseInt(flatmat[cpt++]);
                //System.out.println("dist " + i + " -> "+ j +" : " + dist);
                //for (int j = i + 1; j < nb_mag; j++) {
                data.setDistance(i, j, dist);
                data.setDistance(j, i, data.getDist(i, j));
            }
            i++;
        }
    }

    private String trimLine(String theline) {
        String line = theline.trim().replaceAll("\t", " ");
        boolean change = true;
        while (change) {
            String trimLine = line.replaceAll("  ", " ");
            if (trimLine.equals(line)) change = false;
            line = trimLine;
        }
        return line;
    }

    public void loadLowerMatrix(int nb_mag) throws IOException {
        String line = br.readLine();
        String allMatrix = "";
        while (!line.startsWith("EOF") && !line.startsWith("DISPLAY_DATA_SECTION")) {
            allMatrix += line.trim() + " ";
            line = br.readLine();
        }
        allMatrix = allMatrix.replaceAll("\t", " ");
        boolean change = true;
        while (change) {
            String trimMatrix = allMatrix.replaceAll("  ", " ");
            if (trimMatrix.equals(allMatrix)) change = false;
            allMatrix = trimMatrix;
        }
        Scanner scan = new Scanner(allMatrix);
        scan.useDelimiter(" ");
        for (int i = 0; i < nb_mag; i++) {
            for (int j = 0; j <= i; j++) {
                data.setDistance(i, j, scan.nextInt());
                data.setDistance(j, i, data.getDist(i, j));
            }
        }
    }

    public void loadDistMatrixFromNodesCoords(String edgeFormat, int nbcity) throws IOException {
        double[][] coord = new double[nbcity][2];
        Point[] city_points = new Point[nbcity];

        for (int i = 0; i < nbcity; i++) {
            String line = br.readLine();
            Scanner scan = new Scanner(line);
            //line = line.replace(".",",");
            scan.next();
            coord[i][0] = Double.parseDouble(scan.next());//nextDouble();
            coord[i][1] = Double.parseDouble(scan.next());
            city_points[i] = new Point(coord[i][0], coord[i][1]);
        }
        data.setCoordinates(city_points);

        int[][] dist = new int[nbcity][nbcity];

        for (int i = 0; i < nbcity; i++) {
            for (int j = i + 1; j < nbcity; j++) {
                if (edgeFormat.equals("EUC_2D")) {
                    dist[i][j] = (int) Math.round(Math.sqrt(Math.pow(coord[i][0] - coord[j][0], 2) +
                            Math.pow(coord[i][1] - coord[j][1], 2)));
                } else if (edgeFormat.equals("GEO")) {

                    double latitudei = getLatitude(coord[i][0]);
                    double longitudei = getLongitude(coord[i][1]);
                    double latitudej = getLatitude(coord[j][0]);
                    double longitudej = getLongitude(coord[j][1]);

                    double RRR = 6378.388d;
                    double q1 = Math.cos(longitudei - longitudej);
                    double q2 = Math.cos(latitudei - latitudej);
                    double q3 = Math.cos(latitudei + latitudej);
                    dist[i][j] = (int) (RRR * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

                } else if (edgeFormat.equals("ATT")) {
                    double xd = coord[i][0] - coord[j][0];
                    double yd = coord[i][1] - coord[j][1];
                    double rij = Math.sqrt((xd * xd + yd * yd) / 10.0d);
                    int tij = (int) Math.round(rij);
                    if (tij < rij) dist[i][j] = tij + 1;
                    else dist[i][j] = tij;
                }
                dist[j][i] = dist[i][j];
                data.setDistance(i, j, dist[i][j]);
                data.setDistance(j, i, dist[i][j]);
            }
        }
    }

    public double getLatitude(double xi) {
        double PI = 3.141592d;
        int deg = (int) xi;//(int) Math.round(xi);
        return PI * (deg + 5.0d * (xi - deg) / 3.0d) / 180.0d;
    }

    public double getLongitude(double yi) {
        double PI = 3.141592d;
        int deg = (int) yi;//(int) Math.round(yi);
        return PI * (deg + 5.0d * (yi - deg) / 3.0d) / 180.0d;
    }

    //*****************************************//
    //************ Petit test *****************//
    //*****************************************//

//    public static void main(String[] args) {
////        ParseurLaporte p = new ParseurLaporte("./data/Clase1/Singh33_2.33.50.1.500.applications.tpp");
//        ParseurTSP p = new ParseurTSP("./data/Tsp/berlin52.tsp");
//        TSPInstance tdata = p.parse();
//        //tdata.print();
//
//        long tps = System.currentTimeMillis();
//        TSPHeuristic heuristic = new TSPHeuristic(tdata);
//        heuristic.insertionAnd2OPTHeuristic();
//        heuristic.twoOpt();
//        System.out.println("HEUR: " + heuristic.getCost() + " CPU: " + (System.currentTimeMillis() - tps));
//
//
//        tps = System.currentTimeMillis();
//        SmallTSPModel cpmodel = new SmallTSPModel(tdata);
//        cpmodel.setSolVerbose(false);
//        cpmodel.setHKLowerBound(true);
//        cpmodel.setFilteringInLR(true);
//        cpmodel.buildModel();
//        cpmodel.solve(false, -1);
//        System.out.println("CP: " + cpmodel.getBestValue() + " NODE: " + cpmodel.getSolver().getMeasures().getNodeCount() + " CPU: " + (System.currentTimeMillis() - tps));
//
//
//        /*tps = System.currentTimeMillis();
//        TspPdynSolver tspdyn = new TspPdynSolver(tdata);
//        tspdyn.progDynSolver();
//        //if (seed2 == start)
//        System.out.println(" PDYN: " + tspdyn.extractOptValue() + " " + (System.currentTimeMillis() - tps));
//        */
//
//    }

}