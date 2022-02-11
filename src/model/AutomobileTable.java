package model;

import java.util.*;

import exception.AutoException;

/** @class AutomobileTable
 *        Keeps a hash table of all automobile objects. \n
 *        This is essentially our database to manage our automobiles. **/
public class AutomobileTable {
	private Map<String, Automobile> automobileTable;

	/* constructors */
	public AutomobileTable() {
		automobileTable = new LinkedHashMap<String, Automobile>(64);
	}

	public AutomobileTable(int capacitySize) {
		automobileTable = new LinkedHashMap<String, Automobile>(capacitySize);
	}

	public Automobile getByKey(String automobileKey) {
		return automobileTable.get(automobileKey);
	}

	/** get the hash table key for an automobile object. \n
	 * key is a combination of Make, Model, and Year.
	 * @return the key string **/
	public String getKey(Automobile automobileObject) {
		return automobileObject.getMake() + "-" + automobileObject.getModel() + "-" + automobileObject.getYear();
	}

	/** Inserts an automobile into the hash table. Overwrites existing Automobiles
	 * with the same Make, Model, and Year.
	 * @return the key in the hash table **/
	public String insertOverwrite(Automobile automobileObject) {
		/* key = Make-Model-Year */
		String automobileKey = getKey(automobileObject);
		automobileTable.put(automobileKey, automobileObject);
		return automobileKey;
	}

	/** Inserts an automobile into the hash table. If an automobile with the same
	 * key exists then the exception fixString() method is used to correct it.
	 * @return the key in the hash table **/
	public String insertWrapper(Automobile automobileObject) {
		/* key = Make-Model-Year */
		int tryNumber = 1;
		String automobileKey = getKey(automobileObject);
		// self-healing
		while (tryNumber > 0) {
			try {
				insert(automobileKey, automobileObject);
			} catch (exception.AutoException e) {
				// just try once to fix
				if (tryNumber > 1) {
					tryNumber = -5;
				}
				e.setAutomobile(automobileObject);
				automobileKey = e.fixString(500);
				tryNumber += 1;
			}
		}
		return automobileKey;
	}

	public boolean insert(String automobileKey, Automobile automobileObject) throws AutoException {
		boolean returnValue = false;
		if (automobileTable.containsKey(automobileKey)) {
			throw new exception.AutoException(500);
		} else {
			automobileTable.put(automobileKey, automobileObject);
			returnValue = true;
		}
		return returnValue;
	}

	/* print() and toString() */
	public void print() {
		System.out.print(toString());
	}

	public String toString() {
		return "Automobile Table";
	}
}
