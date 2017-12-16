#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

int main(int argc, char const *argv[]) {

        vector<string> cars;
        string aux;

        //This part gets all strings from the input file
        ifstream file(argv[1]);
        while (!file.eof()) {
                file >> aux;
                cars.push_back(aux);
        }
        file.close();

        //Here we erase the last element of the vector, repeated due to the reading algorithm
        cars.pop_back();

        int lane_number = stoi(cars[0]);
        int locations = stoi(cars[1]);

        cars.erase(cars.begin());
        cars.erase(cars.begin());

        string init_parking_mat[lane_number][locations];

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            init_parking_mat[i][j] = cars[0];
            cars.erase(cars.begin());
          }
        }

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            cout << init_parking_mat[i][j] << " ";
          }
          cout << endl;
        }



        return 0;
}
