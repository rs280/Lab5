
package driver;

//import model.Automobile;
import adapter.*;
import scale.EditOptions;

public class Driver {

	public static void main(String[] args) {
		/* This test driver is similar to Hello.java */
		CreateAuto buildAutoInterface = new CreateAuto();
		buildAutoInterface.init(); // only call this once
		// Build Automobile Object from a file.
		String FordZTWAutomobileKey = buildAutoInterface.buildAuto("FordZTW2021.txt");
		
		if (FordZTWAutomobileKey != null) {
			// Print attributes before serialization
			buildAutoInterface.printAuto(FordZTWAutomobileKey);
			/*
			 * We will start a bunch of operation threads to demonstrate multi-threading
			 * Some operations depend on the order and silently throw an exception that will be logged
			 */
			String input[] = { FordZTWAutomobileKey, "Color", "French Blue Clearcoat Metallic",
				"Cool Cali Blue" };
			String input2[] = { FordZTWAutomobileKey, "Color", "French Blue Clearcoat Metallic",
				"Sunshine Hawaiian Luau Gold" };
			String input3[] = { FordZTWAutomobileKey, "Color", "Colors"};
			String input4[] = { FordZTWAutomobileKey, "Color", "French Blue Clearcoat Metallic"};
			buildAutoInterface.operation(0, input); // Updates option name
			buildAutoInterface.operation(0, input2); // Updates option name
			buildAutoInterface.operation(1, input3); // Updates option set name
			buildAutoInterface.operation(3, input4); // set color choice
			
			
			
		} else {
			System.out.println("Could not build automobile");
		}
		
		
		
	}

}
