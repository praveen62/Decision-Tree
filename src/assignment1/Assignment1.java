/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1;

/**
 *
 * @author praveen
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



class DecNode {
	List<DecNode> nextNode;	// Array of Reference to child nodes
	int attId;						// Attributeindex of Attribute used classify
	String value;					// Class at Attribute at Attributeindex
	int output;						// Output Label number, 1 for '+' and 0 for '-'
	public DecNode( int attId, String value) {
		super();
		nextNode = new ArrayList<DecNode>();
		this.attId = attId;
		this.value = value;
		output = -1;
	}
	
        public int getOutput() {
		return output;
	}
	public void setOutput(int value) {
		this.output = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public DecNode getNextNode(int index) {
		return nextNode.get(index);
	}
	public void addNextNode(DecNode nextNode) {
		this.nextNode.add(nextNode);
	}
        public int getAttId() {
		return attId;
	}
	public void setAttId(int attId) {
		this.attId = attId;
	}
	public String toString() {
		return "DecisionNode [nextNode=" + nextNode + ", attId=" + attId
				+ ", value=" + value + ", output=" + output + "]";
	}	
}


class Record {
	private Map<Integer, String> record;
	private int set;

	/** Constructor initializes Attribute index to value Map
	 * @param str
	 * @param value
	 */
	public Record(String str, int value) {	
		if (str.length() != Constants.NOOFATT ) {
			System.out.println("invalid record");
		}else{
			record = new HashMap<Integer, String>();
			for (int i = 0; i < str.length(); i++) {
				record.put(i, ""+str.charAt(i));	// assigning value to attribute index in 'record' Map
			}
			this.set=value;							// set value for the record, 1 for '+' and 0 for '-' label
		}
	}

	public Map<Integer, String> getRecord() {
		return record;
	}
         /* returns value at from map where attribute index is 'key' @param value  */
	public String getVal(int key) {
		return record.get(key);
	}

	public void setRec(Map<Integer, String> record) {
		this.record = record;
	}
	 /* remove Attribute index 'attId' entry from Map @param attId */
        public void delVal(int attId) {
		record.remove(attId);
	}
	public String toString() {
		return "Record [record=" + record + ", set=" + set + "]";
	}
        public int getSet() {
		return set;
	}
	public void setSet(int value) {
		this.set = value;
	}
}

/** constants are declared here*/
class Constants {
	public static final int EntropyImpurity = 1;
	public static final int MisclassImp = 2;
	public static final double ChiSquarevalue99 = 11.34;
	public static final double ChiSquarevalue95 = 7.82;
	public static final double ChiSquarevalue0 = 0;
	public static double ChiSquarevalue;
	public static int NOOFATT;
}


public class Assignment1 {
	/**start of program
	 * @param args
	 */
	public static void main(String[] args) {
		new Assignment1();
	}

	int impu_func = 1;
	
	/*  this function compute Decision tree and shows result based on user's inputs.*/
	public Assignment1() {
		DataInputStream dis = new DataInputStream(System.in);
		String trainFile = null, validFile = null;
		int config=0;
		try {
			trainFile = "c://data//training.txt";//dis.readLine();
			validFile = "c://data//validation.txt";//dis.readLine();
			System.out.println("Select confidence for chi square statistics : \n1)enter 1 for 0%\n2)Enter 2 for 95%\n3)Enter 3 for 99%\n");
			config = Integer.parseInt(dis.readLine());
			System.out.println("select one among these : \n1)Enter 1 for Information gain(entropy impurity)"
					+ "\n2)Enter 2 for accuracy(misclassification impurity)\n");
			impu_func = Integer.parseInt(dis.readLine());

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Record> trainRecords = getListOfRecords(trainFile);
                if(config==1)
                {
                   Constants.ChiSquarevalue = Constants.ChiSquarevalue0; 
                }
                else if(config==2)
                {
                    Constants.ChiSquarevalue = Constants.ChiSquarevalue95;
                }
                else if(config==3)
                {
                    Constants.ChiSquarevalue = Constants.ChiSquarevalue99;
                }
                else
                {
                    System.out.println("Invalid number");
                }
		
		DecNode root = new DecNode(-1, "root");
		calculate(trainRecords,root);		// Formulate decision tree
		validTree(trainFile, root);		// validate training set
		validTree(validFile, root);		// validate validation set
		try {	dis.readLine();		} catch (IOException e) {		}
	}

	/* calculating correctly classified and misclassified */
	private void validTree(String fileName, DecNode root) {
		List<Record> validRecords = getListOfRecords(fileName);
		int positive=0,error=0;
		for (Record record : validRecords) {
			if(parseRecordInTree(record,root)){
				positive++;
			}else{
				error++;
			}
		}
		System.out.println("correctly classified:"+positive+" \nmisclassified:"+error);
		double total = positive+error;
		System.out.println("% accuracy : " + (positive/total*100)+"%");
	}

	/** This method traverse the 'record' through decision tree with root node 'root'
	 * 	
	 * @param record
	 * @param root
	 * @return
	 */
	private boolean parseRecordInTree(Record record, DecNode root) {
		DecNode parent = root;	//keep root as parent node
		while (true) {                    // while loop starts
			boolean flag = true;	// flag is used for records which cannot be classified using this tree
			for (DecNode decNode : parent.nextNode) {		// for each child of classes  parent  node
				if (record.getVal(decNode.getAttId()).equals(decNode.getValue())) { // if this condition matches
					flag=false;		// flag is set to false as record can be classified till now
					if (decNode.nextNode.size()==0) {	// if it is end of the tree i.e no child for child
						if (decNode.getOutput()==record.getSet()) { //compare label at decision node with validation records node
							return true;		// if they are same, they are correctly classified
						}else {
							return false;		//if not same, misclassified
						}
					}else{						// if it is not end ie not child
						parent = decNode;	// make child as parent node for next loop
					}
					break;                  
				}				
			}
			if (flag) {			// if flag is true decision tree cannot classify 'record'
				return false;
			}
		}
	}

	/** Method computes a decision tree for the given 'records' dataset
	 *  using recursive technique.
	 * @param records  // parameters used
	 * @param parent
	 */
	private void calculate(List<Record> records, DecNode parent) {
		double entropy ;
		if( impu_func == Constants.EntropyImpurity){
			entropy = entropy(records);	// passing records for fiding entropy
		}else {
			// Find Entropy of 'records' dataset
                        entropy = misclassification(records);
		}
		if (entropy == 0) {						//if entropy is 0 
			parent.setOutput(records.get(0).getSet());	// set output label for the node same as label of first record in dataset
			return;										// stop growing this node further
		}else{
			int attId = selectAttribute(records);	// selecting the best attribute
			if (getStats(records,attId) > Constants.ChiSquarevalue) {	// expand node if Chi Square Statistics value is greater the threshold
				Map<String, List<Record>> map = getDiscListsWithDel(records, attId);	// make discrete subsets of Classes Ci of 'records' dataset based on Attribute index
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {					// for each subset Dataset
					Map.Entry<String,List<Record>> pairs = (Map.Entry<String,List<Record>>)it.next();
					List<Record> recordClass = (List<Record>) pairs.getValue();
					DecNode child = new DecNode(attId, pairs.getKey().toString());	// create a child decision node for each class dataset
					parent.addNextNode(child);			// attaching new child node to parent's child node ArrayList
					calculate(recordClass, child);		// recursion for calculate
				}
			}else{	// chisquare value<threshold
				parent.setOutput(More(records));	// finding which is more and set output label
				return;											// stop growing this node further
			}
		}
	}

	/** loading file and reading the data in it
	 */
	private List<Record> getListOfRecords(String file) {
		List<Record> records = new ArrayList<Record>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));	
			boolean fstRec = true;
			String line;
			while ((line = br.readLine())!=null) {		// get data as line by line
				if (fstRec) {
					Constants.NOOFATT = line.substring(0,line.indexOf(" ")).length();	// Set number of Attributes to number of characters in first line of file
					fstRec=false;
				}
				Record record ;
				String value = line.substring(line.indexOf(" ")+1);
				if (value.equals("+")) {
					record = new Record(line.substring(0,line.indexOf(" ")), 1); // '+' label represents 1
				}else{
					record = new Record(line.substring(0,line.indexOf(" ")), 0); // '-' label represents 0
				}
				records.add(record);
			}
			System.out.println();
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("training.txt : File Not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}


	/**Calculates entropy  of records passed as parameter
	 * @param records
	 * @return Entropy
	 */
	public double entropy(List<Record> records) {
		Map<String, List<Record>> recordMap = getSortedMap(records);		
		double neg = records.size();
		double entropy = 0;
		Iterator it = recordMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			List<Record> recordClass = (List<Record>) pairs.getValue();
			double pos = recordClass.size();
			if (pos==0 || pos==neg) {
				return 0;
			}
			entropy -= (pos/neg)*log2(pos/neg);			
		}
		return entropy;
	}

	/**Calculates 
	 * 
	 * @param records
	 * @return 
	 */
	public double misclassification(List<Record> records) {
		Map<String, List<Record>> recordMap = getSortedMap(records);		
		double pos = recordMap.get("+").size();
		double neg = recordMap.get("-").size();	
		if (pos > neg) {
			return neg / (pos+neg);
		}else{
			return pos / (pos+neg);
		}
	}

	/**
	 * returns Log to the base 2 value of parameter x
	 * @param x
	 * @return Log_2(x)
	 */

	public static double log2(double x) {
		return Math.log(x)/Math.log(2);
	}

	/**return a map with Label(here '+' and '-') 
	 * @return Map containing labels as key and their corresponding records
	 */
	public static Map<String, List<Record>> getSortedMap(List<Record> records){
		List<Record> positiveRec = new ArrayList<Record>();
		List<Record> negativeRec = new ArrayList<Record>();
		for (Record record : records) {
			if (record.getSet()==1) {				// 1 represents for '+' and 0 represents for '-'
				positiveRec.add(record);			//Add positive records to positive ArrayList
			}else{
				negativeRec.add(record);			//Add negative records to negative ArrayList
			}
		}
		Map<String, List<Record>> map = new HashMap<String, List<Record>>();
		map.put("+", positiveRec);					// assign ArrayList with positive records to '+' key
		map.put("-", negativeRec);					// assign ArrayList with negative records to '-' key
		return map;
	}

	/**Returns which labels are majority, are they '+' or '-'
	 * @param records
	 * @return labelid
	 */
	public static int More(List<Record> records){
		Map<String, List<Record>> recordMap = getSortedMap(records);
		if (recordMap.get("+").size() > recordMap.get("-").size()) {
			return 1;				// if number of '+' is more than '-', returned is 1
		}else{
			return 0;				// else returned is 0
		}
	}

	/** Returns the index of an attribute which is going to be used as splitting node
	 * @param records
	 * @return bestNodeAttribute
	 */
	public int selectAttribute(List<Record> records) {
		double infoGain=-9999999;					//Initialize infoGain to least value
		int splitterAtt=0;							//Initialize splitting attribute index to 0
		boolean fl=true;							
		Map<Integer, String> attributes = records.get(0).getRecord();	//Get list of all attributes present for every record
		Iterator it = attributes.entrySet().iterator();	
		while (it.hasNext()) {						//Iterate over each attribute
			Map.Entry pairs = (Map.Entry)it.next();
			Integer integer = (Integer) pairs.getKey();	// integer = Key is index of attribute parsed in loop
			if (fl) {
				splitterAtt=integer;
				fl = false;
			}
			Map<String, List<Record>> map = getDiscLists(records, integer);	//returns a map with
		double n = records.size();		
		double ig;
		if( impu_func == Constants.EntropyImpurity){	//check impurity function selected
			ig = entropy(records);			// Initialize infoGain with Entropy impurity value of 'recordClass' dataset
		}else {
			ig = misclassification(records);	// Find Misclassifcation impurity value of 'recordClass' dataset
                        
		}
		Iterator its = map.entrySet().iterator();
		while (its.hasNext()) {
			Map.Entry<String,List<Record>> pair = (Map.Entry<String,List<Record>>)its.next();
			List<Record> recordClass = (List<Record>) pair.getValue();
			double rcSize = recordClass.size();
			
			double entropy ;
			if( impu_func == Constants.EntropyImpurity){
				entropy = entropy(recordClass);		// Find Entropy impurity value of 'recordClass' dataset
			}else {
				entropy = misclassification(recordClass);	// Find Misclassifcation impurity value of 'recordClass' dataset
                                
			}
			ig -= (rcSize/n)*entropy;	//calculate value as (Cn/n*entropy(classrecords)) 
			//for each class to get subtracted from entropy to derive information gain of the attribute
		}
			if (ig>infoGain) {							// implementation of argmax inforGain of attributes
				infoGain = ig;
				splitterAtt = integer;					// splitterAtt will have index of attribute with highest info gain
			}
		}
		return splitterAtt;								// return splitter Attribute index
	}

	
	/** Slicing dataset into smaller sub dataset based on classes in given attribute and also deleting particular attribute column 									{c,a,t}}
	 * @param records
	 * @param attId
	 * @return 
	 */
	public Map<String, List<Record>> getDiscListsWithDel(List<Record> records, int attId){
		Map<String, List<Record>> map = new HashMap<String, List<Record>>();
		for (Record record : records) {
			String str = record.getVal(attId);
			record.delVal(attId);
			if (map.get(str) == null) {
				List<Record> discrete = new ArrayList<Record>();
				discrete.add(record);
				map.put(str, discrete);
			}else{
				map.get(str).add(record);
			}
		}
		return map;
	}

	/** the above function this are almost same but the above one gives the record deletion
	 * @param records
	 * @param attId
	 * @return 
	 */
	public Map<String, List<Record>> getDiscLists(List<Record> records, int attId){  //returning a Map to he called function
		Map<String, List<Record>> map = new HashMap<String, List<Record>>(); 
		for (Record record : records) {   // for each  record
			String str = record.getVal(attId);
			if (map.get(str) == null) {  
				List<Record> discrete = new ArrayList<Record>();
				discrete.add(record);
				map.put(str, discrete);
			}else{
				map.get(str).add(record);
			}
		}
		return map;
	}

	/** The method returns Chi-square  value  for selected attribute
	 * @param records
	 * @param attId
	 * @return
	 */
	public double getStats(List<Record> records,int attId) {
		Map<String, List<Record>> map = getDiscLists(records, attId);	//returns a map with from the result of getDiscLists
		Map<String, List<Record>> recordMap = getSortedMap(records);	
		double p = recordMap.get("+").size();		//initialize p with count of '+' labeled 'records'
		double n = recordMap.get("-").size();		//initialize n with count of '-' labeled 'records'
		double statistics = 0;						//initialize statistics value to 0
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {						// while loop starts
			Map.Entry<String,List<Record>> pairs = (Map.Entry<String,List<Record>>)it.next();
			List<Record> recordClass = (List<Record>) pairs.getValue();
			Map<String, List<Record>> rm = getSortedMap(recordClass);	
			double pi = rm.get("+").size();		// get the size of +
			double ni = rm.get("-").size();		// get the size of -
			double p_i = p*((pi+ni)/(p+n));
			double n_i = n*((pi+ni)/(p+n));
			statistics += (((pi-p_i)*(pi-p_i)/p_i) + ((ni-n_i)*(ni-n_i)/n_i)  ); // calculating Chi square values
		}
		return statistics;
	}
}




