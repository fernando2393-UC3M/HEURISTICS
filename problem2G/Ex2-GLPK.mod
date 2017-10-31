/*Flight Number = V1, V2, V3, V4, V5, V6
	Pilot = P1, P2, P3
	Assistant = A1, A2, A3

	Variables go: VxPyAz = In flight x is going pilot y and assistant z*/

set FLIGHTS;
set TOTALCREW;
set PILOTS within TOTALCREW;
set ATTENDANTS within TOTALCREW;
set MV within FLIGHTS;
set VM within FLIGHTS;
/*Set that allows us to go thruogh the variables inside the set, decision variables*/

/*Below, we have as many parameters in each 'param' as items in the set FLIGHTS*/
param costs {i in FLIGHTS, j in TOTALCREW};
param flightTime {i in FLIGHTS};
param breaks {i in FLIGHTS, j in FLIGHTS};
param pilotBreak {i in PILOTS};

/*Variable that sets all i elements in FLIGHTS all non-equal to zero or negative*/
var x {i in FLIGHTS, j in TOTALCREW} binary;

minimize Overallcosts: sum{i in FLIGHTS, j in TOTALCREW} (costs[i,j]/60)*flightTime[i]*x[i,j];
/*Objective Function*/

s.t. inFlightPilots{i in FLIGHTS}: sum{j in PILOTS} x[i,j] >= 1;
/*Number of pilots in a flight must be 1 or more*/

s.t. inFlightAssistants{i in FLIGHTS}: sum{j in ATTENDANTS} x[i,j] >= 1;
/*Number of assistants in a flight must be 1 or more*/

s.t. hoursAssistantsLarger: sum{i in FLIGHTS} (flightTime[i]*sum{j in PILOTS}x[i,j]) <= sum{i in FLIGHTS} (flightTime[i]*sum{j in ATTENDANTS}x[i,j]);
/*Hours of assistants have to be more than the hours of the pilots in flight*/

s.t. minBreak{i in FLIGHTS, j in FLIGHTS, k in PILOTS: i<>j and i<j}: pilotBreak[k]*(x[i,k]+x[j,k]-1) <= breaks[i,j];

/*Someone who has taken a flight from Madrid to Valencia cannot take another flight from Madrid, and viceversa*/

s.t. position{i in TOTALCREW, j in FLIGHTS}: sum{k in MV:k<j} x[k,i] - sum{l in VM:l<j} x[l,i] <= 1;
s.t. position2{i in TOTALCREW, j in FLIGHTS}: sum{k in VM:k<j} x[k,i] - sum{l in MV:l<j} x[l,i] <= 0;

end;
