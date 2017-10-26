/*Parameters*/
set TYPE;
set COMP;
set NOSE within COMP;
set TAIL within COMP;

param Volume {i in TYPE};
param Number {i in TYPE}; /*Num cases of each type to store: Type 1, Type 2, Type 3*/
param Cost {i in TYPE}; 
param Weight {i in TYPE};
param NumberM {i in TYPE}; /*Num of cases to distribute*/
param Allowed_Weight {i in COMP};
param Allowed_Volume {i in COMP};

/*TYPE{M1, M2, M3}*/
/*COMP{C1, C2, C3, C4, C5, C6}*/

/* decision variables */
var unitsPerComp {i in TYPE, j in COMP} integer >= 0; 

/*Objective function*/
minimize Price: sum{i in TYPE} Cost[i] * (NumberM[i] - sum{j in COMP} unitsPerComp[i, j]);

/*Constraints*/
s.t. WeightC{j in COMP}: sum{i in TYPE} unitsPerComp[i, j]*Weight[i] <= Allowed_Weight[j];
s.t. VolumeC{j in COMP}: sum{i in TYPE} unitsPerComp[i, j]*Volume[i] <= Allowed_Volume[j];
s.t. BalanceC{j in NOSE, k in TAIL}: sum{i in TYPE} unitsPerComp[i, j]*Weight[i] >= 1.1*(sum{i in TYPE} unitsPerComp[i, k]*Weight[i]);
s.t. NumberC{i in TYPE}: sum{j in COMP} unitsPerComp[i, j] <= NumberM[i];

