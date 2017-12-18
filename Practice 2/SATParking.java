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


/*javac -classpath .:jacop-4.3.0.jar SATParking.java*/
/*java -classpath .:jacop SATParking input.input*/

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

                //Matrices to store if a car is blocked based in the adjacent positions DEFAULT TRUE
		//True->FREE
		//False->BLOCKED

                Store store = new Store();
                SatWrapper satWrapper = new SatWrapper();
                store.impose(satWrapper);

		//There is no car in adj position
                BooleanVar empty_fr[][] = new BooleanVar[lane_number][locations];
                int empty_fr_lit[][] = new int[lane_number][locations];

                BooleanVar empty_bh[][] = new BooleanVar[lane_number][locations];
                int empty_bh_lit[][] = new int[lane_number][locations];

		//CHECK IF NEEDED Car is an edge car
                BooleanVar edge[][] = new BooleanVar[lane_number][locations];
                int edge_lit[][] = new int[lane_number][locations];

		//Car surrounded by lower category cars in adj positions
                BooleanVar lowerCat_fr[][] = new BooleanVar[lane_number][locations];
                int lowerCat_fr_lit[][] = new int[lane_number][locations];

                BooleanVar lowerCat_bh[][] = new BooleanVar[lane_number][locations];
                int lowerCat_bh_lit[][] = new int[lane_number][locations];


		//Car surrounded by lower arrival time cars in adj positions
                BooleanVar lowerArr_fr[][] = new BooleanVar[lane_number][locations];
                int lowerArr_fr_lit[][] = new int[lane_number][locations];

                BooleanVar lowerArr_bh[][] = new BooleanVar[lane_number][locations];
                int lowerArr_bh_lit[][] = new int[lane_number][locations];


		//allVariables
/*
                BooleanVar allVariables[] = new BooleanVar[lane_number*(locations-2)*4];

                for (int i = 0; i < lane_number; i++) {
                        for (int k=1; k < (locations-2); k++) {
                                allVariables[i*locations+k] = loc_A[i][k];
                        }
                }

                for (int i = lane_number; i < 2*lane_number; i++) {
                        for (int k=1; k < (locations-2); k++) {
                                allVariables[i*locations+k] = loc_B[i-lane_number][k];
                        }
                }

                for (int i = 2*lane_number; i < 3*lane_number; i++) {
                        for (int k=1; k < (locations-2); k++) {
                                allVariables[i*locations+k] = loc_C[i-2*lane_number][k];
                        }
                }

                for (int i = 3*lane_number; i < 4*lane_number; i++) {
                        for (int k=1; k < (locations-2); k++) {
                                allVariables[i*locations+k] = loc_empty[i-3*lane_number][k];
                        }
                }
*/
                //Now we establish variables and clauses

                for (int i = 0; i<lane_number; i++) {
                        for (int k = 0; k<locations; k++) {

                                empty_fr[i][k] = new BooleanVar(store, "empty_fr " + i + "/" + k);
                                satWrapper.register(empty_fr[i][k]);
                                empty_fr_lit[i][k] = satWrapper.cpVarToBoolVar(empty_fr[i][k], 1, true);

                                empty_bh[i][k] = new BooleanVar(store, "empty_bh " + i + "/" + k);
                                satWrapper.register(empty_bh[i][k]);
                                empty_bh_lit[i][k] = satWrapper.cpVarToBoolVar(empty_bh[i][k], 1, true);

				//CHECK IF NEEDED
                                edge[i][k] = new BooleanVar(store, "edge " + i + "/" + k);
                                satWrapper.register(edge[i][k]);
                                edge_lit[i][k] = satWrapper.cpVarToBoolVar(edge[i][k], 1, true);

                                lowerCat_fr[i][k] = new BooleanVar(store, "lowerCat_fr " + i + "/" + k);
                                satWrapper.register(lowerCat_fr[i][k]);
                                lowerCat_fr_lit[i][k] = satWrapper.cpVarToBoolVar(lowerCat_fr[i][k], 1, true);

                                lowerCat_bh[i][k] = new BooleanVar(store, "lowerCat_bh " + i + "/" + k);
                                satWrapper.register(lowerCat_bh[i][k]);
                                lowerCat_bh_lit[i][k] = satWrapper.cpVarToBoolVar(lowerCat_bh[i][k], 1, true);

                                lowerArr_fr[i][k] = new BooleanVar(store, "lowerArr_fr " + i + "/" + k);
                                satWrapper.register(lowerArr_fr[i][k]);
                                lowerArr_fr_lit[i][k] = satWrapper.cpVarToBoolVar(lowerArr_fr[i][k], 1, true);

                                lowerArr_bh[i][k] = new BooleanVar(store, "lowerArr_bh " + i + "/" + k);
                                satWrapper.register(lowerArr_bh[i][k]);
                                lowerArr_bh_lit[i][k] = satWrapper.cpVarToBoolVar(lowerArr_bh[i][k], 1, true);

				/*loc_B[i][k] = new BooleanVar(store, "B " + i + "/" + k);
                                satWrapper.register(loc_B[i][k]);
                                int_loc_B[i][k] = satWrapper.cpVarToBoolVar(loc_B[i][k], 1, true);*/

                        }
                }


                for (int i = 0; i < lane_number; i++) {
                        for (int k = 1; k < (locations-1); k++) {

				//FRONT
				if(categories[i][k+1]){ //NOT BLOCKED
					addClause(satWrapper, empty_fr_lit[i][k]);
				}
				else{ //BLOCKED
					addClause(satWrapper, -empty_fr_lit[i][k]);
				}

				if(categories[i][k].charAt(0)>categories[i][k+1].charAt(0)){ //NOT BLOCKED
					addClause(satWrapper, lowerCat_fr_lit[i][k]);
				}
				else if(categories[i][k].charAt(0)==categories[i][k+1].charAt(0)){
					if(arrival[i][k].charAt(0)>arrival[i][k+1].charAt(0)){ //NOT BLOCKED
						addClause(satWrapper, lowerArr_fr_lit[i][k]);
					}	
					else{ //BLOCKED
						addClause(satWrapper, -lowerArr_fr_lit[i][k]);
					}			
				}
				else{ //BLOCKED
					addClause(satWrapper, -lowerCat_fr_lit[i][k]);
				}

				//BEHIND
				if(categories[i][k-1]){ //NOT BLOCKED
					addClause(satWrapper, empty_bh_lit[i][k]);
				}
				else{ //BLOCKED
					addClause(satWrapper, -empty_bh_lit[i][k]);
				}

				if(categories[i][k].charAt(0)>categories[i][k-1].charAt(0)){ //NOT BLOCKED
					addClause(satWrapper, lowerCat_bh_lit[i][k]);
				}
				else if(categories[i][k].charAt(0)==categories[i][k-1].charAt(0)){
					if(arrival[i][k].charAt(0)>arrival[i][k-1].charAt(0)){ //NOT BLOCKED
						addClause(satWrapper, lowerArr_bh_lit[i][k]);
					}	
					else{ //BLOCKED
						addClause(satWrapper, -lowerArr_bh_lit[i][k]);
					}			
				}
				else{ //BLOCKED
					addClause(satWrapper, -lowerCat_bh_lit[i][k]);
				}

				addClause(satWrapper, empty_fr_lit[i][k], lowerCat_fr_lit[i][k], lowerArr_fr_lit[i][k], edge[i][k]);
				addClause(satWrapper, empty_bh_lit[i][k], lowerCat_bh_lit[i][k], lowerArr_bh_lit[i][k], edge[i][k]);


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
