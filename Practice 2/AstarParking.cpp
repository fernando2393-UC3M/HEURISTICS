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

//This function calculates the movement cost for each non-blocked car
int cost(int lane_number, int loc, int initial_lane_number, int initial_loc, int locations){
        if(lane_number==initial_lane_number) {
                if(loc>initial_loc) {
                        return 1;
                }
                else if(loc<initial_loc){
                        return 2;
                }
        }
        else{
                if(loc==0) {
                        return 3;
                }
                else if(loc==locations) {
                        return 4;
                }
        }
        return 5;
}


void astar(vector <string> init_parking_mat, vector <string> goal_parking_mat, int lane_number, int locations){

        vector <vector <string> > closed_set;
        vector <vector <string> > open_set;
        vector <string> best_grid;
        int initial_row = 0;
        int initial_loc = 0;
        int final_row = 0;
        int final_loc = 0;

        open_set.push_back(init_parking_mat);

        int gscore = 0;
        int totalheuristic = 0;
        int fscore = 0;
        int counter = 0;

        for (int i = 0; i < lane_number; i++) {
                for (int j = 0; j < locations; j++) {
                        //Here we update the total heuristic for this iteration
                        totalheuristic += heuristic(init_parking_mat, goal_parking_mat, lane_number, locations, init_parking_mat[i*locations+j]);
                }
        }

        while(true){

          for(int i = 0; i < lane_number; i++){
            for(int j = 0; j < locations; j++){
              if(init_parking_mat[i*locations+j]!=goal_parking_mat[i*locations+j]){
                counter=1;
              }
            }
          }

          if(counter==0){
            break;
          }

        for (int i = 0; i < lane_number; i++) {
          for (int j = 0; j < locations; j++) {
            if(j==0 || j==locations-1){
              for (int k = 0; k < lane_number; k++) {
                for (int l = 0; l < locations; l++) {
                  if(init_parking_mat[i*locations+j]!="__"){
                    if(init_parking_mat[k*locations+l]=="__"){
                      if(i==0 & j==0){
                        gscore = cost(k, l, i, j, locations);
                        initial_row = i;
                        initial_loc = j;
                        final_row = k;
                        final_loc = l;
                      }
                      else{
                        int aux = cost(k, l, i, j, locations);
                        if(aux < gscore){
                          gscore = aux;
                          initial_row = i;
                          initial_loc = j;
                          final_row = k;
                          final_loc = l;
                        }
                      }
                    }
                  }
                }
              }
            }
            else{
              for (int k = 0; k < lane_number; k++){
                for ( int l = 0; l < locations; l++){
                  if((init_parking_mat[k*locations+l]=="__") && (init_parking_mat[i*locations+j]!="__" && (init_parking_mat[i*locations+j+1]=="__" || (init_parking_mat[i*locations+j-1]=="__")))){
                    int aux = cost(k, l, i, j, locations);
                    if(aux < gscore){
                      gscore = aux;
                      initial_row = i;
                      initial_loc = j;
                      final_row = k;
                      final_loc = l;
                    }
                  }
                }
              }
            }
          }
        }

        init_parking_mat[final_row*locations+final_loc] = init_parking_mat[initial_row*locations+initial_loc];
        init_parking_mat[initial_row*locations+initial_loc] = "__";

        cout << "The heuristic is: "<<totalheuristic << endl;
        cout << "The cost is: " <<gscore << endl;
        fscore = gscore + totalheuristic;
        cout << "The fscore is: " <<fscore << endl;
        cout << "Initial row: " << initial_row << endl;
        cout << "Initial loc: " << initial_loc << endl;
        cout << "Final row: " << final_row << endl;
        cout << "Final loc: " << final_loc << endl;
        cout << "The next grid is: " << endl;

        for (int i = 0; i < lane_number; i++) {
                for (int j = 0; j < locations; j++) {
                        cout << init_parking_mat[i*locations+j] << " ";
                }
                cout << endl;
        }

      }

      cout << "Configurations. The final state has been reached." << '\n';

}

int main(int argc, char const *argv[]) {

        int counter = 0;
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

        for (int i = 0; i < lane_number; i++) {
                for (int j = 0; j < locations; j++) {
                        if(init_parking_mat[i*locations+j]=="__") {
                        }
                        else{
                                counter++;
                        }
                }
        }

        if (counter==(lane_number*locations)) {
                cout << "Configuration unfeasible. No free slots." << endl;
                return 0;
        }

        astar(init_parking_mat, goal_parking_mat, lane_number, locations);

        // astar_search( init_parking_mat, goal_parking_mat, lane_number, locations);

        return 0;
}
