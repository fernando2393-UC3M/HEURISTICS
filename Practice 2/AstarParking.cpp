#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

/*
   Operations:
   Move forward in the same line: 1
   Move backward in the same line: 2
   Move to the beginning of another line: 3
   Move to the end of another line: 4
 */

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
        return h;
}

void astar_search(vector <string> init_parking_mat, vector <string> goal_parking_mat, int lane_number, int locations){

        int totalheuristic = 0;
        int heuristic_mat[lane_number][locations];


        //Here we start the search selecting by ascendant heuristic order
        for(int z = 1; z<(lane_number-1)+(locations-1); z++) {
                for (int x = 0; x < lane_number; x++) {
                        cout << endl;
                        for (int y = 0; y < locations; y++) {
                                //Checks the heuristic of each car from initial to goal position -> heuristic of the whole parking
                                for (int i = 0; i < lane_number; i++) {
                                        for (int j = 0; j < locations; j++) {
                                                //Here we update the total heuristic for this iteration
                                                totalheuristic += heuristic(init_parking_mat, goal_parking_mat, lane_number, locations, init_parking_mat[i*locations+j]);
                                                //Map of heuristics creation for each car
                                                heuristic_mat[i][j] = heuristic(init_parking_mat, goal_parking_mat, lane_number, locations, init_parking_mat[i*locations+j]);
                                        }
                                }
                                cout << heuristic_mat[x][y];
                                if(heuristic_mat[x][y]==z) {

                                }
                                totalheuristic = 0;
                        }
                }
        }
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

        astar_search( init_parking_mat, goal_parking_mat, lane_number, locations);

        return 0;
}
