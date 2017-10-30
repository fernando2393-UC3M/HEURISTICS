/*Flight Number = V1, V2, V3, V4, V5, V6
	Pilot = P1, P2, P3
	Assistant = A1, A2, A3

	Variables go: VxPyAz = In flight x is going pilot y and assistant z*/

set FLIGHT;
set CREW;
set PILOT within CREW;
set ATTEN within CREW;
set MAD within FLIGHT;
set VALE within FLIGHT;
/*Set that allows us to go thruogh the variables inside the set, decision variables*/

/*Below, we have as many parameters in each 'param' as items in the set FLIGHT*/
param Cost {i in FLIGHT, j in CREW};
param comparingHours {i in FLIGHT};
param breakTime {i in FLIGHT, j in FLIGHT};
param breakPilot {i in PILOT};

/*Variable that sets all i elements in FLIGHT all non-equal to zero or negative*/
var x {i in FLIGHT, j in CREW} binary;

minimize OverallCost: sum{i in FLIGHT, j in CREW} (Cost[i,j]/60)*comparingHours[i]*x[i,j];
/*Objective Function*/

s.t. inFlightPilots{i in FLIGHT}: sum{j in PILOT} x[i,j] >= 1;
/*Number of pilots in a flight must be 1 or more*/

s.t. inFlightAssistants{i in FLIGHT}: sum{j in ATTEN} x[i,j] >= 1;
/*Number of assistants in a flight must be 1 or more*/

s.t. hoursAssistantsLarger: sum{i in FLIGHT} (comparingHours[i]*sum{j in PILOT}x[i,j]) <= sum{i in FLIGHT} (comparingHours[i]*sum{j in ATTEN}x[i,j]);
/*Hours of assistants have to be more than the hours of the pilots in flight*/

s.t. minBreak{i in FLIGHT, j in FLIGHT, k in PILOT: i<>j and i<j}: breakPilot[k]*(x[i,k]+x[j,k]-1) <= breakTime[i,j];

/*Someone who has taken a flight from Madrid to Valencia cannot take another flight from Madrid, and viceversa*/

s.t. position{i in CREW, j in FLIGHT}: sum{k in MAD:k<j} x[k,i] - sum{l in VALE:l<j} x[l,i] <= 1;
s.t. position2{i in CREW, j in FLIGHT}: sum{k in VALE:k<j} x[k,i] - sum{l in MAD:l<j} x[l,i] <= 0;

end;
