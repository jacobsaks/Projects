import java.util.HashSet;

public class RowElement {
	private Object value;
	private String type;
	private Object defaultVal;
	private int maxStrLgth;
	private int maxWholeNum;
	private int maxDecimal;
	private boolean cantBeNull;
	private boolean isUnique;
	private HashSet<Object> ifUnique;
	private boolean isIndexed;
	private BTree theBTree;
	private boolean isPrimaryKey;
	
	
	public static void main(String[] args)
	{	
		int i = 5;
		//RowElement<Integer> q = new RowElement(i);
		//q.setVal("hello");
		//q.getVal();
	}
	
	//Constructor for First Row of Table for Boolean and Integer values- which stores default Values and column Names 
	public RowElement(String name, String typ, Object defaultV, boolean qNull, boolean isUniq)
	{
		//value = column name
		value = name;
		//column Data Type
		type = typ;
		//Default value for the column
		defaultVal = defaultV;
		cantBeNull = qNull;
		isUnique = isUniq;
		ifUnique = new HashSet<Object>();
		isIndexed = false;
		theBTree = new BTree((String)value, "0");
		isPrimaryKey = false;
	}
	
	//Constructor for First Row String value
	public RowElement(String name, String typ, Object defaultV, int strMax, boolean qNull, boolean isUniq)
	{
		//value = column name
		value = name;
		//column Data Type
		type = typ;
		//Default value for the column
		defaultVal = defaultV;
		//max Length of String
		maxStrLgth = strMax;
		cantBeNull = qNull;
		isUnique = isUniq;
		ifUnique = new HashSet<Object>();
		isIndexed = false;
		theBTree = new BTree((String)value, "0");
		isPrimaryKey = false;
	}
	
	//Constructor for First Row Decimal value
		public RowElement(String name, String typ, Object defaultV, int decWholeNum, int decimal, boolean qNull, boolean isUniq)
		{
			//value = column name
			value = name;
			//column Data Type
			type = typ;
			//Default value for the column
			defaultVal = defaultV;
			//whole number max , decimal max length
			maxWholeNum = decWholeNum;
			maxDecimal = decimal;
			cantBeNull = qNull;
			isUnique = isUniq;
			ifUnique = new HashSet<Object>();
			isIndexed = false;
			theBTree = new BTree((String)value, "0");
			isPrimaryKey = false;
			
		}
	//constructor for non-first Row Elements 
	public RowElement(Object val)
	{
		value = val;
		type = null;
		defaultVal = null;
	}
	
	public Object getVal()
	{
		return value;
	}

	public void setVal(Object newVal)
	{
		value = newVal;
	}
	
	public String getType()
	{
		return type;
	}

	public void setVal(String newTyp)
	{
		type = newTyp;
	}
	
	public Object getDefault()
	{
		return defaultVal;
	}

	public void setDefault(Object newDflt)
	{
		defaultVal = newDflt;
	}
	
	public int getMaxStringLength()
	{
		return maxStrLgth; 
	}
	
	public int getWholeNum()
	{
		return maxWholeNum; 
	}
	public int getDecimal()
	{
		return maxDecimal; 
	}
	public boolean getNull()
	{
		return cantBeNull; 
	}
	public boolean getUnique()
	{
		return isUnique; 
	}
	public boolean checkIfUnique(Object toCheck)
	{
		boolean check = ifUnique.add(toCheck);
		return check;
	}
	public void setUnique(Boolean tOrF)
	{
		isUnique = tOrF;
	}
	
	public void addToBTree(String key, Row rowAdd)
	{
		theBTree.put(key, rowAdd);
	}
	public boolean isIndexed()
	{
		return isIndexed;
	}
	public void setIsIndexed(boolean tOrF)
	{
		isIndexed = tOrF;
	}
	
	public void setPrimaryKeyTo(boolean tOrF)
	{
		isPrimaryKey = tOrF;
	}
	public void setNull(boolean tOrF)
	{
		cantBeNull = tOrF;
	}
	public BTree getBTree()
	{
		return theBTree;
	}
	
	//potential issue of making nested Value so won't compare- make sure need to make a value object now just put row in
	public void deleteInBtree(Row theRow, String theKeyVal)
	{
		theBTree.deleteVal(theKeyVal, theRow);
	}
	public void addToBtree(Row theRow, String theKeyVal)
	{
		theBTree.put(theKeyVal, theRow);
	}
	
}

