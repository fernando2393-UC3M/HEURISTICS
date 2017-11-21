import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public class SATParking {
public static void main(String[] args) {

        String filename = args[0];
        String line = null;

        try {
                FileReader filereader = new FileReader (filename);
                BufferedReader bufferedreader = new BufferedReader (filereader);

                while((line = bufferedreader.readLine()) != null) {
                        System.out.println(line);
                }
                bufferedreader.close();
        }

        catch(IOException ex) {
                System.out.println("Error reading file '" + filename + "'");
        }
      }
}
