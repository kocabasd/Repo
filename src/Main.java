import java.io.IOException;
import java.text.ParseException;

public class Main {

	public static void main(String[] args) throws IOException, ParseException {
		// read instance
		//SDInstance sdInstance = new SDInstance("./SDdata/DataSet1/RandomExample13.txt");
		SDValidation sdValidation = new SDValidation("./SDdata/DataSet1/RandomExample13.txt");
		
		// solve instance with Cplex
		//SDCplex sdCplex = new SDCplex();
		//sdCplex.model(sdInstance);
		// solve instance with Gurobi
		//SDGurobi sdGurobi = new SDGurobi();
		//sdGurobi.model(sdInstance);
		//BSInstance bsInstance = new BSInstance("1-1.txt");
		//BSCplex bsCplex = new BSCplex();
		//bsCplex.model(bsInstance);
		

		
	}
}

