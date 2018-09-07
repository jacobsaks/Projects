import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery.OrderBy;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;


public class Table {
	
	private ArrayList<Row> table;
	private String tableName;
	private String primaryKeyName;
	private Row firstRow;
	private int maxSizeOfList;
	private CreateTableQuery query;
	private ColumnDescription[] colNames;
	//private ArrayList<BTree> bTrees;
	private ArrayList<HashSet<Object>> checkForUnique;
	private ResultSet results;
	private ArrayList<Row> resultHolder;
	
	public Table(CreateTableQuery tableInfo)
	{
		table = new ArrayList<Row>();
		query = tableInfo;
		colNames = query.getColumnDescriptions();
		firstRow = createFirstRow();
		tableName = tableInfo.getTableName();
		primaryKeyName = tableInfo.getPrimaryKeyColumn().getColumnName();
		indexPrimaryColumn(primaryKeyName);
		//bTrees = new ArrayList<BTree>();
		checkForUnique = new ArrayList<HashSet<Object>>();
		resultHolder = new ArrayList<Row>();
		//table.add(firstRow);
		//index the primary key
	}
	
	
	//creates first Row which holds column names, column types default values, BTrees and other info about each column 
	public Row createFirstRow()
	{
		Row first = new Row(query, query.getPrimaryKeyColumn().getColumnName());
		Row returnRow = first.createDefaultRow(colNames);
		return returnRow;
		
	}
	
	//deletes Rows based on the delete query 
	public ResultSet delete(DeleteQuery dq)
	{
		ResultSet didAnythingUpdate = new ResultSet();
		ResultRow rr = new ResultRow();
		ArrayList<Row> rowsSatisfyWhere = new ArrayList<Row>(); 
		if(dq.getQueryString().contains("WHERE"))
		{
			Condition where = dq.getWhereCondition();
			//the rows in the table that satisfy the where condition
			rowsSatisfyWhere = this.getArrayOfRowsWhere(where);
			if(rowsSatisfyWhere.isEmpty())
			{
				System.out.println("None of the Rows fit your where condition");
				rr.addObject(false);
				didAnythingUpdate.addRow(rr);
				return didAnythingUpdate;
			}
		}else
		{
			rowsSatisfyWhere = table;
		}
		table.removeAll(rowsSatisfyWhere);
		//for(Row r: rowsSatisfyWhere)
		{
			//int indexToRemove = findRowIndex(r, table);
			//if(indexToRemove == -1){
			//}else
			{
			//	table.remove(indexToRemove);
			}
		}
		deleteInBTrees(rowsSatisfyWhere);
		rr.addObject(true);
		didAnythingUpdate.addRow(rr);
		return didAnythingUpdate;
	}
	
	//delete all of the Rows that were deleted in the chart from all of the Indexed Column's BTrees
	private void deleteInBTrees(ArrayList<Row> toDelete)
	{
		int i;
		for(i = 0; i< firstRow.length(); i++)
		{
			if(firstRow.getElementAtIndex(i).isIndexed())
			{
				for(Row r: toDelete)
				{
					firstRow.getElementAtIndex(i).deleteInBtree(r, r.getElementAtIndex(i).getVal().toString());
				}
			}
		}
	}

	
	//Update the values of Rows that meet the condition of the Update Query 
	public ResultSet update(UpdateQuery uq)  throws IllegalArgumentException
	{
		ResultSet didAnythingUpdate = new ResultSet();
		ResultRow rr = new ResultRow();
		ArrayList<Row> rowsSatisfyWhere = new ArrayList<Row>(); 
		if(uq.getQueryString().contains("WHERE"))
		{
			Condition where = uq.getWhereCondition();
			rowsSatisfyWhere = this.getArrayOfRowsWhere(where);
			if(rowsSatisfyWhere.isEmpty())
			{
				rr.addObject(false);
				didAnythingUpdate.addRow(rr);
				return didAnythingUpdate;
			}
		}else
		{
			rowsSatisfyWhere = table;
		}
		//create two ArrayLists- the first holding the index of the Row to Update(find the column in the chart with the matching name)
		//the second will hold the replacement value- given in the Update Query 
		ArrayList<Integer> indexesToUpdate = new ArrayList<Integer>();
		ArrayList<String> valuesToUpdate = new ArrayList<String>();
		ColumnValuePair[] clmPairs = uq.getColumnValuePairs();
		int i;
		int j;
		//for each ColumnValuePair- find the matching index of the Row- add it to the ArrayList, add the correct Value to the replacement Value ArrayList
		for(j = 0; j <clmPairs.length; j++)
		{
			String clmPairVal = clmPairs[j].getValue();
			String clmName = clmPairs[j].getColumnID().getColumnName();
			for(i = 0; i< firstRow.length(); i++)
			{
				if(firstRow.getElementAtIndex(i).getVal().equals(clmName))
				{
					indexesToUpdate.add(i);
					Boolean ifFalseThrowError = this.typesMatch(clmPairVal, firstRow.getElementAtIndex(i).getType(), i);
					if(firstRow.getElementAtIndex(i).getType().equals("String"))
					{
						if(clmPairVal.startsWith("'") && (clmPairVal.length() < firstRow.getElementAtIndex(i).getMaxStringLength()))
						{
							clmPairVal = clmPairVal.substring(1, clmPairVal.length()-1);
						}else
						{
							System.out.println("The String is either too long or is not surrounded by ' marks.");
							throw new IllegalArgumentException();
						}
					}
					valuesToUpdate.add(clmPairVal);
				}
			}
		}
		
		//table.removeAll(rowsSatisfyWhere);
		for(Row r: rowsSatisfyWhere)
			{
				int indexToRemove = findRowIndex(r, table);
				if(indexToRemove == -1){
				}else
				{
					table.remove(indexToRemove);
				}
			}
		//now delete all old Rows from Btree
		deleteInBTrees(rowsSatisfyWhere);
		
		
		//create a new ArrayList<Row>, update all the Rows with the updated Values, add them to the ArrayList 
		ArrayList<Row> updatedRows = new ArrayList<Row>();
		ArrayList<Row> copyRowsWhere = rowsSatisfyWhere;
		for(Row r: rowsSatisfyWhere)
		{
			//create updated row 
			//Row updatedRow = new Row();
			int k;
			for(k = 0; k < indexesToUpdate.size(); k++)
			{
				RowElement replaceVal = new RowElement(valuesToUpdate.get(k));
				r.replaceElementAtIndex(indexesToUpdate.get(k), replaceVal);
			}
			updatedRows.add(r);
		}
		
		//table.addAll(updatedRows);
		for(Row rows: updatedRows)
		{
			table.add(rows);
		}

		//and add all new rows to the Btree
		int k;
		for(k = 0; k< firstRow.length(); k++)
		{
			if(firstRow.getElementAtIndex(k).isIndexed())
			{
				for(Row r: updatedRows)
				{
					firstRow.getElementAtIndex(k).addToBtree(r, r.getElementAtIndex(k).getVal().toString());
				}
			}
		}
		rr.addObject(true);
		didAnythingUpdate.addRow(rr);
		return didAnythingUpdate;
	}
	
	//Select the Rows that satisfy the condition and return the relevant columns in the result set 
	public ResultSet select(SelectQuery sq)
	{	
		ArrayList<Row> rowsSatisfyWhere = new ArrayList<Row>(); 
		results = new ResultSet();
		ArrayList<Integer> indexOfColumns = new ArrayList<Integer>();
		ColumnID[] colIDs = sq.getSelectedColumnNames();
		//create an ArrayList with the index values of the desired columns
		if(colIDs[0].getColumnName().equals("*"))
		{
			int w;
			for(w =0; w<firstRow.length(); w++)
			{
				indexOfColumns.add(w);
			}
		}
		for(ColumnID cid: colIDs)
		{
			String name = cid.getColumnName();
			int i;
			for(i = 0; i< firstRow.length(); i++)
			{
				if(firstRow.getElementAtIndex(i).getVal().equals(name))
				{
					indexOfColumns.add(i);
					//allIndexed = allIndexed && firstRow.getElementAtIndex(i).isIndexed();
				}
			}
		}
		//find all the rows that satisfy the where condition if there is one
		if(sq.getQueryString().contains("WHERE"))
		{
			Condition where = sq.getWhereCondition();
			rowsSatisfyWhere = this.getArrayOfRowsWhere(where);
		}else  //if no where condition- get every row 
		{
			rowsSatisfyWhere = table;
		}
		
		//order the Rows here by the Order by- given ArrayList<Row> return ArrayList<Row> in order specified 
		if(sq.getOrderBys().length !=0)
		{
			rowsSatisfyWhere = putInOrder(rowsSatisfyWhere, sq);
		}
		
		
		//for each row in result ArrayList of rows- add the desired column values as a row to ResultSet
		for(Row q: rowsSatisfyWhere)
		{
				ResultRow rr = new ResultRow();
				//using the indexes of the select rows saved in the arraylist, get all the values in those indexes of the Row in the table
				//and add them to a result row to be returned in the ResultSet
				for(int j = 0; j< indexOfColumns.size(); j++)
				{
					rr.addObject(q.getElementAtIndex(indexOfColumns.get(j)).getVal());
				}
					
				results.addRow(rr);
				//System.out.print("you did it- and it printed");
			
		}
		//if there is a function related to this select query- return the resultSet that will be produced
		if(sq.getFunctions().size() != 0)
		{
			return this.selectFunction(sq, results);
		}else  //create a resultSet of the rows of values and return it 
		{
			ResultRow clmNamesRow = new ResultRow();
			ResultRow dataTypesRow = new ResultRow();
			for(int j = 0; j< indexOfColumns.size(); j++)
			{
				clmNamesRow.addObject(firstRow.getElementAtIndex(indexOfColumns.get(j)).getVal().toString());
			}
			for(int j = 0; j< indexOfColumns.size(); j++)
			{
				dataTypesRow.addObject(firstRow.getElementAtIndex(indexOfColumns.get(j)).getType());
			}
			results.addRowAtIndex(clmNamesRow, 0);
			results.addRowAtIndex(dataTypesRow, 1);
			if(sq.isDistinct())
			{
				ArrayList<Integer> toDel = getRidOfDoubles(results);
				for (int p = toDel.size() - 1; p >= 0; p--)
				{
					results.deleteRow(toDel.get(p));
				}
			}
			return results;
		}	
	}		
	
	//take an ArrayList of Rows and order them as specified 
	private ArrayList<Row> putInOrder(ArrayList<Row> input, SelectQuery sq)
	{
		ArrayList<Row> ordered = new ArrayList<Row>();
		OrderBy[] orderBys = sq.getOrderBys();
		for(Row r: input)
		{
			int i;
			boolean rowNotAdded = true;
			if(ordered.size() == 0)
			{
				//ordered.add(r);
			}
			//find where to add the row and add it 
			for(i = 0; i < ordered.size() && rowNotAdded; i++)
			{
				if(!getIfGoesAfter(r, ordered.get(i), orderBys, 0))
				{
					ordered.add(i, r);
					rowNotAdded = false;
				}
			}
			if(rowNotAdded) //Row should be added at the End of the arraylist
			{
				ordered.add(r);
			}
		}
		return ordered;
	}
	
	//figure out if the Row goes after another Row 
	private Boolean getIfGoesAfter(Row toAdd, Row compare, OrderBy[] ordBy, int indexOfOrderBy)
	{
		String clmName = ordBy[indexOfOrderBy].getColumnID().getColumnName().toString();
		int columnIndex = 0;
		int cpm;
		Boolean returnVal = true;
		int j;
		//find the index of the column
		for(j = 0; j< firstRow.length(); j++)
		{
			if(firstRow.getElementAtIndex(j).getVal().equals(clmName))
			{
				columnIndex = j;
			}
		}
		//compare the rowElement to put in with the one in the index- set returnVal if it should 
		
		//if the value is a double or integer- turn the string representation into a value to compare 
		if(firstRow.getElementAtIndex(columnIndex).getType().equals("Double") || firstRow.getElementAtIndex(columnIndex).getType().equals("Integer"))
		{
			if(toAdd.getElementAtIndex(columnIndex).getVal() != null)
			{
				//cpm = toAdd.getElementAtIndex(columnIndex).getVal().toString().compareTo(compare.getElementAtIndex(columnIndex).getVal().toString());
				Double toAddD =Double.valueOf(toAdd.getElementAtIndex(columnIndex).getVal().toString());
				Double compareD =Double.valueOf(compare.getElementAtIndex(columnIndex).getVal().toString());
				cpm = toAddD.compareTo(compareD);
			}else
			{
				cpm = 1;
			}
		}else // if its a string or boolean compare them as the strings they already are 
		{
			if(toAdd.getElementAtIndex(columnIndex).getVal() != null)
			{
				cpm = toAdd.getElementAtIndex(columnIndex).getVal().toString().compareTo(compare.getElementAtIndex(columnIndex).getVal().toString());
		
			}else
			{
				cpm = 1;
			}
		}
		if(cpm > 0)
		{
			returnVal = true;
		}else if(cpm < 0)
		{
			returnVal = false;
		}else if(cpm == 0)
		{
			//if no more order bys just put after
			if(ordBy.length == indexOfOrderBy + 1)
			{
				returnVal =  true;
			}else
			{
				return getIfGoesAfter(toAdd, compare, ordBy, indexOfOrderBy +1 );
				
			}
		}
		//returnVal is assuming Ascending- if its descending return the inverse of returnVal
		if(ordBy[indexOfOrderBy].isDescending())
		{
			returnVal = !returnVal;
		}
		
		return returnVal;
		
	}
	
	//if the select has a function then this method executes the function on the relevant rows(from the where statement)- these rows are saved in the ResultSet parameter
	private ResultSet selectFunction(SelectQuery sq, ResultSet rs)
	{
		ResultSet theResults = new ResultSet();
		ResultSet allTheResults = new ResultSet();
		int w;
		//this loop executes the return for each function 
		for(w = 0; w<sq.getFunctions().size(); w++)
		{
			String functionName = sq.getFunctions().get(w).function.toString();
			ColumnID[] colIDs = sq.getSelectedColumnNames();
			String columnName = colIDs[w].getColumnName();
			int i;
			int columnIndex = 0;
			for(i = 0; i< firstRow.length(); i++)
			{
				if(firstRow.getElementAtIndex(i).getVal().equals(columnName))
				{
					columnIndex = i;
					//allIndexed = allIndexed && firstRow.getElementAtIndex(i).isIndexed();
				}
			}
			boolean isDistinct = sq.isDistinct();
			
			//pass the information to the specific function methods to get the result 
			if(functionName.equals("AVG"))
			{
				theResults = selectAvgOrSum(sq, columnIndex, w, isDistinct, rs);
			}
			if(functionName.equals("COUNT"))
			{
				theResults = selectCount(sq, columnIndex, w, isDistinct, rs);
			}
			if(functionName.equals("MAX"))
			{
				theResults = selectMaxOrMin(sq, columnIndex, w, isDistinct, rs);
			}
			if(functionName.equals("MIN"))
			{
				theResults = selectMaxOrMin(sq, columnIndex, w, isDistinct, rs);
			}
			if(functionName.equals("SUM"))
			{
				theResults = selectAvgOrSum(sq, columnIndex, w, isDistinct, rs);
			}
			allTheResults.addRow(theResults.getRowAtIndex(0));
			allTheResults.addRow(theResults.getRowAtIndex(1));
			allTheResults.addRow(theResults.getRowAtIndex(2));
		}
			
			return allTheResults;
	}
	
	//for functions Avg and Sum - returns the average or sum of the given column 
	private ResultSet selectAvgOrSum(SelectQuery sq, int clmIndex, int numberFunction, boolean isDistinct, ResultSet whereResult) throws IllegalArgumentException
	{
		//remove all duplicates  
		if(isDistinct || sq.getFunctions().get(numberFunction).isDistinct)
		{
			ArrayList<Integer> toDel = getRidOfDoubles(whereResult);
			for (int p = toDel.size() - 1; p >= 0; p--)
			{
				whereResult.deleteRow(toDel.get(p));
			}
			
		}
		ResultSet returnThis = new ResultSet();
		//if trying to get the sum/avg of a string or boolean column- throw exception
		if(firstRow.getElementAtIndex(clmIndex).getType().equals("String") || firstRow.getElementAtIndex(clmIndex).getType().equals("Boolean"))
		{
			System.out.println("The type of the column is not applicable to the given function");
			throw new IllegalArgumentException();
		//if column is an integer- return the avg or sum	
		}else if(firstRow.getElementAtIndex(clmIndex).getType().equals("Integer"))
		{
				Double total = 0.0;
				for(ResultRow r: whereResult.getArrayList())
				{
					if(r.getRow().get(numberFunction) != null)
					{
						total = total + Double.valueOf(r.getRow().get(numberFunction).toString());
					}
				}
				if(sq.getFunctions().get(numberFunction).function.toString().equals("AVG"))
				{
					total = total / table.size();
				}
				String returnStringOfValue = total.toString();
				if(sq.getFunctions().get(numberFunction).function.toString().equals("SUM"))
				{
					int decimalIndex = returnStringOfValue.indexOf(".");
					if(!(returnStringOfValue.contains("E")))
					{
						returnStringOfValue = returnStringOfValue.substring(0, decimalIndex);
					}
				}
				ResultRow totalFunction = new ResultRow();
				totalFunction.addObject(total);
				returnThis.addRow(totalFunction);
		//if column is a double- return the avg or sum
		}else if(firstRow.getElementAtIndex(clmIndex).getType().equals("Double"))
		{
			Double total2 = 0.0;
			for(ResultRow r: whereResult.getArrayList())
			{
				total2 = total2 + Double.valueOf(r.getRow().get(0).toString());
			}
			if(sq.getFunctions().get(numberFunction).function.toString().equals("AVG"))
			{
				total2 = total2 / table.size();
			}
			ResultRow totalFunction = new ResultRow();
			totalFunction.addObject(total2);
			returnThis.addRow(totalFunction);
			
		}
		//generate the result set 
		ResultRow clmNameRow = new ResultRow();
		ResultRow dataTypeRow = new ResultRow();
		clmNameRow.addObject(firstRow.getElementAtIndex(clmIndex).getVal().toString());
		dataTypeRow.addObject(firstRow.getElementAtIndex(clmIndex).getType());
		returnThis.addRowAtIndex(clmNameRow, 0);
		returnThis.addRowAtIndex(dataTypeRow, 1);
		return returnThis;
	}
	
	//creates and returns the result set for a count function in a select statement 
	private ResultSet selectCount(SelectQuery sq, int clmIndex, int numberFunction, boolean isDistinct, ResultSet whereResult)
	{
		if(isDistinct || sq.getFunctions().get(numberFunction).isDistinct)
		{
			//remove all duplicates  
			if(isDistinct || sq.getFunctions().get(numberFunction).isDistinct)
			{
				ArrayList<Integer> toDel = getRidOfDoubles(whereResult);
				for (int p = toDel.size() - 1; p >= 0; p--)
				{
					whereResult.deleteRow(toDel.get(p));
				}
				
			}
		}
		int count = whereResult.getArrayList().size();
		ResultRow countTtl = new ResultRow();
		countTtl.addObject(count);
		ResultSet returnThis = new ResultSet();
		returnThis.addRow(countTtl);
		ResultRow clmNameRow = new ResultRow();
		ResultRow dataTypeRow = new ResultRow();
		clmNameRow.addObject(firstRow.getElementAtIndex(clmIndex).getVal().toString());
		dataTypeRow.addObject(firstRow.getElementAtIndex(clmIndex).getType());
		returnThis.addRowAtIndex(clmNameRow, 0);
		returnThis.addRowAtIndex(dataTypeRow, 1);
		return returnThis;
		
	}
	
	//finds the max or the min for a column and returns the value 
	private ResultSet selectMaxOrMin(SelectQuery sq, int clmIndex, int numberFunction, boolean isDistinct, ResultSet whereResult)
	{
		if(isDistinct || sq.getFunctions().get(numberFunction).isDistinct)
		{
			//remove all duplicates  
			if(isDistinct || sq.getFunctions().get(numberFunction).isDistinct)
			{
				ArrayList<Integer> toDel = getRidOfDoubles(whereResult);
				for (int p = toDel.size() - 1; p >= 0; p--)
				{
					whereResult.deleteRow(toDel.get(p));
				}
				
			}
		}
		//order the results- return max or min- 
		int i;
		String val;
		ArrayList<Double> toCompare = new ArrayList<Double>();
		for(i = 0; i<whereResult.getArrayListOfRows().size(); i++)
		{
			if(whereResult.getArrayList().get(i).getRow().get(numberFunction) != null)
			{
				val = whereResult.getArrayList().get(i).getRow().get(numberFunction).toString();
				Double valEnter = Double.valueOf(val);
				toCompare.add(valEnter);
			}
		}
		if(toCompare.isEmpty())
		{
			System.out.print("There were no non-null values for this specific query so there is no max or min");
			throw new IllegalArgumentException();
		}
		Object minOrMax;
		String returnStringOfValue;
		if(sq.getFunctions().get(numberFunction).function.toString().equals("MAX"))
		{
			minOrMax = Collections.max(toCompare);
		}else if(sq.getFunctions().get(numberFunction).function.toString().equals("MIN"))
		{
			minOrMax = Collections.min(toCompare);
		}else
		{
			throw new IllegalArgumentException();
		}
		returnStringOfValue = minOrMax.toString();
		if(firstRow.getElementAtIndex(clmIndex).getType().equals("Integer"))
		{
			int decimalIndex = returnStringOfValue.indexOf(".");
			if(!(returnStringOfValue.contains("E")))
			{
				returnStringOfValue = returnStringOfValue.substring(0, decimalIndex);
			}
		}
		ResultRow minMaxResult = new ResultRow();
		minMaxResult.addObject(returnStringOfValue);
		ResultSet returnThis = new ResultSet();
		returnThis.addRow(minMaxResult);
		ResultRow clmNameRow = new ResultRow();
		ResultRow dataTypeRow = new ResultRow();
		clmNameRow.addObject(firstRow.getElementAtIndex(clmIndex).getVal().toString());
		dataTypeRow.addObject(firstRow.getElementAtIndex(clmIndex).getType());
		returnThis.addRowAtIndex(clmNameRow, 0);
		returnThis.addRowAtIndex(dataTypeRow, 1);
		return returnThis;
		
	}
	
	//this method takes a where condition and finds the relevant rows and returns them in an Arraylist- it will recursively call itself and generate multiple 
	//arraylists and combine them based on the AND or OR condition. 
	private ArrayList<Row> getArrayOfRowsWhere(Condition where)
	{
		//if the operator is an AND- recursively call getArrayWhere on both right and left and then combine the two separate results 
		if(where.getOperator().toString().contains("AND"))
		{
			ArrayList<Row> toRemove = new ArrayList<Row>();
			ArrayList<Row> rightSide = getArrayOfRowsWhere((Condition)where.getRightOperand());
			ArrayList<Row> leftSide = getArrayOfRowsWhere((Condition)where.getLeftOperand());
			for(Row r: rightSide)
			{
				//if the left side contains r
				if(containedInTheArrayList(r, leftSide))
				{
					int dd = 5;
				}else
				{
					toRemove.add(r);
				}
			}
			//rightSide.removeAll(toRemove);
			for(Row r: toRemove)
			{
				int indexToRemove = findRowIndex(r, rightSide);
				if(indexToRemove ==-1)
				{	
				}else{
					rightSide.remove(indexToRemove);
				}
			}
			
			return rightSide;
		}//if the operator is an OR- recursively call getArrayWhere on both right and left and then combine the two separate results 
		else if(where.getOperator().toString().contains("OR"))
		{
			ArrayList<Row> rightSide = getArrayOfRowsWhere((Condition)where.getRightOperand());
			ArrayList<Row> leftSide = getArrayOfRowsWhere((Condition)where.getLeftOperand());
			if(leftSide.size() == 0)
			{
				return rightSide;
			}
			for(Row r: leftSide)
			{
				//if(rightSide.contains(r))
				if(containedInTheArrayList(r, rightSide))
				{
					
				}else
				{
					rightSide.add(r);
				}
			}
			return rightSide;
		}//Base Case- if the operator is equal find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().equals("EQUALS"))
		{
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Value> hold = new ArrayList<Value>();
			//ArrayList<Row> temp = new ArrayList<Row>();
			//int q = whereStatement.indexOf("EQUALS");
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					//if indexed get the info from the BTREE
					if(firstRow.getElementAtIndex(k).isIndexed())
					{
						//get the Result Arraylist of rows 
						hold = firstRow.getElementAtIndex(k).getBTree().getValue(right);
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
						//temp = firstRow.getElementAtIndex(k).getBTree().getValue(right);
						
					}else //no BTree so get information from the table itself 
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the value matches
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									if(leftDbl.compareTo(rightDbl) == 0)
										//if((Double.valueOf(r.getElementAtIndex(k).getVal().toString())).equals(Double.valueOf(right)));
									{
										temp.add(r);
									}
								}
							}else
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									if(r.getElementAtIndex(k).getVal().toString().equals(right))
									{
										temp.add(r); //if yes add the Row to the ArrayList
									}
								}
							}
							
						}
					}
				}
			}
			return temp;
		}//Base Case- if the operator is not-equal find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().contains("NOT_EQUALS"))
		{
			//cmprReturn(int indexOfStr, int lngtOfString)
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Row> temp2 = new ArrayList<Row>();
			ArrayList<Value> hold2 = new ArrayList<Value>();
			ArrayList<Value> hold = new ArrayList<Value>();
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					if(firstRow.getElementAtIndex(k).isIndexed())
					{
						//get the Result Arraylist of rows 
						//start with every single value, then remove the matching values 
						hold = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove("0");
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
						//remove all equal to
						hold2 = firstRow.getElementAtIndex(k).getBTree().getValue(right);
						for(Value v: hold2)
						{
							if(v != null)
							{
								temp2.add((Row) v.getVal());
							}
						}
						
						//temp = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove("0");
						//temp.removeAll(temp2);
						for(Row r: temp2)
						{
							int indexToRemove = findRowIndex(r, temp);
							if(indexToRemove == -1){
							}else
							{
								temp.remove(indexToRemove);
							}
						}
						
					}else
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the value matches
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									if(!(leftDbl.compareTo(rightDbl) == 0))
										//if(!(Double.valueOf(r.getElementAtIndex(k).getVal().toString())).equals(Double.valueOf(right)));
									{
										temp.add(r);
									}
								}
							}else
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									if(r.getElementAtIndex(k).getVal().toString().equals(right))
									{
									
									}else 
									{
										temp.add(r); //if not equal add the Row to the ArrayList
									}
								}
							
							}
						}
					}
				}
			}
			return temp;
		}//Base Case- if the operator is greaterThan find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().equals("GREATER_THAN"))
		{
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Row> temp2 = new ArrayList<Row>();
			ArrayList<Value> hold = new ArrayList<Value>();
			ArrayList<Value> hold2 = new ArrayList<Value>();
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					if(firstRow.getElementAtIndex(k).isIndexed())
					{	
						//get the Result Arraylist of rows
						hold = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove(right);
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
						hold2 = firstRow.getElementAtIndex(k).getBTree().getValue(right);
						if(hold2 != null)
						{
							for(Value v: hold2)
							{
								if(v != null)
								{
									temp2.add((Row) v.getVal());
								}
							}
						}
						temp.removeAll(temp2);
						//for(Object v: firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove(right))
						//{
							//temp.add((Row) v);
						//} 
						//temp = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove(right);
					}else
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the row value is greater
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									if(leftDbl.compareTo(rightDbl) > 0)
										//if((Double.valueOf(r.getElementAtIndex(k).getVal().toString())).compareTo(Double.valueOf(right)) > 0);
									{
										temp.add(r);
									}
								}
							}else
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									if((r.getElementAtIndex(k).getVal().toString().compareTo(right) > 0) && (r.getElementAtIndex(k).getVal() != null))
									{
										temp.add(r); //if yes add the Row to the ArrayList
									}
								}
							}
						}
					}
				}
			}
			return temp;
		}//Base Case- if the operator is lessThan find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().equals("LESS_THAN"))
		{
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Row> temp2 = new ArrayList<Row>();
			ArrayList<Value> hold = new ArrayList<Value>();
			ArrayList<Value> hold2 = new ArrayList<Value>();
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					if(firstRow.getElementAtIndex(k).isIndexed())
					{
						//get the Result Arraylist of rows 
						hold = firstRow.getElementAtIndex(k).getBTree().getAllValuesBelow(right);
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
						hold2 = firstRow.getElementAtIndex(k).getBTree().getValue(right);
						for(Value v: hold2)
						{
							if(v != null)
							{
								temp2.add((Row) v.getVal());
							}
						}
						temp.removeAll(temp2);
						//temp = firstRow.getElementAtIndex(k).getBTree().getAllValuesBelow(right);
					}else
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the row value is greater
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									if(leftDbl.compareTo(rightDbl) < 0)
										//if((Double.valueOf(r.getElementAtIndex(k).getVal().toString())).compareTo(Double.valueOf(right)) < 0);
									{
										temp.add(r);
									}
								}
							}else if(r.getElementAtIndex(k).getVal() != null)
							{
								if(r.getElementAtIndex(k).getVal().toString().compareTo(right) < 0)
								{
									temp.add(r); //if yes add the Row to the ArrayList
								}
							}
							
						}
					}
				}
			}
			return temp;
		}//Base Case- if the operator is GreaterThanOrEquals find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().contains("GREATER_THAN_OR_EQUALS"))
		{
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Value> hold2 = new ArrayList<Value>();
			ArrayList<Value> hold = new ArrayList<Value>();
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					if(firstRow.getElementAtIndex(k).isIndexed())
					{
						//get the Result Arraylist of rows 
						//add all greater than
						hold = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove(right);
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
						
						//temp = firstRow.getElementAtIndex(k).getBTree().getAllValuesAbove(right);
						//temp.addAll(firstRow.getElementAtIndex(k).getBTree().getValue(right));
					}else
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the row value is greater or equal
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									//if((Double.valueOf(r.getElementAtIndex(k).getVal().toString())).compareTo(Double.valueOf(right)) >= 0);
									if(leftDbl.compareTo(rightDbl) >= 0)
									{
										temp.add(r);
									}
								}
							}else if(r.getElementAtIndex(k).getVal() != null)
							{
								if(r.getElementAtIndex(k).getVal().toString().compareTo(right) >= 0)
								{
									temp.add(r); //if yes add the Row to the ArrayList
								}
							}
							
						}
					}
				}
			}
			return temp;
		}//Base Case- if the operator is lessThanOrEquals find all the rows that satisfy the condition
		else if(where.getOperator().name().toString().contains("LESS_THAN_OR_EQUALS"))
		{
			ArrayList<Row> temp = new ArrayList<Row>();
			ArrayList<Value> hold2 = new ArrayList<Value>();
			ArrayList<Value> hold = new ArrayList<Value>();
			String left = where.getLeftOperand().toString();
			String right = where.getRightOperand().toString();
			right = right.replaceAll("'", "");
			int k;
			for(k = 0; k < firstRow.length(); k++)
			{
				//find the right column to look at 
				if(firstRow.getElementAtIndex(k).getVal().toString().equals(left))
				{
					if(firstRow.getElementAtIndex(k).isIndexed())
					{
						//get the Result Arraylist of rows 
						//add all less than
						hold = firstRow.getElementAtIndex(k).getBTree().getAllValuesBelow(right);
						for(Value v: hold)
						{
							if(v != null)
							{
								temp.add((Row) v.getVal());
							}
						}
					}else
					{	//for each row check if value satisfies condition, if so add the Row to the resultHolder ArrayList
						for(Row r: table)
						{
						//see if the row value is lower or equal
							if(firstRow.getElementAtIndex(k).getType().equals("Integer") || firstRow.getElementAtIndex(k).getType().equals("Double"))
							{
								if(r.getElementAtIndex(k).getVal() != null)
								{
									Double leftDbl = Double.valueOf(r.getElementAtIndex(k).getVal().toString());
									Double rightDbl = Double.valueOf(right);
									if(leftDbl.compareTo(rightDbl) <= 0)
										//if((Double.valueOf(r.getElementAtIndex(k).getVal().toString())).compareTo(Double.valueOf(right)) <= 0);
									{
										temp.add(r);
									}
								}
							}else if(r.getElementAtIndex(k).getVal() != null)
							{
								if(r.getElementAtIndex(k).getVal().toString().compareTo(right) <= 0)
								{
									temp.add(r); //if yes add the Row to the ArrayList
								}
							}
							
						}
					}
				}
			}
			return temp;
		}
		
		return null;
	}
	
	
	//method to index a column 
	public void indexColumn(CreateIndexQuery ciq)
	{
		String clmName = ciq.getColumnName();
		String tblName = ciq.getTableName();
		Row firstInTable = firstRow;
		//find the correct column in the index 
		int j;
		for(j = 0; j < firstInTable.length();j++)
		{
			if(clmName.equals(firstInTable.getElementAtIndex(j).getVal()))
			{
				//For each Row in the table get the column for the key and place the entire row as the return value
				for(int k = 0; k < this.getTableRows().size(); k++)
				{
					String key4Tree = ((String)this.getTableRows().get(k).getElementAtIndex(j).getVal());
					Row val4Tree= ((Row)this.getTableRows().get(k));
					firstInTable.getElementAtIndex(j).addToBTree(key4Tree, val4Tree);
					//bT.put(key4Tree, val4Tree);
					
				}
				firstRow.getElementAtIndex(j).setIsIndexed(true);
			}
			
		}
	}

	//create an index for the primary column 
	public void indexPrimaryColumn(String columnName)
	{
		String clmName = columnName;
		String tblName = tableName;
		BTree bT = new BTree(columnName, "0");
		Row firstInTable = firstRow;
		//find the correct column in the index 
		int j;
		for(j = 0; j < firstInTable.length();j++)
		{
			if(clmName.equals(firstInTable.getElementAtIndex(j).getVal()))
			{
				//For each Row in the table get the column for the key and place the entire row as the return value
				for(int k = 0; k < this.getTableRows().size(); k++)
				{
					String key4Tree = ((String)this.getTableRows().get(k).getElementAtIndex(j).getVal());
					Row val4Tree= ((Row)this.getTableRows().get(k));
					firstInTable.getElementAtIndex(j).addToBTree(key4Tree, val4Tree);
					//bT.put(key4Tree, val4Tree);
					
				}
				firstRow.getElementAtIndex(j).setIsIndexed(true);
			}
			
		}
	}
	
	
	
	//Creates a new Row and inserts the row after checking type security and other requirements 
	public void insertRow(InsertQuery toAdd) throws IllegalArgumentException
	{
		ColumnValuePair[] pairs = toAdd.getColumnValuePairs();
		checkColumnsAllInChart(pairs);
		Row toInsert = new Row();
		int i;
		int j;
		boolean valueWasAdded;
		
		// this is n^2 to fix need to use fact that the order is made by the hashset of the column names 
		//for each column in the chart
		for(j=0; j< firstRow.length(); j++)
		{
			valueWasAdded = false;
			for(i = 0; i < pairs.length; i++)
			{
				//check that the columnValuePair column name == same column name as the first row AND the types match(within typeMatch checks max lengths are in boundary)
				//System.out.println(pairs[i].getClass().toString());
				if(pairs[i].getColumnID().getColumnName().equals(firstRow.getElementAtIndex(j).getVal().toString()) && typesMatch(pairs[i].getValue(), firstRow.getElementAtIndex(j).getType(), j))
				{
					if (firstRow.getElementAtIndex(j).getUnique() == true)
					{
						//check if the value is added to the hashset of the row- showing it is unique - 
						boolean test = firstRow.getElementAtIndex(j).checkIfUnique(pairs[i].getValue());
						if(!test)
						{
							System.out.println("You entered a non-Unique Value for a Unique Column");
							throw new IllegalArgumentException();
						}
					}
					//issue is all values are put into table as a string- try to convert to correct type using ValueOF method on wrapper type
					//pairs[i].getValue();
					String toEnter = pairs[i].getValue();
					if(firstRow.getElementAtIndex(j).getType().equalsIgnoreCase("String"))
					{
							toEnter = toEnter.substring(1, toEnter.length() - 1);
					}
					RowElement addThis = new RowElement(toEnter);
					
					//add entry into Row
					toInsert.addEntry(addThis);
					valueWasAdded = true;
				}
			}
			//if no value was entered- should put a null in the spot but if its a non-Null column throw an error 
			if(!valueWasAdded && firstRow.getElementAtIndex(j).getNull() == true)
			{
				System.out.println("You did not enter a value for a required field");
				throw new IllegalArgumentException();
			}else if(!valueWasAdded) //add a null or the default 
			{
				RowElement addMe = new RowElement(firstRow.getElementAtIndex(j).getDefault());
				toInsert.addEntry(addMe);
			}
		}
		table.add(toInsert);
		int y;
		//add the new row to all relevant BTrees
		for (y = 0; y< firstRow.length(); y++)
		{
			if(firstRow.getElementAtIndex(y).isIndexed())
			{
				if((toInsert.getElementAtIndex(y).getVal() != null))
				{
					firstRow.getElementAtIndex(y).addToBTree((String)(toInsert.getElementAtIndex(y).getVal().toString()), toInsert);
				}
			}
		}
	}
	
	//checks whether the typesMatch for the input information, whether strings and decimals conform to size restrictions, ints and doubles are numeric...
	private boolean typesMatch(String valueToEnter, String type, int indexOfFirstRow) throws IllegalArgumentException
	{
		if(type.equals("Integer"))
		{
			if(isNumeric(valueToEnter))
			{
				return true;
			}else 
			{
				System.out.println("You entered an invalid entry for an Integer Field");
				throw new IllegalArgumentException();
			}
		}
		if(type.equals("Double"))
		{
			int comma = valueToEnter.indexOf(".");
			//test that the value is a number, its string length before the decimal is less than max, and decimal length is also less than max
			if(isNumeric(valueToEnter) && valueToEnter.substring(0, comma).length() <= firstRow.getElementAtIndex(indexOfFirstRow).getWholeNum() &&
					valueToEnter.substring(comma, valueToEnter.length() - 1).length() <= firstRow.getElementAtIndex(indexOfFirstRow).getDecimal())
			{
				return true;
			}else 
			{
				System.out.println("You entered an invalid entry for a Double Field");
				throw new IllegalArgumentException();
			}
		}
				
		if(type.equals("Boolean"))
		{
			if(valueToEnter.toLowerCase().equals("true") | valueToEnter.toLowerCase().equals("false"))
			{
				return true;
			}else
			{
				System.out.println("You entered an invalid entry for an Boolean Field");
				throw new IllegalArgumentException();
			}
		}
		if(type.equals("String"))
		{
			if(valueToEnter.length() < firstRow.getElementAtIndex(indexOfFirstRow).getMaxStringLength() && valueToEnter.startsWith("'"))
			{
				return true;
			}else
			{
				System.out.println("You entered an invalid entry for a String Field either too long or is not surrounded with ' on each side");
				throw new IllegalArgumentException();
			}
		}
		
		return false;
	}
	
	
	//method taken from https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
	//checks if a string is numeric 
	private static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	
	public void deleteRow(int atIndex)
	{
		table.remove(atIndex);
		//update BTree
	}
	
	//returns the table name 
	public String getTableName()
	{
		return tableName;
	}
	//returns the firstRow of the table 
	public Row getFirstRow()
	{
		return firstRow;
	}
	//returns the table as an arraylist of rows 
	public ArrayList<Row> getTableRows()
	{
		return table;
	}
	//finds the index of the row in the arraylist- used to delete rows becuase couldnt use list.removeAll()
	public int findRowIndex(Row r, ArrayList<Row> rowsSatisfyWhere)
	{
		int i;
		for(i = 0; i < rowsSatisfyWhere.size(); i++)
		{
			if(r.equals(rowsSatisfyWhere.get(i)))
			{
				return i;
			}
		}
		//not found 
		return -1;
	}
	//checks whether a row is found in an arraylist or rows 
	public boolean containedInTheArrayList(Row r, ArrayList<Row> toCheck)
	{
		boolean found = false;
		for(Row ro: toCheck)
		{
			if(r.equals(ro))
			{
				found = true;
			}
		}
		return found;
	}
	//checks whether any columns are invalid - throws an error if one is not in the chart 
	private void checkColumnsAllInChart(ColumnValuePair[] cvp)
	{		
		for(ColumnValuePair c: cvp)
		{
			boolean found = false;
			int i;
			for(i = 0; i<firstRow.length(); i++)
			{
				if(c.getColumnID().getColumnName().equals(firstRow.getElementAtIndex(i).getVal().toString()))
				{
					found = true;
				}
				
			}
			if(!found)
			{
				System.out.println("You asked to enter information into a column that doesnt Exist");
				throw new IllegalArgumentException();
			}
		}
	}
	
	//returns the indexes in the given ArrayList<ArrayList<Object>> to delete
	private ArrayList<Integer> getRidOfDoubles(ResultSet makeUnique)
	{
		ArrayList<ResultRow> rr = makeUnique.getArrayList();
		ArrayList<Integer> toDelete = new ArrayList<Integer>();
		int i;
		for(i = 0; i < rr.size(); i++)
		{
			if(hasDuplicateAfterInList(rr, i))
			{
				toDelete.add(i);
			}
		}
		return toDelete;
		
	}
	//checks if there is a duplicate for the row at the given index- 
	private Boolean hasDuplicateAfterInList(ArrayList<ResultRow> rows , int indexToCheck)
	{
		ArrayList<Object> compareRow = rows.get(indexToCheck).getRow();
		int j;
		for(j = indexToCheck + 1 ; j < rows.size(); j++)
		{
			if(compareRow.equals(rows.get(j).getRow()))
			{
				return true; 
			}
		}
		return false;
	}
	
}
