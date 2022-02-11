package adapter;

import model.*;
import scale.EditOptions;
import exception.*;

public abstract class ProxyAutomobile {
	private static model.AutomobileTable automobileTable;
	private static int threadNumber; // keep track of thread numbers
	private util.FileIO autoutil;

	protected ProxyAutomobile() {
		autoutil = new util.FileIO();
	}

	public void init() {
		// initialize the static automobile table
		automobileTable = new AutomobileTable(64);
		threadNumber = 0;
	}

	public boolean updateOptionSetName(String automobileKey, String optionSetName, String nameNew) {
		boolean returnValue = false;
		model.Automobile automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			automobileObject.setOptionSetName(optionSetName, nameNew);
			returnValue = true;
		}
		return returnValue;
	}

	public boolean updateOptionPrice(String automobileKey, String optionSetName, String optionName, double priceNew) {
		boolean returnValue = false;
		model.Automobile automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			automobileObject.setOptionSetOptionPrice(optionSetName, optionName, priceNew);
			returnValue = true;
		}
		return returnValue;
	}

	public boolean updateOptionName(String automobileKey, String optionSetName, String optionName,
		String optionNameNew) {
		boolean returnValue = false;
		model.Automobile automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			automobileObject.setOptionSetOptionName(optionSetName, optionName, optionNameNew);
			returnValue = true;
		}
		return returnValue;
	}

	/* CreateAuto Implementation */
	public String buildAuto(String filename) {
		String returnValue = null;
		model.Automobile automobileObject = new model.Automobile();
		try {
			autoutil.read(filename, automobileObject);
			returnValue = automobileTable.insertWrapper(automobileObject);
		} catch (exception.AutoException e) {
			// double check that return value is null
			returnValue = null;
		}
		return returnValue;
	}

	public boolean printAuto(String automobileKey) {
		boolean returnValue = false;
		model.Automobile automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			returnValue = true;
			System.out.println(automobileObject.toString());
		}
		return returnValue;
	}

	public boolean serialize(String automobileKey, String fileName) {
		boolean returnValue = false;
		model.Automobile automobileObject;
		try {
			automobileObject = automobileTable.getByKey(automobileKey);
			autoutil.serialize(fileName, automobileObject);
			returnValue = true;
		} catch (exception.AutoException e) {
			// nothing
		}
		if (returnValue) {
			System.out.println("Serialized data is saved in " + fileName);
		} else {
			System.out.println("Automobile could not be serialized");
		}
		return returnValue;
	}

	public String deserialize(String fileName) {
		String returnValue = null;
		model.Automobile automobileObject = autoutil.deserialize(fileName);
		if (automobileObject != null) {
			System.out.println("Deserialized data read from " + fileName);
			returnValue = automobileTable.insertWrapper(automobileObject);
		} else {
			System.out.println("Automobile could not be deserialized");
		}
		return returnValue;
	}

	/* AutoChoice Implementation */
	public boolean setOptionChoice(String automobileKey, String optionSetName, String optionName) {
		boolean returnValue = false;
		model.Automobile automobileObject;
		automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			automobileObject.setOptionSetChoice(optionSetName, optionName);
			returnValue = true;
		}
		return returnValue;
	}

	public String getOptionChoice(String automobileKey, String optionSetName) {
		String returnValue = null;
		model.Automobile automobileObject;
		automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			returnValue = automobileObject.getOptionSetChoiceName(optionSetName);
		}
		return returnValue;
	}

	public Double getOptionChoicePrice(String automobileKey, String optionSetName) {
		Double returnValue = null;
		model.Automobile automobileObject;
		automobileObject = automobileTable.getByKey(automobileKey);
		if (automobileObject != null) {
			returnValue = automobileObject.getOptionSetChoicePrice(optionSetName);
		}
		return returnValue;
	}

	/* scale.Scaleable Implementation */
	public void operation(int operationNumber, String[] inputArguments) {
		/*
		 * scale.EditOptions mimics Hello.java
		 * It contains a switching statement to delegate the operation number
		 */
		EditOptions editObtionsObject = new scale.EditOptions(this, operationNumber, threadNumber++, inputArguments);
		editObtionsObject.start();
	}
}