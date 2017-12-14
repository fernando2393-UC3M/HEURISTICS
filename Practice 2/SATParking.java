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
                                System.out.print(categories[i][k]+" ");
                        }
                }
                System.out.println("");
                String arrival[][] = new String [lane_number][locations];
                for(int i = 0; i < lane_number; i++) {
                        System.out.println(" ");
                        for(int k = 0; k < locations; k++) {
                                arrival[i][k] = String.valueOf(parking[i][k].charAt(1));
                                System.out.print(arrival[i][k]+" ");
                        }
                }


                Store store = new Store();
                SatWrapper satWrapper = new SatWrapper();
                store.impose(satWrapper);

                BooleanVar parking_mat[][] = new BooleanVar[lane_number][locations];
                BooleanVar car_diff_front[][] = new BooleanVar[lane_number][locations];
                BooleanVar car_diff_behind[][] = new BooleanVar[lane_number][locations];
                BooleanVar car_same_front[][] = new BooleanVar[lane_number][locations];
                BooleanVar car_same_behind[][] = new BooleanVar[lane_number][locations];

                BooleanVar[] allVariables = new BooleanVar[]{parking_mat, car_diff_front, car_diff_behind, car_same_front, car_same_behind};

                //Literals matrix
                int parking_mat_Lit[][] = new int[lane_number][locations];
                int car_diff_front_Lit[][] = new int[lane_number][locations];
                int car_diff_behind_Lit[][] = new int[lane_number][locations];
                int car_same_front_Lit[][] = new int[lane_number][locations];
                int car_same_behind_Lit[][] = new int[lane_number][locations];

                for(int i = 0; i < lane_number; i++) {
                        for(int k = 0; k < locations; k++) {
                                parking_mat[i][k] = new BooleanVar (store, "Car Type "+categories[i][k]+" arrived at "+arrival[i][k]+ " in "+i+","+k);
                                satWrapper.register(parking_mat[i][k]);
                                //CAR:0 NOCAR:1
                                if(categories[i][k].equals("_")) {
                                        parking_mat_Lit[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, true);
                                }
                                else{
                                        parking_mat_Lit[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 1, true);
                                }
                        }
                }

                //Fill literal matrices
                for(int i = 0; i < lane_number; i++) {
                        for(int k = 1; k < (locations-1); k++) {
                                car_diff_front_Lit[i][k] = new BooleanVar (store, "Car "+i+k+" has in front a car with different class ");
                                car_diff_behind_Lit[i][k] = new BooleanVar (store, "Car "+i+k+" has behind a car with different class ");
                                car_same_front_Lit[i][k] = new BooleanVar (store, "Car "+i+k+" has in front a car with same class ");
                                car_same_behind_Lit[i][k] = new BooleanVar (store, "Car "+i+k+" has in behind a car with same class ");

                                satWrapper.register(car_diff_front_Lit);
                                satWrapper.register(car_diff_behind_Lit);
                                satWrapper.register(car_same_front_Lit);
                                satWrapper.register(car_same_behind_Lit);

                                //Different category for car front
                                if(categories[i][k+1].charAt(0)>categories[i][k].charAt(0)) {
                                        car_diff_front_Lit[i][k]=1; //BLOCKED
                                }

                                //Different category for car behind
                                if(categories[i][k-1].charAt(0)>categories[i][k].charAt(0)) {
                                        car_diff_behind_Lit[i][k]=1; //BLOCKED
                                }

                                //Same category for car front
                                if(categories[i][k+1].charAt(0)==categories[i][k].charAt(0)) {
                                        //Check time of car front
                                        if(Integer.parseInt(arrival[i][k+1])>Integer.parseInt(arrival[i][k])) {
                                                car_same_front_Lit[i][k]=1; //BLOCKED
                                        }
                                        if(Integer.parseInt(arrival[i][k-1])>Integer.parseInt(arrival[i][k])) {
                                                car_same_behind_Lit[i][k]=1; //BLOCKED
                                        }
                                }
                        }
                }

                for(int i=0; i < lane_number; i++) {
                        for(int k=1; k < locations-1; k++) {
                                if(categories[i][k].charAt(0)=='A') {
                                        //Ax,y && (Bx+1,y v Cx+1,y v Adx+1,y) && (Bx-1,y v Cx-1,y v Adx-1,y)
                                        addClause(satWrapper, car_diff_front_Lit[i][k], car_same_front_Lit[i][k]);
                                        addClause(satWrapper, car_diff_behind_Lit[i][k], car_same_behind_Lit[i][k]);
                                        addClause(satWrapper, parking_mat_Lit[i][k]);
                                }
                                if(categories[i][k].charAt(0)=='B') {
                                        //Bx,y && (Bdx+1,y v Cx+1,y) && (Bdx-1,y v Cx-1,y)
                                        addClause(satWrapper, car_diff_front_Lit[i][k], car_same_front_Lit[i][k]);
                                        addClause(satWrapper, car_diff_behind_Lit[i][k], car_same_behind_Lit[i][k]);
                                        addClause(satWrapper, parking_mat_Lit[i][k]);
                                }
                                if(categories[i][k].charAt(0)=='C') {
                                        //Cx,y && (Cdx+1,y) && (Cdx-1,y)
                                        addClause(satWrapper, car_same_front_Lit[i][k]);
                                        addClause(satWrapper, car_same_behind_Lit[i][k]);
                                        addClause(satWrapper, parking_mat_Lit[i][k]);
                                }
                        }
                }

                Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();
                SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(allVariables, new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());
                Boolean result = search.labeling(store, select);

                for(int i=0; i < lane_number; i++){
                  for(int k=0; k < locations; k++){
                    if (result) {
                            System.out.println("Solution: ");

                            if(parking_mat[i][k].dom().value() == 1) {
                                    System.out.println(parking_mat[i][k].id());
                            }

                            if(car_diff_front[i][k].dom().value() == 1) {
                                    System.out.println(car_diff_front[i][k].id());
                            }

                            if(car_diff_behind[i][k].dom().value() == 1) {
                                    System.out.println(car_diff_behind[i][k].id());
                            }

                            if(car_same_front[i][k].dom().value() == 1) {
                                    System.out.println(car_same_front[i][k].id());
                            }

                            if(car_same_behind[i][k].dom().value() == 1) {
                                    System.out.println(car_same_behind[i][k].id());
                            }
                    } else{
                            System.out.println("*** No");
                    }

                  }
                }

                //   //Now, we have to set values according to the categories and arrival times
                //  for (int i = 0; i < lane_number; i++) {
                //    for (int k = 1; k < (locations-1); k++) {
                //      if ((categories[i][k].charAt(0)<categories[i][k-1].charAt(0)) && (categories[i][k].charAt(0)<categories[i][k+1].charAt(0))) {
                //        int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, false);
                //      }
                //      if ((categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) && (categories[i][k].charAt(0)==categories[i][k+1].charAt(0))) {
                //        if ((Integer.parseInt(arrival[i][k])<Integer.parseInt(arrival[i][k-1])) && (Integer.parseInt(arrival[i][k])<Integer.parseInt(arrival[i][k+1]))) {
                //          int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, false);
                //        }
                //      }
                //    }
                //  }

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
        clause.add(literal2);
        clause.add(literal3);
        satWrapper.addModelClause(clause.toArray());
}
}
