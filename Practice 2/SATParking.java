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
                int int_loc_A[][] = new int[lane_number][locations];
                BooleanVar loc_B[][] = new BooleanVar[lane_number][locations];
                int int_loc_B[][] = new int[lane_number][locations];
                BooleanVar loc_C[][] = new BooleanVar[lane_number][locations];
                int int_loc_C[][] = new int[lane_number][locations];
                BooleanVar loc_empty[][] = new BooleanVar[lane_number][locations];
                int int_loc_empty[][] = new int[lane_number][locations];

                //Now we establish variables and clauses

                for (int i = 0; i<lane_number; i++) {
                        for (int k = 0; k<locations; k++) {

                                loc_A[i][k] = new BooleanVar(store, "A " + i + "/" + k);
                                satWrapper.register(loc_A[i][k]);
                                int_loc_A[i][k] = satWrapper.cpVarToBoolVar(loc_A[i][k], 1, true);

                                loc_B[i][k] = new BooleanVar(store, "B " + i + "/" + k);
                                satWrapper.register(loc_B[i][k]);
                                int_loc_B[i][k] = satWrapper.cpVarToBoolVar(loc_B[i][k], 1, true);

                                loc_C[i][k] = new BooleanVar(store, "C " + i + "/" + k);
                                satWrapper.register(loc_C[i][k]);
                                int_loc_C[i][k] = satWrapper.cpVarToBoolVar(loc_C[i][k], 1, true);

                                loc_empty[i][k] = new BooleanVar(store, "Empty " + i + "/" + k);
                                satWrapper.register(loc_empty[i][k]);
                                int_loc_empty[i][k] = satWrapper.cpVarToBoolVar(loc_empty[i][k], 1, true);
                        }
                }

                for (int i = 0; i < lane_number; i++) {
                        for (int k = 0; k < locations; k++) {

                                switch(categories[i][k]) {

                                case "A":

                                        if (k > 0 && k < (lane_number-1)) {
                                                if (categories[i][k].charAt(0)>categories[i][k+1].charAt(0) && categories[i][k].charAt(0)>categories[i][k-1].charAt(0)) {
                                                        addClause(satWrapper, int_loc_A[i][k+1], -int_loc_B[i][k+1], -int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                        addClause(satWrapper, int_loc_A[i][k-1], -int_loc_B[i][k-1], -int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                }
                                                if (categories[i][k].charAt(0)==categories[i][k+1].charAt(0) && categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) {
                                                        if (Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k+1]) && Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k-1])) {
                                                                addClause(satWrapper, int_loc_A[i][k+1], -int_loc_B[i][k+1], -int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                                addClause(satWrapper, int_loc_A[i][k-1], -int_loc_B[i][k-1], -int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                        }
                                                }
                                        }
                                        else{
                                                addClause(satWrapper, int_loc_A[i][k], -int_loc_B[i][k], -int_loc_C[i][k], -int_loc_empty[i][k]);
                                        }
                                        break;

                                case "B":

                                        if (k > 0 && k < (lane_number-1)) {
                                                if (categories[i][k].charAt(0)>categories[i][k+1].charAt(0) && categories[i][k].charAt(0)>categories[i][k-1].charAt(0)) {
                                                        addClause(satWrapper, int_loc_A[i][k+1], int_loc_B[i][k+1], -int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                        addClause(satWrapper, int_loc_A[i][k-1], int_loc_B[i][k-1], -int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                }
                                                if (categories[i][k].charAt(0)==categories[i][k+1].charAt(0) && categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) {
                                                        if (Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k+1]) && Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k-1])) {
                                                                addClause(satWrapper, -int_loc_A[i][k+1], int_loc_B[i][k+1], -int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                                addClause(satWrapper, -int_loc_A[i][k-1], int_loc_B[i][k-1], -int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                        }
                                                }
                                        }
                                        else{
                                                addClause(satWrapper, -int_loc_A[i][k], int_loc_B[i][k], -int_loc_C[i][k], -int_loc_empty[i][k]);
                                        }
                                        break;

                                case "C":

                                        if (k > 0 && k < (lane_number-1)) {
                                                if (categories[i][k].charAt(0)>categories[i][k+1].charAt(0) && categories[i][k].charAt(0)>categories[i][k-1].charAt(0)) {
                                                        addClause(satWrapper, int_loc_A[i][k+1], int_loc_B[i][k+1], int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                        addClause(satWrapper, int_loc_A[i][k-1], int_loc_B[i][k-1], int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                }
                                                if (categories[i][k].charAt(0)==categories[i][k+1].charAt(0) && categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) {
                                                        if (Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k+1]) && Integer.parseInt(arrival[i][k])>Integer.parseInt(arrival[i][k-1])) {
                                                                addClause(satWrapper, -int_loc_A[i][k+1], -int_loc_B[i][k+1], int_loc_C[i][k+1], int_loc_empty[i][k+1]);
                                                                addClause(satWrapper, -int_loc_A[i][k-1], -int_loc_B[i][k-1], int_loc_C[i][k-1], int_loc_empty[i][k-1]);
                                                        }
                                                }
                                        }
                                        else{
                                                addClause(satWrapper, -int_loc_A[i][k], -int_loc_B[i][k], int_loc_C[i][k], -int_loc_empty[i][k]);
                                        }
                                        break;

                                default:

                                        addClause(satWrapper, -int_loc_A[i][k], -int_loc_B[i][k], -int_loc_C[i][k], int_loc_empty[i][k]);

                                }

                        }

                }

                BooleanVar allVariables[] = new BooleanVar[lane_number*locations*4];

                for (int i = 0; i < lane_number; i++) {
                        for (int k=0; k < locations; k++) {
                                allVariables[i*locations+k] = loc_A[i][k];
                        }
                }

                for (int i = lane_number; i < 2*lane_number; i++) {
                        for (int k=0; k < locations; k++) {
                                allVariables[i*locations+k] = loc_B[i-lane_number][k];
                        }
                }

                for (int i = 2*lane_number; i < 3*lane_number; i++) {
                        for (int k=0; k < locations; k++) {
                                allVariables[i*locations+k] = loc_C[i-2*lane_number][k];
                        }
                }

                for (int i = 3*lane_number; i < 4*lane_number; i++) {
                        for (int k=0; k < locations; k++) {
                                allVariables[i*locations+k] = loc_empty[i-3*lane_number][k];
                        }
                }


                //Here we solve the problem

                Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();
                SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(allVariables, new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());
                Boolean result = search.labeling(store, select);


                if (result) {
                        System.out.println("The problem is SATISFIABLE");
                        for(int i=0; i < lane_number; i++) {
                                for(int k=0; k < locations; k++) {

                                        if(loc_A[i][k].dom().value() == 1) {
                                                System.out.println(loc_A[i][k].id());
                                        }

                                        if(loc_B[i][k].dom().value() == 1) {
                                                System.out.println(loc_B[i][k].id());
                                        }

                                        if(loc_C[i][k].dom().value() == 1) {
                                                System.out.println(loc_C[i][k].id());
                                        }

                                        if(loc_empty[i][k].dom().value() == 1) {
                                                System.out.println(loc_empty[i][k].id());
                                        }
                                }
                        }
                }

                else{
                        System.out.println("NO Satisfiable problem");
                }



        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }

}

public static void addClause(SatWrapper satWrapper, int literal1, int literal2, int literal3, int literal4){
        IntVec clause = new IntVec(satWrapper.pool);
        clause.add(literal1);
        clause.add(literal2);
        clause.add(literal3);
        clause.add(literal4);
        satWrapper.addModelClause(clause.toArray());
}

public static void addClause(SatWrapper satWrapper, int literal1, int literal2, int literal3){
        IntVec clause = new IntVec(satWrapper.pool);
        clause.add(literal1);
        clause.add(literal2);
        clause.add(literal3);
        satWrapper.addModelClause(clause.toArray());
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
