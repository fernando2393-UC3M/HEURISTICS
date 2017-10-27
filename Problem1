
/*Sets*/
set FLIGHTS;
set TOTALCREW;
set PILOTS within CREWTOTAL;
set ATTENDANTS within CREWTOTAL;

/*Parameters*/
param costs {i in FLIGHTS, j in CREWTOTAL};
param isEven {i in FLIGHTS, j in CREWTOTAL};
param flightTime {i in FLIGHTS};
param delay {i in FLIGHTS, j in FLIGHTS};


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

/*A pilot can take a flight*/
/*
Solo una idea basado en lo que ha dicho en clase, la sintaxis esta mal y a la funcion le faltaria el delay
s.t. takeFlight {j in PILOTS, f1 in FLIGHTS, f2 in FLIGHTS, f1<>f2 AND f2<f1}: isEven[i,j]*x[i,j] -1>= 0;
*/
