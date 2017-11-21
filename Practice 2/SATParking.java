import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

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

                while((line = bufferedreader.readLine()) != null){
                  String car_info[] = line.split(" ");
                  for (int i = 0; i < locations; i++) {
                    parking[j][i] = car_info[i];
                  }
                  j++;
                  System.out.println(line);
                }

                bufferedreader.close();
        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }
      }
}
