package adapter;

import model.*;
import scale.EditOptions;

import java.io.*;
import java.util.*;

import exception.*;

public abstract class ProxyAutomobile {
	private static model.AutomobileTable automobileTable;
	private static int threadNumber; // keep track of thread numbers
	private util.FileIO fileIOUtil;
	private util.StreamIO streamIOUtil;

	protected ProxyAutomobile() {
		fileIOUtil = new util.FileIO();
		streamIOUtil = new util.StreamIO();
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
	/** builds the Automobile object from a configuration file
	 * @param fileName The file name
	 * @param fileType the file type
	 *            choices: text, property
	 * @return Automobile Key on success, and null on failure */
	public String buildAuto(String fileName, String fileType) {
		String automobileKey = null;
		model.Automobile automobileObject = new model.Automobile();
		if (fileType.equals("text")) {
			// fileType = text
			try {
				fileIOUtil.addToAutomobile(fileName, automobileObject);
				automobileKey = automobileTable.insertWrapper(automobileObject);
			} catch (exception.AutoException e) {
				// return already null
			}
		} else if (fileType.equals("property")) {
			// fileType = property
			try {
				streamIOUtil.propertiesToAutomobile(streamIOUtil.fileToProperties(fileName), automobileObject);
				automobileKey = automobileTable.insertWrapper(automobileObject);
			} catch (exception.AutoException e) {
				// return already null
			}
		}
		return automobileKey;
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
			fileIOUtil.serialize(fileName, automobileObject);
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
		model.Automobile automobileObject;
		try {
			automobileObject = fileIOUtil.deserialize(fileName);
			returnValue = automobileTable.insertWrapper(automobileObject);
			System.out.println("Deserialized data read from " + fileName);
		} catch (AutoException e) {
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
		/* scale.EditOptions mimics Hello.java
		 * It contains a switching statement to delegate the operation number */
		EditOptions editObtionsObject = new scale.EditOptions(this, operationNumber, threadNumber++, inputArguments);
		editObtionsObject.start();
	}

	/* Assignment 5
	 * 6/12/2018
	 * server.AutoServer Implementation */
	public String buildAutomobileFromProperties(Properties automobileProperties) throws exception.AutoException {
		String automobileKey = null;
		model.Automobile automobileObject = new model.Automobile();
		streamIOUtil.propertiesToAutomobile(automobileProperties, automobileObject);
		automobileKey = automobileTable.insertOverwrite(automobileObject);
		return automobileKey;
	}

	public Properties propertiesFromStream(InputStream socketStreamIn) throws exception.AutoException {
		return streamIOUtil.deserializeToStream(socketStreamIn);
	}

	public String automobileFromStream(InputStream socketStreamIn) throws exception.AutoException {
		return automobileTable.insertOverwrite(fileIOUtil.deserializeFromStream(socketStreamIn));
	}

	public void automobileToStream(OutputStream socketStreamOut, String automobileKey) throws exception.AutoException {
		fileIOUtil.serializeToStream(socketStreamOut, automobileTable.getByKey(automobileKey));
	}

	public String getAutomobileList() {
		StringBuffer listString = new StringBuffer();
		for (Map.Entry<String, Automobile> entry : automobileTable.getMap().entrySet()) {
			// assuming nothing is null (for performance)
			listString.append("Car ID: ").append(entry.getKey()).append("\tName=").append(entry.getValue().getYear());
			listString.append(" ").append(entry.getValue().getMake()).append(" ").append(entry.getValue().getModel());
			listString.append("\tRetail Price=$").append(entry.getValue().getPrice()).append("\n");
		}
		return listString.toString();
	}
}