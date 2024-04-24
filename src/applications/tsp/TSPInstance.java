package applications.tsp;

import applications.tsp.ParseurTSP;
import bench.Point;

import java.util.*;

/**
 * A class to represent an instance of a TSP
 * The original instance is represented (no fake home/depot)
 * author: Hadrien Cambazard
 */
public class TSPInstance {

    /**
     * Name of the instance
     */
    protected String name;

    /**
     * Name of the benchmark
     */
    protected String benchclass;

    /**
     * Matrix of distances
     */
    protected int[][] dist;

    /**
     * The city coordinates are SOMETIMES available (Euclidian TSP)
     */
    protected Point[] coordinates;

    /**
     * Number of cities (note that indices start from 0)
     */
    public int nbCities; //cities in {0,..., nbCities-1}

    /**
     * Upper current_bound of the best possible tour
     */
    protected int ub;

    /**
     * indicate whether distances are symetric
     */
    protected boolean isSym = false;

    /**
     * Random generator
     */
    protected Random rand;

    /**
     * final cost has to be divided by scaleFactor to get
     * the original real costs (all distances, times were shited by scaleFactor to be integers)
     */
    protected int scaleFactor;

    /**
     * best known value
     * -1 if it has not been set
     */
    private double bestKnownSol;

    public TSPInstance(int nbCities) {
        this.nbCities = nbCities;
        this.dist = new int[nbCities][nbCities];
        this.scaleFactor = 1;
        this.bestKnownSol = -1;//default
        benchclass = "";
    }

    public TSPInstance(int[][] distances) {
        this.nbCities = distances.length;
        this.scaleFactor = 1;
        this.dist = new int[nbCities][nbCities];
        for (int i = 0; i < nbCities; i++) {
            System.arraycopy(distances[i], 0, dist[i], 0, nbCities);
        }
        init();
    }

    public void init() {
        ub = 0;
        isSym = true;
        for (int i = 0; i < dist.length; i++) {
            int max = 0;
            for (int j = 0; j < dist.length; j++) {
                if (i != j && max < dist[i][j])
                    max = dist[i][j];
                if (dist[i][j] != dist[j][i])
                    isSym = false;
            }
            ub += max;
        }
        if (ub < 0) {
            throw new Error("Overflow in TSPInstance");
        }
    }

    //*************************************************//
    //************* Set *******************************//
    //*************************************************//

    public void setBestKnownSol(double bestKnownSol) {
        this.bestKnownSol = bestKnownSol;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(int a, int b, int cost) {
        dist[a][b] = cost;
    }

    public void setBenchclass(String benchclass) {
        this.benchclass = benchclass;
    }

    public void setCoordinates(Point[] coordinates) {
        this.coordinates = coordinates;
    }

    //*************************************************//
    //************* Get *******************************//
    //*************************************************//

    public String getName() {
        return name;
    }

    public String getTspClass() {
        if (benchclass == null) return "Unknown";
        if (benchclass.equals("")) {
            if (nbCities <= 30) {
                return (isSym ? "S" : "A") + "-N<=30";
            } else if (nbCities <= 100) {
                return (isSym ? "S" : "A") + "-30<N<=100";
            } else {
                return (isSym ? "S" : "A") + "-N>100";
            }
        } else {
            return benchclass;
        }
    }

    public double getBestKnownSol() {
        return bestKnownSol;
    }

    public final int getNbCities() {
        return nbCities;
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public int[][] getCostMatrix() {
        return dist;
    }

    public boolean isSymmetric() {
        return isSym;
    }

    public Point getCityPoint(int i) {
        if (coordinates != null) {
            return coordinates[i];
        }
        return null;
    }

    public final int getUb() {
        return ub;
    }

    /**
     * @return the distance for traveling from a to b
     */
    public int getDist(int a, int b) {
        if (b == nbCities) return dist[a][0];
        if (a == nbCities) return dist[b][0];
        return dist[a][b];
    }


    public final double getRealDist(int a, int b) {
        return getDist(a, b) / ((double) scaleFactor);
    }

    //****************************************************//
    //*** Conversion Asymmetric -> symmetric *************//
    //****************************************************//

    /**
     * Transformation of Jonker and Volgenant
     *
     * @return a symetric instance equivalent to the asymmetric one but with twice the number of nodes
     */
    public TSPInstance conversion() {
        TSPInstance asymData = new TSPInstance(2 * nbCities);
        asymData.scaleFactor = this.scaleFactor;
        for (int i = 0; i < 2 * nbCities; i++) {
            for (int j = 0; j < 2 * nbCities; j++) {
                asymData.setDistance(i, j, ub);
            }
        }
        for (int i = 0; i < nbCities; i++) {
            for (int j = nbCities; j < 2 * nbCities; j++) {
                if (i == (j - nbCities)) {
                    asymData.setDistance(i, j, -ub);
                    asymData.setDistance(j, i, -ub);
                } else {
                    asymData.setDistance(i, j, dist[j - nbCities][i]);
                    asymData.setDistance(j, i, dist[j - nbCities][i]);
                }
            }
        }
        asymData.ub = ub;
        asymData.isSym = false;
        return asymData;
    }

    //****************************************************//
    //*** Conversion Double Home *************************//
    //****************************************************//

    /**
     * Transformation to double the home
     *
     * @return an instance with two homes to match CP model
     */
    public TSPInstance convTwoHomes() {
        TSPInstance plusOneData = new TSPInstance(nbCities+1);
        plusOneData.scaleFactor = this.scaleFactor;
        for (int i = 0; i < nbCities+1; i++) {
            for (int j = 0; j < nbCities+1; j++) {
                if (i != j) {
                    if (j == nbCities) {
                        plusOneData.setDistance(i, j, dist[i][0]);
                    } else if (i == nbCities) {
                        plusOneData.setDistance(i, j, dist[0][j]);
                    } else {
                        plusOneData.setDistance(i, j, dist[i][j]);
                    }
                }
            }
        }
        plusOneData.setDistance(nbCities,0, 0);
        plusOneData.setDistance(0, nbCities, 0);
        plusOneData.bestKnownSol = bestKnownSol;
        plusOneData.ub    = ub;
        plusOneData.isSym = isSym;
        plusOneData.name = this.name + "_2homes";
        if (coordinates != null) {
            plusOneData.coordinates = new Point[nbCities + 1];
            for (int i = 0; i < nbCities; i++) {
                plusOneData.coordinates[i] = new Point(coordinates[i].getX(), coordinates[i].getY());
            }
            plusOneData.coordinates[nbCities] = new Point(coordinates[0].getX(), coordinates[0].getY());
        }
        return plusOneData;
    }

    //*************************************************//
    //************* Generation ************************//
    //*************************************************//

    /**
     * Random initialization of the distances (non-symetric)
     *
     * @param seed: to reproduce random generation
     */
    public void genereateRandom(int seed) {
        genereateRandom(seed, false);
    }

    /**
     * Random  initialization of the distance matrix
     *
     * @param seed: to reproduce random generation
     * @param sym:  true is the distances have to be symetric i.e dist[i][j] = dist[j][i]
     */
    public void genereateRandom(int seed, boolean sym) {
        if (rand == null)
            rand = new Random(seed);
        ub = 0;
        for (int i = 0; i < nbCities; i++) {
            int start = (sym ? i + 1 : 0);
            for (int j = start; j < nbCities; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = rand.nextInt(100) + 1;
                    if (sym) dist[j][i] = dist[i][j];//rand.nextInt(100);

                    ub += dist[i][j];
                }
            }
        }
        ub = ub + 1;
        isSym = sym;
    }

    //*************************************************//
    //************* Print *****************************//
    //*************************************************//

    public void print() {
        for (int i = 0; i < dist.length; i++) {
            System.out.println(Arrays.toString(dist[i]));
        }
    }


    //*************************************************//
    //*** Sorted Neigbhors ****************************//
    //*************************************************//

    /**
     * @param city: the reference city to consider
     * @return the list of cities sorted by increasing (to) distance
     */
    public ArrayList<Integer> closestTo(int city) {
        ArrayList<Integer> closestTo = new ArrayList<Integer>();
        for (int i = 0; i < getNbCities(); i++) {
            if (i != city) {
                closestTo.add(i);
            }
        }
        Collections.sort(closestTo, new DistanceComparator(city));
        return closestTo;
    }

    public class DistanceComparator implements Comparator<Integer> {
        private int refCity;

        public DistanceComparator(int refCity) {
            this.refCity = refCity;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int do1 = dist[o1][refCity];// + dist[refCity][o1];
            int do2 = dist[o2][refCity];// + dist[refCity][o2];
            if (do1 < do2) {
                return -1;
            } else if (do1 > do2) {
                return 1;
            } else return 0;
        }
    }
    //*************************************************//
    //************* Main ******************************//
    //*************************************************//

    public static void main(String[] args) {
        ParseurTSP p = new ParseurTSP("./data/Tsp/dantzig42.tsp");
        TSPInstance data = p.parse();
        data.print();
        System.out.println("------------------");
        //TSPInstance conv_data = data.conversion();
        //conv_data.print();
    }
}
