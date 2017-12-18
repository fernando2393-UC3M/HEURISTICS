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
		System.out.print("CATEGORIES");
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
		System.out.print("ARRIVAL");
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

		//Car is an edge car
                BooleanVar edge[][] = new BooleanVar[lane_number][locations];
                int edge_lit[][] = new int[lane_number][locations];

		//Car surrounded by lower category cars in adj positions
                BooleanVar lowerCat_fr[][] = new BooleanVar[lane_number][locations];
                int lowerCat_fr_lit[][] = new int[lane_number][locations];

                BooleanVar lowerCat_bh[][] = new BooleanVar[lane_number][locations];
                int lowerCat_bh_lit[][] = new int[lane_number][locations];

		//Car surrounded by equal category cars in adj positions
                BooleanVar equalCat_fr[][] = new BooleanVar[lane_number][locations];
                int equalCat_fr_lit[][] = new int[lane_number][locations];

                BooleanVar equalCat_bh[][] = new BooleanVar[lane_number][locations];
                int equalCat_bh_lit[][] = new int[lane_number][locations];

		//Car surrounded by lower arrival time cars in adj positions
                BooleanVar lowerArr_fr[][] = new BooleanVar[lane_number][locations];
                int lowerArr_fr_lit[][] = new int[lane_number][locations];

                BooleanVar lowerArr_bh[][] = new BooleanVar[lane_number][locations];
                int lowerArr_bh_lit[][] = new int[lane_number][locations];


                //Now we establish variables and clauses

                for (int i = 0; i<lane_number; i++) {
                        for (int k = 0; k<locations; k++) {

                                empty_fr[i][k] = new BooleanVar(store, "empty_fr " + i + "/" + k);
                                satWrapper.register(empty_fr[i][k]);
                                empty_fr_lit[i][k] = satWrapper.cpVarToBoolVar(empty_fr[i][k], 1, true);

                                empty_bh[i][k] = new BooleanVar(store, "empty_bh " + i + "/" + k);
                                satWrapper.register(empty_bh[i][k]);
                                empty_bh_lit[i][k] = satWrapper.cpVarToBoolVar(empty_bh[i][k], 1, true);

                                edge[i][k] = new BooleanVar(store, "edge " + i + "/" + k);
                                satWrapper.register(edge[i][k]);
                                edge_lit[i][k] = satWrapper.cpVarToBoolVar(edge[i][k], 1, true);

                                lowerCat_fr[i][k] = new BooleanVar(store, "lowerCat_fr " + i + "/" + k);
                                satWrapper.register(lowerCat_fr[i][k]);
                                lowerCat_fr_lit[i][k] = satWrapper.cpVarToBoolVar(lowerCat_fr[i][k], 1, true);

                                lowerCat_bh[i][k] = new BooleanVar(store, "lowerCat_bh " + i + "/" + k);
                                satWrapper.register(lowerCat_bh[i][k]);
                                lowerCat_bh_lit[i][k] = satWrapper.cpVarToBoolVar(lowerCat_bh[i][k], 1, true);

                                equalCat_fr[i][k] = new BooleanVar(store, "equalCat_fr " + i + "/" + k);
                                satWrapper.register(equalCat_fr[i][k]);
                                equalCat_fr_lit[i][k] = satWrapper.cpVarToBoolVar(equalCat_fr[i][k], 1, true);

                                equalCat_bh[i][k] = new BooleanVar(store, "equalCat_bh " + i + "/" + k);
                                satWrapper.register(equalCat_bh[i][k]);
                                equalCat_bh_lit[i][k] = satWrapper.cpVarToBoolVar(equalCat_bh[i][k], 1, true);

                                lowerArr_fr[i][k] = new BooleanVar(store, "lowerArr_fr " + i + "/" + k);
                                satWrapper.register(lowerArr_fr[i][k]);
                                lowerArr_fr_lit[i][k] = satWrapper.cpVarToBoolVar(lowerArr_fr[i][k], 1, true);

                                lowerArr_bh[i][k] = new BooleanVar(store, "lowerArr_bh " + i + "/" + k);
                                satWrapper.register(lowerArr_bh[i][k]);
                                lowerArr_bh_lit[i][k] = satWrapper.cpVarToBoolVar(lowerArr_bh[i][k], 1, true);

                        }
                }


                for (int i = 0; i < lane_number; i++) {
			addClause(satWrapper, edge_lit[i][0]);
			addClause(satWrapper, edge_lit[i][locations-1]);
                        for (int k = 1; k < (locations-1); k++) {
				addClause(satWrapper, -edge_lit[i][k]);
				//FRONT
				if(categories[i][k+1].charAt(0)=='0'){ //NOT BLOCKED
					addClause(satWrapper, empty_fr_lit[i][k]);
					System.out.println("Adding empty_fr_lit["+i+"]["+k+"]");
				}
				else{ //BLOCKED
					addClause(satWrapper, -empty_fr_lit[i][k]);
					System.out.println("Adding empty_fr_lit["+i+"]["+k+"] BLOCKED");
					if(categories[i][k].charAt(0)>categories[i][k+1].charAt(0)){ //NOT BLOCKED
						addClause(satWrapper, lowerCat_fr_lit[i][k]);
						addClause(satWrapper, -equalCat_fr_lit[i][k]);
						System.out.println("Adding lowerCat_fr_lit["+i+"]["+k+"]");
					}
					else if(categories[i][k].charAt(0)==categories[i][k+1].charAt(0)){
						addClause(satWrapper, -lowerCat_fr_lit[i][k]);
						addClause(satWrapper, equalCat_fr_lit[i][k]);
						if(arrival[i][k].charAt(0)>arrival[i][k+1].charAt(0)){ //NOT BLOCKED
							addClause(satWrapper, lowerArr_fr_lit[i][k]);
							System.out.println("Adding lowerArr_fr_lit["+i+"]["+k+"]");
						}	
						else{ //BLOCKED
							addClause(satWrapper, -lowerArr_fr_lit[i][k]);
							System.out.println("Adding lowerArr_fr_lit["+i+"]["+k+"] BLOCKED");
						}			
					}
					else{ //BLOCKED
						addClause(satWrapper, -lowerCat_fr_lit[i][k]);
						addClause(satWrapper, -equalCat_fr_lit[i][k]);
						addClause(satWrapper, -lowerArr_fr_lit[i][k]);
						System.out.println("Adding lowerCat_fr_lit["+i+"]["+k+"] BLOCKED");
					}
				}

				//BEHIND
				if(categories[i][k-1].charAt(0)=='0'){ //NOT BLOCKED
					addClause(satWrapper, empty_bh_lit[i][k]);
					System.out.println("Adding empty_bh_lit["+i+"]["+k+"]");
				}
				else{ //BLOCKED
					addClause(satWrapper, -empty_bh_lit[i][k]);
					System.out.println("Adding empty_bh_lit["+i+"]["+k+"] BLOCKED");
					if(categories[i][k].charAt(0)>categories[i][k-1].charAt(0)){ //NOT BLOCKED
						addClause(satWrapper, lowerCat_bh_lit[i][k]);
						addClause(satWrapper, -equalCat_bh_lit[i][k]);
						System.out.println("Adding lowerCat_bh_lit["+i+"]["+k+"]");
					}
					else if(categories[i][k].charAt(0)==categories[i][k-1].charAt(0)){
						addClause(satWrapper, -lowerCat_bh_lit[i][k]);
						addClause(satWrapper, equalCat_bh_lit[i][k]);
						if(arrival[i][k].charAt(0)>arrival[i][k-1].charAt(0)){ //NOT BLOCKED
							addClause(satWrapper, lowerArr_bh_lit[i][k]);
							System.out.println("Adding lowerArr_bh_lit["+i+"]["+k+"]");
						}	
						else{ //BLOCKED
							addClause(satWrapper, -lowerArr_bh_lit[i][k]);
							System.out.println("Adding lowerArr_bh_lit["+i+"]["+k+"] BLOCKED");
						}			
					}
					else{ //BLOCKED
						addClause(satWrapper, -lowerCat_bh_lit[i][k]);
						addClause(satWrapper, -equalCat_bh_lit[i][k]);
						addClause(satWrapper, -lowerArr_bh_lit[i][k]);
						System.out.println("Adding lowerCat_bh_lit["+i+"]["+k+"] BLOCKED");

					}
				}

				System.out.print("\n");
				addClause(satWrapper, empty_fr_lit[i][k], empty_bh_lit[i][k], edge_lit[i][k], lowerCat_fr_lit[i][k], lowerCat_bh_lit[i][k], equalCat_bh_lit[i][k], equalCat_fr_lit[i][k]);
				addClause(satWrapper, empty_fr_lit[i][k], empty_bh_lit[i][k], edge_lit[i][k], lowerCat_fr_lit[i][k], lowerCat_bh_lit[i][k], lowerArr_bh_lit[i][k], equalCat_fr_lit[i][k]);
				addClause(satWrapper, empty_fr_lit[i][k], empty_bh_lit[i][k], edge_lit[i][k], lowerCat_fr_lit[i][k], lowerCat_bh_lit[i][k], equalCat_bh_lit[i][k], lowerArr_fr_lit[i][k]);
				addClause(satWrapper, empty_fr_lit[i][k], empty_bh_lit[i][k], edge_lit[i][k], lowerCat_fr_lit[i][k], lowerCat_bh_lit[i][k], lowerArr_bh_lit[i][k], lowerArr_fr_lit[i][k]);
                        }

                }


		//allVariables
                BooleanVar allVariables[] = new BooleanVar[lane_number*(locations)*9];

                for (int i = 0; i < lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = empty_fr[i][k];
//				System.out.println("Matrix 1: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = lane_number; i < 2*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = empty_bh[i-lane_number][k];
//				System.out.println("Matrix 2: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 2*lane_number; i < 3*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = edge[i-2*lane_number][k];
//				System.out.println("Matrix 3: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 3*lane_number; i < 4*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = lowerCat_fr[i-3*lane_number][k];
//				System.out.println("Matrix 4: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 4*lane_number; i < 5*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = lowerCat_bh[i-4*lane_number][k];
//				System.out.println("Matrix 5: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 5*lane_number; i < 6*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = equalCat_fr[i-5*lane_number][k];
//				System.out.println("Matrix 6: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 6*lane_number; i < 7*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = equalCat_bh[i-6*lane_number][k];
//				System.out.println("Matrix 7: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }


                for (int i = 7*lane_number; i < 8*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = lowerArr_fr[i-7*lane_number][k];
//				System.out.println("Matrix 8: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
                        }
                }

                for (int i = 8*lane_number; i < 9*lane_number; i++) {
                        for (int k=0; k < (locations); k++) {
                                allVariables[i*locations+k] = lowerArr_bh[i-8*lane_number][k];
//				System.out.println("Matrix 9: i= "+i+"; j= "+k);
//				System.out.println("allVariables["+ (i*locations+k) +"]= "+allVariables[i*locations+k]);
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

                                        if(empty_fr[i][k].dom().value() == 1) {
                                                System.out.println(empty_fr[i][k].id());
                                        }

                                        if(empty_bh[i][k].dom().value() == 1) {
                                                System.out.println(empty_bh[i][k].id());
                                        }

                                        if(edge[i][k].dom().value() == 1) {
                                                System.out.println(edge[i][k].id());
                                        }

                                        if(lowerCat_fr[i][k].dom().value() == 1) {
                                                System.out.println(lowerCat_fr[i][k].id());
                                        }

                                        if(lowerCat_bh[i][k].dom().value() == 1) {
                                                System.out.println(lowerCat_bh[i][k].id());
                                        }

                                        if(equalCat_fr[i][k].dom().value() == 1) {
                                                System.out.println(equalCat_fr[i][k].id());
                                        }

                                        if(equalCat_bh[i][k].dom().value() == 1) {
                                                System.out.println(equalCat_bh[i][k].id());
                                        }

                                        if(lowerArr_fr[i][k].dom().value() == 1) {
                                                System.out.println(lowerArr_fr[i][k].id());
                                        }

                                        if(lowerArr_bh[i][k].dom().value() == 1) {
                                                System.out.println(lowerArr_bh[i][k].id());
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

public static void addClause(SatWrapper satWrapper, int literal1, int literal2, int literal3, int literal4, int literal5, int literal6, int literal7){
        IntVec clause = new IntVec(satWrapper.pool);
        clause.add(literal1);
        clause.add(literal2);
        clause.add(literal3);
        clause.add(literal4);
        clause.add(literal5);
        clause.add(literal6);
        clause.add(literal7);
        satWrapper.addModelClause(clause.toArray());
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
