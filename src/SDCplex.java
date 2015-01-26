import ilog.cplex.*;
import ilog.concert.*;

public class SDCplex extends SDConstant{
	IloCplex cplex;
	IloNumVar[] b, l, ex, sh, F;
	IloNumVar[][] w;
	
	public void model(SDInstance instance){
		// Initialization of n, m, w, x and a variables
		modelConstraints(instance);
		// Initialization of Cplex solver
		initCplex();
		// Initialization of Cplex variables
		modelCplexVariables();
		// Initialization of Cplex constraints
		modelCplexConstraints();
		// Solve Cplex model
		solve();
	}
	
	public void initCplex(){
		try {
			cplex = new IloCplex();
		} 
		catch(IloException e){
			e.printStackTrace();
		}
	}
	
	public void modelCplexVariables() {
		try {
			// Initialization of variable b		
			b = cplex.numVarArray(m, 0, 1, IloNumVarType.Bool);
			
			// Initialization of variable w
			w = new IloNumVar[m][];
			for (int s = 0; s < m; s++){
				w[s] = cplex.numVarArray(daysPerCycle, 0, Integer.MAX_VALUE, IloNumVarType.Int);				
			}			
			
			// Initialization of variable l
			l = cplex.numVarArray(n, 0, Integer.MAX_VALUE, IloNumVarType.Int);
			
			// Initialization of variable ex
			ex = cplex.numVarArray(n, 0, Integer.MAX_VALUE, IloNumVarType.Int);
			
			// Initialization of variable sh
			sh = cplex.numVarArray(n, 0, Integer.MAX_VALUE, IloNumVarType.Int);
			
			// Initialization of variable F
			F = cplex.numVarArray(3, 0, Double.MAX_VALUE, IloNumVarType.Int);
		} 
		catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	public void modelCplexConstraints() {
		try {
			IloLinearNumExpr obj = cplex.linearNumExpr();
			for ( int i = 0; i < 3; i++){
				obj.addTerm(weight[i], F[i]);
			}
			
			// Objective Function			
			cplex.addMinimize(obj);
						
			// Constraint 1 (6)
			IloLinearNumExpr[] expr1 = new IloLinearNumExpr[m];
			for ( int s = 0; s < m; s++){
				expr1[s] = cplex.linearNumExpr();
				expr1[s].addTerm(b[s], instance.getMaxEmployeeNeeded()*7); 
				cplex.addLe(cplex.sum(w[s]), expr1[s]);
			}
			
			// Constraint 2 (7)
			Integer tempDay;
			IloLinearNumExpr[] expr2 = new IloLinearNumExpr[n];
			for ( int t = 0; t < n; t++){
				expr2[t] = cplex.linearNumExpr();
				for ( int s = 0; s < m; s++){
					if(nextDay[s] == true && t % (24 * 60 / instance.getSlotLength()) < 12 * 60 / instance.getSlotLength() ){
						tempDay = ((daysPerCycle *t/n) +6) %7;
					}
					else 
						tempDay = (daysPerCycle *t/n);
		
					expr2[t].addTerm(a[t][s], w[s][tempDay]);
					
				}
				cplex.addEq (l[t], expr2[t]);
			}
			
			// Constraint 3 (8)
			IloLinearNumExpr[] expr3 = new IloLinearNumExpr[n];
			for ( int t = 0; t < n; t++){
				expr3[t] = cplex.linearNumExpr();
				expr3[t].setConstant(-instance.getSlotLength()*x[t]);
				expr3[t].addTerm(instance.getSlotLength(), l[t]);
				cplex.addGe (ex[t], expr3[t]);
			}
			cplex.addEq (F[0], cplex.sum(ex));
			// Constraint 4 (9)
			IloLinearNumExpr[] expr4 = new IloLinearNumExpr[n];
			for ( int t = 0; t < n; t++){
				expr4[t] = cplex.linearNumExpr();
				expr4[t].setConstant(instance.getSlotLength()*x[t]);
				expr4[t].addTerm(-instance.getSlotLength(), l[t]);
				cplex.addGe (sh[t], expr4[t]);
			}
			cplex.addEq (F[1], cplex.sum(sh));	
			
			// Constraint 5 (10)
			cplex.addEq (F[2], cplex.sum(b));
			
		} 
		catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	public void solve(){
		try{	

			if (cplex.solve()){				
				// Show excesses and shortages of TimeSlot t 
				double[] Excesses = cplex.getValues(ex); 
				double[] Shortages = cplex.getValues(sh); 
				double[] Load = cplex.getValues(l); 
				for (int j = 0; j < n; ++j) {
					if (Excesses[j] != 0 || Shortages[j] != 0)
					System.out.println("Excesses/Shortages of TimeSlot " + j + " = " + Excesses[j]/instance.getSlotLength()+ " / " + Shortages[j]/instance.getSlotLength()+ "         Load = " + Load[j] +"        nrOfNeededEmployees: " + x[j]); 
				}
				
				System.out.println();

				double[] Obj = cplex.getValues(F); 
				// The value of F and weight
				for (int j = 0; j < 3; ++j) {
					System.out.println("F_" + j + " = " + Obj[j] + "  W_" + j + " = " + weight[j]);
				}
				System.out.println();

				// The value of objective function
				System.out.println("#Fitness of assumed optimal Solution:");
				System.out.println("# " + cplex.getObjValue());
				System.out.println();
				System.out.println("#Optimal Solution:");
				System.out.println("#Shift Start Length Duties:");
				
				double[] IsShiftActive = cplex.getValues(b);
				
				// The value of
				int shift = 1;
				for (int s =0; s< tempShift.size(); s++){		
					double[] nrOfAssignedEmployees = cplex.getValues(w[s]);
					if (IsShiftActive[s]==1){
						System.out.print("# " + shift + ":  " + tempShift.get(s).getStart() * instance.getSlotLength()/60 + ":" + (tempShift.get(s).getStart() % (60/instance.getSlotLength())) * instance.getSlotLength() +  "    " + tempShift.get(s).getLength() * instance.getSlotLength()  + "       ");
						for (int i = 0; i < 7; ++i) 
							System.out.print((int)nrOfAssignedEmployees[i] + "  ");
						System.out.println();
						shift ++;
					}
				}
			}
			cplex.end();
		}
		catch(IloException e){
			e.printStackTrace();
		}
	}
}
