import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
// import org.jacop.core.BooleanVar;
// import org.jacop.core.Store;
// import org.jacop.jasat.utils.structures.IntVec;
// import org.jacop.satwrapper.SatWrapper;
// import org.jacop.search.DepthFirstSearch;
// import org.jacop.search.IndomainMin;
// import org.jacop.search.Search;
// import org.jacop.search.SelectChoicePoint;
// import org.jacop.search.SimpleSelect;
// import org.jacop.search.SmallestDomain;

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

                String categories[][] = new String [lane_number][locations];
                for(int i = 0; i < lane_number; i++){
                  System.out.println(" ");
                  for(int k = 0; k < locations; k++){
                    categories[i][k] = String.valueOf(parking[i][k].charAt(0));
                    System.out.print(categories[i][k]+" ");
                  }
                }
                System.out.println("");
                String arrival[][] = new String [lane_number][locations];
                for(int i = 0; i < lane_number; i++){
                  System.out.println(" ");
                  for(int k = 0; k < locations; k++){
                    arrival[i][k] = String.valueOf(parking[i][k].charAt(1));
                    System.out.print(arrival[i][k]+" ");
                  }
                }

                bufferedreader.close();
        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }

        // Store store = new Store();
        // BooleanVar A = new BooleanVar(store, "Car of type A in tile");
    		// BooleanVar B = new BooleanVar(store, "Car of type B in tile");
    		// BooleanVar C = new BooleanVar(store, "Car of type C in tile");

}
}
