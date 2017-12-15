import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import org.jacop.core.BooleanVar;
import org.jacop.core.Store;
import org.jacop.jasat.utils.structures.IntVec;
import org.jacop.satwrapper.SatWrapper;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;
import org.jacop.constraints.*;


public class SATParking {
public static void main(String[] args) {

        String filename = args[0];
        String line = null;
        int lane_number;
        int locations;
        int j = 0;

        try {
                FileReader filereader = new FileReader (filename);
                BufferedReader bufferedreader = new BufferedReader (filereader);

                line = bufferedreader.readLine();
                String data[] = line.split(" ");
                lane_number = Integer.parseInt(data[0]);
                locations = Integer.parseInt(data[1]);

                String parking[][] = new String [lane_number][locations];

                while((line = bufferedreader.readLine()) != null) {
                        String car_info[] = line.split(" ");
                        for (int i = 0; i < locations; i++) {
                                parking[j][i] = car_info[i];
                        }
                        j++;
                        System.out.println(line);
                }

                bufferedreader.close();
                //END PARSER

                String categories[][] = new String [lane_number][locations];
                for(int i = 0; i < lane_number; i++) {
                        System.out.println(" ");
                        for(int k = 0; k < locations; k++) {
                                categories[i][k] = String.valueOf(parking[i][k].charAt(0));
                                if (categories[i][k].equals("_")) {
                                        categories[i][k] = "0";
                                }
                                System.out.print(categories[i][k]+" ");
                        }
                }
                System.out.println("");
                String arrival[][] = new String [lane_number][locations];
                for(int i = 0; i < lane_number; i++) {
                        System.out.println(" ");
                        for(int k = 0; k < locations; k++) {
                                arrival[i][k] = String.valueOf(parking[i][k].charAt(1));
                                if (arrival[i][k].equals("_")) {
                                        arrival[i][k] = "0";
                                }
                                System.out.print(arrival[i][k]+" ");
                        }
                }

                //Here we create variables and matrices which will be used later

                Store store = new Store();
                SatWrapper satWrapper = new SatWrapper();
                store.impose(satWrapper);

                BooleanVar loc_A[][] = new BooleanVar[lane_number][locations];
                int int_loc_A[][] = new BooleanVar[lane_number][locations];
                BooleanVar loc_B[][] = new BooleanVar[lane_number][locations];
                int int_loc_B[][] = new BooleanVar[lane_number][locations];
                BooleanVar loc_C[][] = new BooleanVar[lane_number][locations];
                int int_loc_C[][] = new BooleanVar[lane_number][locations];
                BooleanVar loc_empty[][] = new BooleanVar[lane_number][locations];
                int int_loc_empty[][] = new BooleanVar[lane_number][locations];

                //Now we establish variables and clauses

                for (int i = 0; i < lane_number; i++) {
                        for (int k = 0; k < locations; k++) {

                          loc_A[i][k] = new BooleanVar(store, "A: "+i+"/"+k);

                                switch(categories[i][k]) {

                                case "A":

                                        loc_A[i][k] = new BooleanVar(store, "There is A car in " + i + "/" + k);
                                        satWrapper.register(loc_A[i][k]);
                                        int_loc_A[i][k] = satWrapper.cpVarToBoolVar(loc_A[i][k], 1, true);

                                        loc_B[i][k] = new BooleanVar(store, "There is no B car in " + i + "/" + k);
                                        satWrapper.register(loc_B[i][k]);
                                        int_loc_B[i][k] = satWrapper.cpVarToBoolVar(loc_B[i][k], 1, true);

                                        loc_C[i][k] = new BooleanVar(store, "There is no C car in " + i + "/" + k);
                                        satWrapper.register(loc_C[i][k]);
                                        int_loc_C[i][k] = satWrapper.cpVarToBoolVar(loc_C[i][k], 1, true);

                                        loc_empty[i][k] = new BooleanVar(store, "There is no empty car in " + i + "/" + k);
                                        satWrapper.register(loc_empty[i][k]);
                                        int_loc_empty[i][k] = satWrapper.cpVarToBoolVar(loc_empty[i][k], 1, true);

                                        if (k > 0 && k < (lane_number-1)) {
                                                if (categories[i][k].charAt(0)>categories[i][k+1].charAt(0) && categories[i][k].charAt(0)>categories[i][k-1].charAt(0)) {
                                                        addClause(satWrapper, loc_A[i][k+1], -loc_B[i][k+1], -loc_C[i][k+1], loc_empty[i][k+1]);
                                                        addClause(satWrapper, loc_A[i][k-1], -loc_B[i][k-1], -loc_C[i][k-1], loc_empty[i][k-1]);
                                                }
                                                if (categories[i][k].charAt(0)==categories[i][k+1].charAt(0) && categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) {
                                                        if (Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k+1]) && Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k-1])) {
                                                                addClause(satWrapper, loc_A[i][k+1], -loc_B[i][k+1], -loc_C[i][k+1], loc_empty[i][k+1]);
                                                                addClause(satWrapper, loc_A[i][k-1], -loc_B[i][k-1], -loc_C[i][k-1], loc_empty[i][k-1]);
                                                        }
                                                }
                                        }
                                        else{
                                                addClause(satWrapper, loc_A[i][k], -loc_B[i][k], -loc_C[i][k], -loc_empty[i][k]);
                                        }
                                        break;

                                case "B":

                                case "C":

                                default:

                                }

                        }

                }


                //Here we solve the problem

                Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();
                SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(allVariables, new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());
                Boolean result = search.labeling(store, select);

                for(int i=0; i < lane_number; i++) {
                        for(int k=0; k < locations; k++) {
                                if (result) {
                                        System.out.println("Solution: ");

                                        if(parking_mat[i][k].dom().value() == 1) {
                                                System.out.println(parking_mat[i][k].id());
                                        }
                                }

                                else{
                                        System.out.println("*** No");
                                }

                        }
                }

        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }


}

public static void addClause(SatWrapper satWrapper, int literal1, int literal2){
        IntVec clause = new IntVec(satWrapper.pool);
        clause.add(literal1);
        clause.add(literal2);
        satWrapper.addModelClause(clause.toArray());
}


public static void addClause(SatWrapper satWrapper, int literal1){
        IntVec clause = new IntVec(satWrapper.pool);
        clause.add(literal1);
        satWrapper.addModelClause(clause.toArray());
}
}
