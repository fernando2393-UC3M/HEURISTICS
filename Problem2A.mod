/* 2nd problem */


/* SETS */

/* We create a set of FLIGHTS that represents all the flights we have: v1, v2, v3, v4, v5, v6 */
set FLIGHTS;

/* There are two sets within FLIGHTS: the ones going from MADRID (v1, v3, v5), and the ones going from VALENCIA (v2, v4, v6) */
set F_MAD within FLIGHTS;
set F_VAL within FLIGHTS;

/* We create a set for CREW members that represents all the crew members we have: p1, p2, p3, a1, a2, a3 */
set CREW;

/* There are two sets within CREW: PILOTS (p1, p2, p3), and ASSISTANTS (a1, a2, a3) */
set PILOTS within CREW;
set ASSISTANTS within CREW;


/* PARAMETERS */

/* Parameter containing the duration of each flight (in minutes) */
param Flight_Duration{i in FLIGHTS};

/* Parameter containing the interval duration between two flights (in minutes) */
param Flight_Interval{i in FLIGHTS, j in FLIGHTS};

/* Mandatory break times of each pilot (in minutes)*/
param Break{i in PILOTS};

/* Cost of each crew member in €/h */
param Cost{i in CREW, j in FLIGHTS};


/* DECISION VARIABLES */

/* The variable assignment represents if a crew member "i" is assigned to a flight "j" (value 1) or not (value 0). It must be a binary and non-negative number */
/* Having 6 crew members and 6 flights we will end up with 36 decision variables */
var assignment {i in CREW, j in FLIGHTS} binary;


/* OBJECTIVE FUNCTION */

/* Our goal is to minimize the cost of assingning crew members to the flight */
/* We convert the flight duration into hours, as it is stated in the parameter cost */
minimize Expense: sum{i in CREW, j in FLIGHTS} assignment[i,j]*Cost[i,j]*Flight_Duration[j]/60;


/* CONSTRAINTS */

/* There must be at least one pilot in each flight */
s.t. Min_Pilot{j in FLIGHTS}: sum{i in PILOTS} assignment[i,j] >= 1;

/* There must be at least one assistant in each flight */
s.t. Min_Assistant{j in FLIGHTS}: sum{i in ASSISTANTS} assignment[i,j] >= 1;

/* The total hours in flight of the assistants must be higher that the total ones of the pilots*/
s.t. Flight_Duration_Pilots_Assistants: (sum{i in PILOTS, k in FLIGHTS}Flight_Duration[k]*assignment[i,k]) - (sum{j in ASSISTANTS, k in FLIGHTS}Flight_Duration[k]*assignment[j,k]) <= 0;
	
/* Each pilot must rest at the arrival airport for a certain amount of time */
s.t. Rest{i in PILOTS, j in F_MAD, k in F_VAL: j<k}: Break[i]*(assignment[i,j] + assignment[i,k] - 1) <= Flight_Interval[j,k]; 

/*Who can fly*/
/*Pilots*/
/*0:v1 1:v3 2:v5 0:v2 1:v4 2:v6*/
/*s.t. Pilot_Can_Fly_FromVAL{p in PILOTS, v in F_VAL}: assignment[p,v] <= (sum{i in F_MAD : i<=v}assignment[p,i]) - (sum{j in F_VAL : j<v}assignment[p,j]);*/
/*s.t. Pilot_Can_Fly_FromMAD{p in PILOTS, v in F_MAD}: (sum{i in F_MAD : i<v}assignment[p,i]) - (sum{j in F_VAL : j<v}assignment[p,j]) + assignment[p,v] >= - 1  ;*/

s.t. Pilot_Can_Fly_FromVAL{p in PILOTS, v in F_VAL}: assignment[p,v] <= (sum{i in F_MAD : i<=v}assignment[p,i]) - (sum{j in F_VAL : j<v}assignment[p,j]);
s.t. Pilot_Can_Fly_FromMAD{p in PILOTS, v in F_MAD}: (sum{i in F_VAL : i<v}assignment[p,i]) - (sum{j in F_MAD : j<v}assignment[p,j]) - assignment[p,v] >= - 1;

/*Assistants*/
/*s.t. Assistant_Can_Fly_FromVAL{a in ASSISTANTS, v in F_VAL}: assignment[a,v] <= (sum{i in F_MAD : i<=v}assignment[a,i]) - (sum{j in F_VAL : j<v}assignment[a,j]);*/
/*s.t. Pilot_Can_Fly_MAD{p in PILOTS, v in F_MAD}: (sum{i in F_MAD : i<v}assignment[p,i]) - (sum{j in F_VAL : j<v}assignment[p,j]) + assignment[p,v] >= - 1  ;*/

s.t. Assistant_Can_Fly_FromVAL{a in ASSISTANTS, v in F_VAL}: assignment[a,v] <= (sum{i in F_MAD : i<=v}assignment[a,i]) - (sum{j in F_VAL : j<v}assignment[a,j]);
s.t. Assistant_Can_Fly_FromMAD{a in ASSISTANTS, v in F_MAD}: (sum{i in F_VAL : i<v}assignment[a,i]) - (sum{j in F_MAD : j<v}assignment[a,j]) - assignment[a,v] >= - 1;

end;


















