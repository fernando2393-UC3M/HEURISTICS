#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

int main(int argc, char const *argv[]) {

        vector<string> initialcars;
        string aux;

        //This part gets all strings from the initial state file
        ifstream initialfile(argv[1]);
        while (!initialfile.eof()) {
                initialfile >> aux;
                initialcars.push_back(aux);
        }
        initialfile.close();

        //Here we erase the last element of the vector, repeated due to the reading algorithm
        initialcars.pop_back();

        int lane_number = stoi(initialcars[0]);
        int locations = stoi(initialcars[1]);

        initialcars.erase(initialcars.begin());
        initialcars.erase(initialcars.begin());

        string init_parking_mat[lane_number][locations];

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            init_parking_mat[i][j] = initialcars[0];
            initialcars.erase(initialcars.begin());
          }
        }

        //First check print
        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            cout << init_parking_mat[i][j] << " ";
          }
          cout << endl;
        }

        //Here, we get the data from the goal file

        vector<string> goalcars;

        ifstream goalfile(argv[2]);
        while (!goalfile.eof()) {
                goalfile >> aux;
                goalcars.push_back(aux);
        }
        goalfile.close();

        //Here we erase the last element of the vector, repeated due to the reading algorithm
        goalcars.pop_back();

        lane_number = stoi(goalcars[0]);
        locations = stoi(goalcars[1]);

        goalcars.erase(goalcars.begin());
        goalcars.erase(goalcars.begin());

        string goal_parking_mat[lane_number][locations];

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            goal_parking_mat[i][j] = goalcars[0];
            goalcars.erase(goalcars.begin());
          }
        }

        //Second check print
        cout << endl;
        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            cout << goal_parking_mat[i][j] << " ";
          }
          cout << endl;
        }



        return 0;
}
