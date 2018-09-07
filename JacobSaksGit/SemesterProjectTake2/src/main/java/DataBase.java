import java.util.ArrayList;
import java.io.IOException;
import java.lang.IllegalAccessException;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.create.index.CreateIndex;

public class DataBase {
	
	private ArrayList<Table> allTables;
	private static SQLParser parser;

	
	
	public DataBase()
	{
		allTables = new ArrayList<Table>();
		parser = new SQLParser();
	}

	
	public ResultSet execute(String sqlString) throws Exception
	{
		try {
		if(sqlString.contains("CREATE TABLE"))//query is a createTable query 
		{
				CreateTableQuery table1 = (CreateTableQuery)parser.parse(sqlString);
				ColumnDescription[] col = table1.getColumnDescriptions();
				try //try to create the table
				{
					Table tbl =new Table(table1);
					allTables.add(tbl);
					ResultRow clmNames = new ResultRow();
					int i;
					//add all the clmNames to the ResultRow to return in the result set 
					for(i = 0; i< tbl.getFirstRow().length() ; i++)
					{
						clmNames.addObject(tbl.getFirstRow().getElementAtIndex(i).getVal());
					}
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(clmNames);
					this.printOutResultSet(returnThis);
					//ResultSet for a create table query will print out the column names in the table 
					return returnThis;
					
				}catch(IllegalArgumentException e) //if something goes wrong print false
				{
					System.out.println("You Entered an Invalid entry type for the given table");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis1 = new ResultSet();
					returnThis1.addRow(falseRow);
					this.printOutResultSet(returnThis1);
					return returnThis1;
				}

		}else if(sqlString.contains("INSERT")) //an insert query 
		{
			
				InsertQuery insert1 = (InsertQuery)parser.parse(sqlString);
				Table toInsert = this.getTableWithName(insert1.getTableName());
				if (toInsert == null) //if no table found with the name - return false 
				{
					System.out.println("The Table you requested does not exist.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				try
				{
					toInsert.insertRow(insert1); //insert the row 
					ResultRow trueRow = new ResultRow();
					trueRow.addObject("true");
					ResultSet returnThis1 = new ResultSet();
					returnThis1.addRow(trueRow);
					this.printOutResultSet(returnThis1);
					return returnThis1; // return true 
					
				}
				catch(IllegalArgumentException e) //error thrown then return false 
				{
					System.out.println("You entered an invalid entry type for the given table. So the Insert was not completed.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				
		}else if(sqlString.contains("CREATE INDEX")) //index query to index a column(make a Btree)
		{
				CreateIndexQuery index1 = (CreateIndexQuery)parser.parse(sqlString);
				Table toIndex = this.getTableWithName(index1.getTableName());
				if (toIndex == null) //if no table with that name return false 
				{
					System.out.println("The Table you requested does not exist.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				try
				{
					toIndex.indexColumn(index1);
					ResultRow trueRow = new ResultRow();
					trueRow.addObject("true");
					ResultSet returnThis2 = new ResultSet();
					returnThis2.addRow(trueRow);
					this.printOutResultSet(returnThis2);
					return returnThis2; //if valid query return true 
				}
				catch(IllegalArgumentException e) //error thrown return false 
				{
					System.out.println("You index query failed.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis1 = new ResultSet();
					returnThis1.addRow(falseRow);
					this.printOutResultSet(returnThis1);
					return returnThis1;
				}
		}else if(sqlString.contains("SELECT"))//select query was entered 
		{
			try
			{	
				SelectQuery sq = (SelectQuery) parser.parse(sqlString);
				String [] tableNames = sq.getFromTableNames();
				Table toSelectFrom = this.getTableWithName(tableNames[0]);
				if (toSelectFrom == null) //if no table with that name return false 
				{
					System.out.println("The Table you requested does not exist.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				ResultSet toRet = toSelectFrom.select(sq);
				this.printOutResultSet(toRet);
				return toRet; //return a result set containing all the data that was selected - with column names and types in first two rows 
			} catch(IllegalArgumentException e) //error thrown return false 
			{
				System.out.println("You entered an invalid entry type for the given table. So the Insert was not completed.");
				ResultRow falseRow = new ResultRow();
				falseRow.addObject("false");
				ResultSet returnThis = new ResultSet();
				returnThis.addRow(falseRow);
				this.printOutResultSet(returnThis);
				return returnThis;
			}
			
		}else if(sqlString.contains("UPDATE"))//update query 
		{
			
				UpdateQuery uq1 = (UpdateQuery) parser.parse(sqlString);
				Table toUpdate = this.getTableWithName(uq1.getTableName());
				if (toUpdate == null) //no table exists with given name- return false 
				{
					System.out.println("The Table you requested does not exist.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				try
				{
					ResultSet toRet = toUpdate.update(uq1);
					this.printOutResultSet(toRet);
					return toRet; //result set returns true 
				} catch(IllegalArgumentException e) //error thrown return false 
				{
					System.out.println("You entered an invalid entry type for the given table. So the Insert was not completed.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				
			
		}else if(sqlString.contains("DELETE"))//delete query 
		{
			
				DeleteQuery dq1 = (DeleteQuery) parser.parse(sqlString);
				Table toDeleteFrom = this.getTableWithName(dq1.getTableName());
				if (toDeleteFrom == null)//if table does not exist return false 
				{
					System.out.println("The Table you requested does not exist.");
					ResultRow falseRow = new ResultRow();
					falseRow.addObject("false");
					ResultSet returnThis = new ResultSet();
					returnThis.addRow(falseRow);
					this.printOutResultSet(returnThis);
					return returnThis;
				}
				ResultSet dqRet = toDeleteFrom.delete(dq1);
				this.printOutResultSet(dqRet);
				return dqRet; //returns true if delete worked 
			
		}
		return null; //if the string contains none of these then its not a valid query so return a null
	}catch (JSQLParserException e)
		{ //error in the parser 
		System.out.println("An error occured parsing your query, check it was entered correctly");
		return null;
		}
	}
	
	
	
	
	//get the table with the given name from the arraylist of tables 
	private Table getTableWithName(String name)
	{
		for(Table c: allTables)
		{
			if(c.getTableName().equals(name))
			{
				return c;
			}
		}
		return null;
	}
	

	
	//print out the contents of a result set 
	private void printOutResultSet(ResultSet toPrint)
	{
		int i;
		int j;
		//For Each Row in the ResultSet
		for(i = 0 ; i < toPrint.numbOfRows(); i++)
		{
			//For Each Cell in the row
			for(j = 0; j< toPrint.getRowAtIndex(i).numbOfCells(); j++)
			{	
				if(toPrint.getRowAtIndex(i).getObjectAtIndex(j) != null)
				{
					System.out.print(toPrint.getRowAtIndex(i).getObjectAtIndex(j) + "  ");
				}
			}
			System.out.println(" ");
			System.out.println(" ");
		}
	}
	
	//only using this method for purposes of printing test results from the table- otherwise this method would not exist
	public ArrayList<Table> getTables()
	{
		return allTables;
	}
	
	
	
}

