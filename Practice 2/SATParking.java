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
                int int_parking_mat[][] = new int[lane_number][locations];
                for(int i = 0; i < lane_number; i++) {
                        for(int k = 0; k < locations; k++) {
                                parking_mat[i][k] = new BooleanVar (store, "Car Type "+categories[i][k]+" arrived at "+arrival[i][k]+ " in "+i+","+k);
                                satWrapper.register(parking_mat[i][j]);
                                //Set bool variable true only for the ones which have categories[i][j] different than "_"
                                if(categories[i][k].equals("_")) {
                                        int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, false);
                                }
                                else{
                                        int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 1, true);
                                }

                        }
                }

                //Now, we have to set values according to the categories and arrival times
                for (int i = 0; i < lane_number; i++) {
                  for (int k = 1; k < (locations-1); k++) {
                    if ((categories[i][k].charAt(0)<categories[i][k-1].charAt(0)) && (categories[i][k].charAt(0)<categories[i][k+1].charAt(0))) {
                      int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, false);
                    }
                    if ((categories[i][k].charAt(0)==categories[i][k-1].charAt(0)) && (categories[i][k].charAt(0)==categories[i][k+1].charAt(0))) {
                      if ((Integer.parseInt(arrival[i][k])<Integer.parseInt(arrival[i][k-1])) && (Integer.parseInt(arrival[i][k])<Integer.parseInt(arrival[i][k+1]))) {
                        int_parking_mat[i][k] = satWrapper.cpVarToBoolVar(parking_mat[i][k], 0, false);
                      }
                    }
                  }
                }

                //Here, we start comparisons between category and arrival time

        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }


}
}
