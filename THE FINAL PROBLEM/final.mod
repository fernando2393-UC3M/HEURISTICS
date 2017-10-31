
/*Sets*/
set FLIGHTS;
set TOTALCREW;
set PILOTS within TOTALCREW;
set ATTENDANTS within TOTALCREW;
set MV within FLIGHTS;
set VM within FLIGHTS;

/*For Luggage*/
set TYPE;
set COMP;
set NOSE within COMP;
set TAIL within COMP;

/*Parameters*/
param costs {i in FLIGHTS, j in TOTALCREW};
param flightTime {i in FLIGHTS};
param breaks {i in FLIGHTS, j in FLIGHTS};
param pilotBreak {i in PILOTS};

/*For Luggage Distribution (Ex1)*/
param Volume {i in TYPE};
param Number {i in TYPE}; /*Num cases of each type to store: Type 1, Type 2, Type 3*/
param Cost {i in TYPE}; 
param Weight {i in TYPE};
param NumberM {i in TYPE}; /*Num of cases to distribute*/
param Allowed_Weight {i in COMP};
param Allowed_Volume {i in COMP};

/* Decision Variables */
var x {i in FLIGHTS, j in TOTALCREW} binary;

/*For Luggage Distribution (Ex1)*/
var unitsPerComp {i in TYPE, j in COMP} integer >= 0;

/*Objective function*/
minimize Cost: sum{i in FLIGHTS, j in TOTALCREW} (costs[i,j]/60)*flightTime[i]*x[i,j]+(sum{i in TYPE} Cost[i] * (NumberM[i] - sum{j in COMP} unitsPerComp[i, j]));

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

/*For Luggage Distribution (Ex1)*/
s.t. WeightC{j in COMP}: sum{i in TYPE} unitsPerComp[i, j]*Weight[i] <= Allowed_Weight[j];
s.t. VolumeC{j in COMP}: sum{i in TYPE} unitsPerComp[i, j]*Volume[i] <= Allowed_Volume[j];
s.t. BalanceC{j in NOSE, k in TAIL}: sum{i in TYPE} unitsPerComp[i, j]*Weight[i] >= 1.1*(sum{i in TYPE} unitsPerComp[i, k]*Weight[i]);
s.t. NumberC{i in TYPE}: sum{j in COMP} unitsPerComp[i, j] <= NumberM[i];

end;
