import java.util.ArrayList;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;

public class Row {
	
	private ArrayList<RowElement> rowData;
	private String primaryKey;
	private CreateTableQuery query; 
	private ColumnDescription[] colNames;
	
	//constructor to make first Row of a table 
	public Row(CreateTableQuery toMakeRow, String primKey)
	{
		rowData = new ArrayList<RowElement>();
		primaryKey = primKey;
		query = toMakeRow;
		//colNames = query.getColumnDescriptions();
	}
	
	public Row()
	{
		rowData = new ArrayList<RowElement>();
	}
	
	public Row(InsertQuery createRow)
	{
		rowData = new ArrayList<RowElement>();
		
	}
	
	//creates the first row of a table which holds all the important info and stores the BTrees
	public Row createDefaultRow(ColumnDescription[] toPutIn)
	{
		String[] sqlTypes = {"VARCHAR","INT","BOOLEAN","DECIMAL"};
		String[] javaEquiv = {"String", "Integer", "Boolean", "Double"};
		//array of the default values 
		Integer defaultInt = null;
		Double defaultDbl = null;
		String defaultString = null;
		Boolean defualtBool = null; 
		Object[] defaultVal = { defaultString, defaultInt , defualtBool, defaultDbl};
		//add the primaryKey type to the default Row
		//ColumnDescription.DataType primaryK = query.getPrimaryKeyColumn().getColumnType();
		Row resultRow = new Row();
		boolean found;
		//get the types for the rest of the columns and add the default values to the default row 
		for(ColumnDescription c: toPutIn)
		{	
			ColumnDescription.DataType type = c.getColumnType();
			int j;
			found = false;
			//finds what sqlType the Column has, save value of j to get complement data from other two arrays
			for(j = 0; j<sqlTypes.length && !found ;j++)
			{
				if(type.toString().equals(sqlTypes[j]))
				{
					found = true;
				}
			}
			j--;
			String columnType = javaEquiv[j];
			String clmName = c.getColumnName();
			RowElement toAdd;
			Object defaultValue;
			//boolean if the column can be null or not 
			boolean nullVal = c.isNotNull();
			boolean isUniq = c.isUnique();
			//String defaultValue;
			if(c.getHasDefault())
			{
				defaultValue = c.getDefaultValue();
				//toAdd = new RowElement(clmName, columnType, c.getDefaultValue());
			}else
			{
				defaultValue = defaultVal[j];
				//toAdd = new RowElement(clmName, columnType, defaultVal[j]);
			}
			
			//if column is type string or decimal add its max length/s 
			if(type.toString().equals("VARCHAR"))
			{
				//boolean nullVal = c.isNotNull();
				toAdd = new RowElement(clmName, columnType, defaultValue, c.getVarCharLength(), nullVal, isUniq);
			}else if(type.toString().equals("DECIMAL"))
			{
				//boolean nullVal = c.isNotNull();
				toAdd = new RowElement(clmName, columnType, defaultValue, c.getWholeNumberLength(), c.getFractionLength(), nullVal, isUniq);
			}else
			{
				toAdd = new RowElement(clmName, columnType, defaultValue, nullVal, isUniq);
			}
			
			resultRow.addEntry(toAdd);
		}
		for(RowElement r: resultRow.getRowData())
		{
			if(r.getVal().toString().equals(query.getPrimaryKeyColumn().getColumnName()))
			{
				r.setPrimaryKeyTo(true);
				r.setIsIndexed(true);
				r.setNull(true);
				r.setUnique(true);
			}
		}
		return resultRow;
	}
	
	public void addEntry(RowElement toAdd)
	{
		rowData.add(toAdd);
	}
	
	public RowElement getElementAtIndex(int index)
	{
		return rowData.get(index);
	}
	
	public void replaceElementAtIndex(int index, RowElement replacementVal)
	{
		rowData.remove(index);
		rowData.add(index,replacementVal);
	}
	
	public int length()
	{
		return rowData.size();
	}
	
	public ArrayList<RowElement> getRowData()
	{
		return rowData;
	}
	
}
