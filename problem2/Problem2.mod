
/*Sets*/
set FLIGHTS;
set TOTALCREW;
set PILOTS within TOTALCREW;
set ATTENDANTS within TOTALCREW;
set MV within FLIGHTS;
set VM within FLIGHTS;

/*Parameters*/
param costs {i in FLIGHTS, j in TOTALCREW};
param flightTime {i in FLIGHTS};
param breaks {i in FLIGHTS, j in FLIGHTS};
param pilotBreak {i in PILOTS};

/* Decision Variables */
var x {i in FLIGHTS, j in TOTALCREW} binary;

/*Objective function*/
minimize Cost: sum{i in FLIGHTS, j in TOTALCREW} (costs[i,j]/60)*flightTime[i]*x[i,j]+160;

/*Constraints*/

/*There is at least one pilot in every flight*/
s.t. minPilots{i in FLIGHTS}: sum{j in PILOTS} x[i,j] >= 1;

/*There is at least one flight attendant in every flight*/
s.t. minAtt{i in FLIGHTS}: sum{j in ATTENDANTS} x[i,j] >= 1;

/*The number of hours of the attendants must be higher than the number of hours of pilots*/
s.t. hoursATT: sum{i in FLIGHTS} (flightTime[i]*sum{j in PILOTS}x[i,j]) <= sum{i in FLIGHTS} (flightTime[i]*sum{j in ATTENDANTS}x[i,j]);

/*The breaks are respected*/
s.t. breakRespected{i in FLIGHTS, j in FLIGHTS, k in PILOTS: i<>j and i<j}: pilotBreak[k]*(x[i,k]+x[j,k]-1) <= breaks[i,j];

/*The crew member is in departure airport to take a flight*/
s.t. locationMV{i in TOTALCREW, j in FLIGHTS}: sum{k in MV:k<j} x[k,i] - sum{l in VM:l<j} x[l,i] <= 1;
s.t. locationVM{i in TOTALCREW, j in FLIGHTS}: sum{k in VM:k<j} x[k,i] - sum{l in MV:l<j} x[l,i] <= 0;

end;
