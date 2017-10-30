
/*Sets*/
set FLIGHTS;
set TOTALCREW;
set PILOTS within CREWTOTAL;
set ATTENDANTS within CREWTOTAL;
set MV within FLIGHTS;
set VM within FLIGHTS;

/*Parameters*/
param costs {i in FLIGHTS, j in CREWTOTAL};
param flightTime {i in FLIGHTS};
param breaks {i in FLIGHTS, j in FLIGHTS};
param flightTime {i in FLIGHTS};
param pilotBreak {i in PILOTS};

/* decision variables */
var  x {i in TOTALCREW, j in FLIGHTS} binary;

/*Objective function*/
minimize Cost: sum{i in FLIGHTS, j in CREW} costs[i, j]*x[i,j];

/*Constraints*/

/*There is at least one pilot in every flight*/
s.t. minPilots {i in FLIGHTS}: sum{j in PILOTS} x[i,j] >= 0;

/*There is at least one flight attendant in every flight*/
s.t. minAtt {i in FLIGHTS}: sum{j in ATTENDANTS} x[i,j] >= 0;

/*The number of hours of the attendants must be higher than the number of hours of pilots*/
s.t. hoursATT : sum{i in FLIGHTS} (flightTime[i] * sum{j in PILOTS} x[i,j]) <= sum{i in FLIGHTS} (flightTime[i] * sum{j in ATTENDANTS} x[i,j]);

/*Breaks are respected*/
s.t. breakRespected{i in FLIGHTS, j in FLIGHTS, k in PILOTS: i<>j and i<j}: pilotBreak[k]*(x[i,k]+x[j,k]-1) <= breaks[i,j];

/*Location of Crew is the required to work in a flight*/
s.t. position{i in TOTALCREW, j in FLIGHTS}: sum{m in MV:m<j} x[m,i] - sum{v in VM:v<j} x[v,i] <= 1;
s.t. position2{i in TOTALCREW, j in FLIGHTS}: sum{v in VM:v<j} x[v,i] - sum{m in MV:m<j} x[m,i] <= 0;
