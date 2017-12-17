#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

int heuristic(vector <string> init, vector <string> goal, int lane_number, int locations, string car){
  //Here we introduce the initial and goal position and calculate the heuristic (minimum cost from init to goal)
  int h;
  int init_lane;
  int init_column;
  int goal_lane;
  int goal_column;

  for (int i = 0; i < lane_number; i++) {
    for (int j = 0; j < locations; j++) {
      if (init[i*locations+j]==car) {
        init_lane=i;
        init_column=j;
        break;
      }
    }
  }
  for (int i = 0; i < lane_number; i++) {
    for (int j = 0; j < locations; j++) {
      if (goal[i*locations+j]==car) {
        goal_lane=i;
        goal_column=j;
        break;
      }
    }
  }

  h = abs(init_lane-goal_lane)+abs(init_column-goal_column);
  cout << h << endl;
  return h;
}

void astar_search(){

}

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

        vector <string> init_parking_mat;

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            init_parking_mat.push_back(initialcars[0]);
            initialcars.erase(initialcars.begin());
          }
        }

        //First check print
        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            cout << init_parking_mat[i*locations+j] << " ";
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

        vector<string> goal_parking_mat;

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            goal_parking_mat.push_back(goalcars[0]);
            goalcars.erase(goalcars.begin());
          }
        }

        //Second check print
        cout << endl;
        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            cout << goal_parking_mat[i*locations+j] << " ";
          }
          cout << endl;
        }

        heuristic(init_parking_mat, goal_parking_mat, lane_number, locations, "A3");

        return 0;
}
