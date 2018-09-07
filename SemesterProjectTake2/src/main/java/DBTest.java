
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;




public class DBTest {
	static SQLParser parser = new SQLParser();
	private CreateTableQuery hi;
	

	public static void main(String[] args) throws Exception
	{
		//create a Database object to manage the  queries
		DataBase testDB = new DataBase();
		
		
//---------CREATE TABLE TEST--------------
		System.out.println("------------------STARTING CREATE TABLE TEST---------------------");
		//Make a new table, with all the different variable types and and limitations and column qualities
		String query = "CREATE TABLE YCStudent"
				+ "("
				+ " BannerID int,"
				+ " SSNUM int UNIQUE,"
				+ " FirstName varchar(255),"
				+ " LastName varchar(255) NOT NULL,"
				+ " GPA decimal(1,2) DEFAULT 0.00,"
				+ " CurrentStudent boolean DEFAULT true,"
				+ " PRIMARY KEY (SSNUM)"
				+ ");";
		//create the Table and print out the columns- shows the table was created and ResultSet works correctly 
		System.out.println("Query String: " + query);
		testDB.execute(query);
		
		System.out.println("-------------DONE WITH CREATE TABLE TEST--------------");
		
//----------CREATE INDEX TEST--------------
		System.out.println("-------------STARTING CREATE INDEX TESTING-----------");
		String index1 = ("CREATE INDEX GPA_Index on YCStudent (GPA)");
		System.out.println("Query String: " + index1);
		//create the index- the Database object will print the resultSet- should be true if the index was successful
		testDB.execute(index1);
		//Print the field value indicating whether the column has been indexed,of the GPA RowElement 
		System.out.println(testDB.getTables().get(0).getFirstRow().getElementAtIndex(1).isIndexed());
		
		System.out.println("-------------DONE WITH CREATE INDEX TEST--------------");
		
//---------INSERT ROWS TEST--------------
		System.out.println("---------------STARTING INSERT ROWS TEST----------------");
		//insert eight valid rows to show which kinds or row insertions are legal
		//inserting eight will also show us the BTree is working to store the values with the splitting
		
		//first few insertions have every column filled, column Value pairs in different orders to show the pairing works and it doesn't have to be the same order.
		
		 String query0 = ("INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA, CurrentStudent) VALUES (80035556, 123456, 'Saul','cohen' , 4.0, True);");

		 String query1 = ("INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA, CurrentStudent) VALUES (800456, 1234567, 'Jacob', 'Saks', 4.0, True);");

		 String query2 = ("INSERT INTO YCStudent (LastName,FirstName, CurrentStudent, BannerID, SSNUM, GPA) VALUES ('Shmo', 'Joe', false, 800123456, 121212121, 4.02);");

	 	 String query3 = "INSERT INTO YCStudent (SSNUM, FirstName, BannerID, CurrentStudent, GPA, LastName) VALUES (123123123, 'Ploni', 8000654321, true, 1.03, 'Almoni');";

		 String query4 = "INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA, CurrentStudent) VALUES (800459, 12347, 'Dufe', 'HDFssd', 1.0, False);";
		 //example of the default Current Student Field working 
		 String query5 = "INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA) VALUES (800460, 15369, 'Dufenshmirtz', 'blah', 1.0);";
		 //leave the first name out- should be replaced with a null 
		 String query6 = "INSERT INTO YCStudent (BannerID, SSNUM, LastName, GPA, CurrentStudent) VALUES (800461, 36567, 'fsssss', 6.0, False);";

		 String query7 = "INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA, CurrentStudent) VALUES (800462, 100000, 'shmirtz', 'namess', 4.8, False);";
		 
		//execute all the queries - should all print true 
		 System.out.println("Query String: " + query0);
		 	testDB.execute(query0);
		 	System.out.println("Query String: " + query1);
		 	testDB.execute(query1);
		 	System.out.println("Query String: " + query2);
			testDB.execute(query2);
			System.out.println("Query String: " + query3);
			testDB.execute(query3);
			System.out.println("Query String: " + query4);
			testDB.execute(query4);
			System.out.println("Query String: " + query5);
			testDB.execute(query5);
			System.out.println("Query String: " + query6);
			testDB.execute(query6);
			System.out.println("Query String: " + query7);
			testDB.execute(query7);
			
			System.out.println();
		//print all the rows - do this by selecting all- database prints out the table
		String select44 = ("SELECT * FROM YCStudent;");
		System.out.println("Query String: " + select44);
		testDB.execute(select44);
		
		
		//put in some error inducing insertions and print the error message to make sure we're getting the right error message
		//the first two will not meet the required value constraints
		 
		//varchar too long 
		String badInsertOne = "INSERT INTO YCStudent (FirstName, LastName, BannerID, CurrentStudent, SSNUM, GPA) VALUES ('Bad', 'Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion Insertaion ', 800000001, true, 00000000, 3.95);";
		//decimal too many digits in integer part 
		String badInsertTwo = "INSERT INTO YCStudent (FirstName, LastName, BannerID, CurrentStudent, SSNUM, GPA) VALUES('Bad', 'Insertion', 800000002, true, 00000000, 11.0);";
		
		//a null value in a NOT NULL column
		String badInsertThree = "INSERT INTO YCStudent (FirstName, BannerID, CurrentStudent, SSNUM, GPA) VALUES('Bad', 800000003, true, 00000000, 1.00);";
		//invalid column name
		String badInsertFour = "INSERT INTO YCStudent (FirstName, LastName, BannerID, CurrentStudent, SSNUM, GA) VALUES('Bad', 'Insertion', 800000004, true, 00000000, 1.00);";
		//invalid table name
		String badInsertFive = "INSERT INTO YCSudent (FirstName, LastName, BannerID, CurrentStudent, SSNUM, GPA) VALUES('Bad', 'Insertion', 800000005, true, 00000000, 1.00);";
		//insert a non-unique val into unique column 
		String badInsertSix = "INSERT INTO YCStudent (BannerID, SSNUM, FirstName, LastName, GPA, CurrentStudent) VALUES (800462, 100000, 'Dufenshmirtz', 'blah', 4.8, False);";

		System.out.println("Query String: " + badInsertOne);
		testDB.execute(badInsertOne);
		System.out.println("Query String: " + badInsertTwo);
		testDB.execute(badInsertTwo);
		System.out.println("Query String: " + badInsertThree);
		testDB.execute(badInsertThree);
		System.out.println("Query String: " + badInsertFour);
		testDB.execute(badInsertFour);
		System.out.println("Query String: " + badInsertFive);
		testDB.execute(badInsertFive);
		System.out.println("Query String: " + badInsertSix);
		testDB.execute(badInsertSix);
		
		System.out.println("------------------DONE WITH INSERT TESTING---------------");
		
//---------BTREE TEST--------------		
		System.out.println("----------------STARTING BTREE TESTING-----------------");
		//Now we'll test if the BTrees successfully did all the insertion- this is the BTree in the GPA column which was indexed 
		 BTree checkIt = testDB.getTables().get(0).getFirstRow().getElementAtIndex(1).getBTree();
		 
		 Node root = checkIt.getRoot();
		 //becuase there were 8 entries the root should have 2 entries 
		 System.out.println(root.entryCount());
		 Entry[] rootEntries = root.getEntries();
		 
		 Node child1 = rootEntries[0].getChild();
		 Node child2 = rootEntries[1].getChild();
		 //the sum of these two outputs should be 7 becuase of the 8 inputs, there are two doubles so 2 will not have its own entry and will just be 
		 //entered into the Value Array of the existing Node- therefore the inserts yield 6 new nodes and the Sentinel = 7
		 System.out.println(child1.entryCount());
		 System.out.println(child2.entryCount());
		
		 //make sure the primary key was also indexed correctly- its index is 5
		 BTree checkIt2 = testDB.getTables().get(0).getFirstRow().getElementAtIndex(5).getBTree();
		 
		 Node root2 = checkIt2.getRoot();
		 //becuase there were 8 entries the root should have 2 entries 
		 System.out.println(root2.entryCount());
		 Entry[] rootEntries2 = root2.getEntries();
		 
		 Node child11 = rootEntries2[0].getChild();
		 Node child22 = rootEntries2[1].getChild();
		 //the sum of these two outputs should be 9 because of the 8 inputs plus the Sentinel = 9
		 System.out.println(child11.entryCount());
		 System.out.println(child22.entryCount());
		
		 
		 
		 
			
			Object[] rootkeys = new Object[6];
			Object[] node1keys = new Object[6];
			Object[] node2keys = new Object[6];
			Object[] node1values = new Object[6];
			Object[] node2values = new Object[6];
			
			//keys of the root 
			for(int i = 0; i < 2; i++)
			{
			rootkeys[i] = root.getEntryInIndex(i).getKey().getVal().toString();
			}
			System.out.println("---------Child one key-value printouts.-------");
			//keys of child one and corresponding values in arraylists 
			for(int i = 0; i < 3; i++)
			{
				node1keys[i] = child1.getEntryInIndex(i).getKey().getVal().toString();
				node1values[i] = child1.getEntryInIndex(i).getEntryVal();
				System.out.println(node1keys[i]);
				System.out.println(node1values[i]);
				
			}
			System.out.println("---------End of child one key-value printouts. Beginning Child two-------");
			//keys of child one and corresponding values in arraylists 
			for(int i = 0; i < 4; i++)
			{
				node2keys[i] = child2.getEntryInIndex(i).getKey().getVal().toString();
				node2values[i] = child2.getEntryInIndex(i).getEntryVal();
				System.out.println(node2keys[i]);
				System.out.println(node2values[i]);
			}
			
			//BTree is working exactly as it should- it adds values(the rows) of equal keys into the arraylist of the values for the given key
			System.out.println("-----------DONE WITH BTREE TESTING-----------");
			

			
//----------------------------SELECT TESTING-------------------------------
		 //in order to show the database can handle multiple tables I will make a second table- 
		 //I will just make sure that it can do basic tests on this second table becuase as long as it can
		 //correctly find the table it can do any complicated select that was shown above 
			
		System.out.println("---------------STARTING SELECT TESTING---------------");
			String createTable2 = "CREATE TABLE selecttest"
			+ "("
			+ " one int,"
			+ " two int,"
			+ " three int,"
			+ " name varchar(200),"
			+ " PRIMARY KEY (name)"
			+ ");";
			System.out.println("Query String: " + createTable2);
			testDB.execute(createTable2);
			
			String a = "INSERT INTO selecttest (one, two, three, name) VALUES(1, 1, 1, 'a');";
			String b = "INSERT INTO selecttest (one, two, three, name) VALUES(1, 1, 2, 'b');";
			String c = "INSERT INTO selecttest (one, two, three, name) VALUES(1, 1, 3, 'c');";
			String d = "INSERT INTO selecttest (one, two, three, name) VALUES(1, 2, 1, 'd');";
			String e = "INSERT INTO selecttest (one, two, three, name) VALUES(1, 2, 2, 'e');";
			String f = "INSERT INTO selecttest (one, two, three, name) VALUES(2, 1, 3, 'f');";
			String g = "INSERT INTO selecttest (one, two, three, name) VALUES(2, 1, 2, 'g');";
			String h = "INSERT INTO selecttest (one, two, three, name) VALUES(2, 2, 1, 'h');";
			
			System.out.println("Query String: " + a);
			testDB.execute(a);
			System.out.println("Query String: " + b);
			testDB.execute(b);
			System.out.println("Query String: " + c);
			testDB.execute(c);
			System.out.println("Query String: " + d);
			testDB.execute(d);
			System.out.println("Query String: " + e);
			testDB.execute(e);
			System.out.println("Query String: " + f);
			testDB.execute(f);
			System.out.println("Query String: " + g);
			testDB.execute(g);
			System.out.println("Query String: " + h);
			testDB.execute(h);
			
			//first things first, I will check that the system can support multiple tables:
			//each table's info should print out under the correct table name:
			for(Table t: testDB.getTables())
			{
			System.out.println("Printing info from: " + t.getTableName());
			//print all rows 
			String selectAll2 = ("SELECT * FROM " + t.getTableName() + ";");
			testDB.execute(selectAll2);
			System.out.println("Done with Table:" + t.getTableName());
			System.out.println("");
			}
			
			//Now that we know the system supports multiple tables, onto checking select:
			//first lets check SELECT FUNCTIONS:
			String selectFunction1 = "SELECT COUNT (three) FROM selecttest;";
			String selectFunction2 = "SELECT COUNT (DISTINCT three) FROM selecttest;";
			String selectFunction3 = "SELECT MAX (two) FROM selecttest;";
			//lets throw in one test for using the BTree on an indexed row (NOTE: FROM YCStudent Table)
			String selectFunction4 = "SELECT MIN (SSNUM) FROM YCStudent;";
			String selectFunction5 = "SELECT SUM (one) FROM selecttest;";
			String selectFunction6 = "SELECT SUM (DISTINCT one) FROM selecttest;";
			String selectFunction7 = "SELECT SUM (DISTINCT one), AVG(three) FROM selecttest;";//test getting two values
			
			System.out.println("Query String: " + selectFunction1);
			testDB.execute(selectFunction1);
			System.out.println("Query String: " + selectFunction2);
			testDB.execute(selectFunction2);
			System.out.println("Query String: " + selectFunction3);
			testDB.execute(selectFunction3);
			System.out.println("Query String: " + selectFunction4);
			testDB.execute(selectFunction4);
			System.out.println("Query String: " + selectFunction5);
			testDB.execute(selectFunction5);
			System.out.println("Query String: " + selectFunction6);
			testDB.execute(selectFunction6);
			System.out.println("Query String: " + selectFunction7);
			testDB.execute(selectFunction7);
			
			//output should be
			//8
			//3
			//2
			//12347
			//11.0
			//3.0
			//should be 9.0 and 1.5
			
			System.out.println("---------------DONE WITH SELECT FUNCTIONS---------------");
		    
			System.out.println("---------------TESTING SELECT ROWS---------------------");
			//because we've already tested the conditions in update and delete, I don't think I need to test them more here. 
			//I'm instead going to focus on the features specific to select.
			
			//Lets start with selecting the whole table
			String selectRows1 = "SELECT one, two, three, name FROM selecttest;";
			String selectRows1a ="SELECT * FROM selecttest;";//should have the same output as 1
			
			//now lets test taking only some of the rows (with a simple condition, why not)
			String selectRows2 = "SELECT three, name FROM selecttest WHERE one>1;";
			//lets try with distinct
			String selectRows3 = "SELECT DISTINCT one FROM selecttest;";
			//distinct with two columns
			String selectRows4 = "SELECT DISTINCT one, two FROM selecttest;";
			//Finally, lets put it all together with a very complicated, layered orderedBy
			String selectRows5 = "SELECT name FROM selecttest ORDER BY one ASC, two DESC, three ASC;";
			
			System.out.println("Query String: " + selectRows1);
			testDB.execute(selectRows1);
			System.out.println("Query String: " + selectRows1a);
			testDB.execute(selectRows1a);
			//should print out all columns for every row
			System.out.println("done with select one");
			System.out.println("Query String: " + selectRows2);
			testDB.execute(selectRows2);
			//should print columns three and one from rows f, g, h
			System.out.println("done with select two");
			System.out.println("Query String: " + selectRows3);
			testDB.execute(selectRows3);
			//should print 1, 2 (the only two distinct values)
			System.out.println("done with select three");
			System.out.println("Query String: " + selectRows4);
			testDB.execute(selectRows4);
			//should print four "pairs" of values 11, 12, 21, 22
			System.out.println("done with select four");
			System.out.println("Query String: " + selectRows5);
			testDB.execute(selectRows5);
			//Should print in the following order: D E A B C H G F
			System.out.println("done with select five");
			
			System.out.println("----------------------DONE WITH SELECT TESTING--------------------");
		 
//---------------UPDATE TEST----------------------
			System.out.println("-------------------STARTING UPDATE TEST------------------");
		 

			System.out.println("Print the table : Query String: " + select44);
			testDB.execute(select44);
			
			//test a basic update with a compound condition, One condition from the BTree, the other From the list of Entries
			String updateOne = "UPDATE YCStudent SET FirstName='Avery' WHERE BannerID=80035556 AND LastName='cohen';";
			System.out.println("Query String: " + updateOne);
			testDB.execute(updateOne);
			System.out.println("Print the table : Query String: " + select44);
			testDB.execute(select44);
			//test >= for the BTree and the list
			String updateTwo = "UPDATE YCStudent SET GPA=3.85 WHERE BannerID>=800462;";
			System.out.println("Query String: " + updateTwo);
			testDB.execute(updateTwo);
			testDB.execute(select44);
			//test the <> operator, 
			//also tests changing multiple things with one update
			String updateThree = "UPDATE YCStudent SET LastName='Saco', CurrentStudent=false, GPA=3.99 WHERE GPA <> 4.0;";
			System.out.println("Query String: " + updateThree);
			testDB.execute(updateThree);
			testDB.execute(select44);
			//test the <= operator
			String updateFour = "UPDATE YCStudent SET LastName = 'funnyName' WHERE GPA<=3.99;";
			System.out.println("Query String: " + updateFour);
			testDB.execute(updateFour);
			testDB.execute(select44);
			
			//After all these updates, Every person except for Jacob Saks should have a 3.99 GPA , lastName - funnyName and have false for CurrentStudent 
			//print out the table info to check
			
			//print all the rows - do this by selecting all- database prints out the table
			String selectAll1 = ("SELECT * FROM YCStudent;");
			System.out.println("Print the table : Query String: " + selectAll1);
			testDB.execute(selectAll1);
			
			//since we changed a lot of the GPA's, we need to make sure the corresponding changes were made in the BTree
			//the New value for the SSNum would be the smallest value (excluding the Sentinal Value of course), so the value of the First Non-sentinal Entry
			//of the first Node should be 999999999. In addition, the old value should have been deleted so the old SSNum (123456789) should have an empty
			//list as it's value. Furthermore, nothing should change in the BannerID BTree - so this should still print out 800123456 during that iteration.
			
			
			//we expect one value for the 4.0 key and the other 7 values will be in the 3.99 key 
			BTree checkIt22 = testDB.getTables().get(0).getFirstRow().getElementAtIndex(1).getBTree();
			 
			 Node root22 = checkIt22.getRoot();
			 //becuase there were 8 entries the root should have 2 entries 
			 System.out.println(root.entryCount());
			 Entry[] rootEntries22 = root.getEntries();
			 
			 Node child12 = rootEntries22[0].getChild();
			 Node child222 = rootEntries22[1].getChild();
			
			Object[] rootkeys2 = new Object[6];
			Object[] node1keys2 = new Object[6];
			Object[] node2keys2 = new Object[6];
			Object[] node1values2 = new Object[6];
			Object[] node2values2 = new Object[6];
			
			//keys of the root 
			for(int i = 0; i < 2; i++)
			{
			rootkeys2[i] = root22.getEntryInIndex(i).getKey().getVal().toString();
			}
			System.out.println("---------Child one key-value printouts.-------");
			//keys of child one and corresponding values in arraylists- should have all rows under GPA = 3.99 except for one under 4.0
			for(int i = 0; i < 5; i++)
			{
				node1keys2[i] = child12.getEntryInIndex(i).getKey().getVal().toString();
				node1values2[i] = child12.getEntryInIndex(i).getEntryVal();
				System.out.println(node1keys2[i]);
				System.out.println(node1values2[i]);
				
			}
			System.out.println("---------End of child one key-value printouts. Beginning Child two-------");
			//keys of child one and corresponding values in arraylists 
			for(int i = 0; i < 4; i++)
			{
				node2keys2[i] = child222.getEntryInIndex(i).getKey().getVal().toString();
				node2values2[i] = child222.getEntryInIndex(i).getEntryVal();
				System.out.println(node2keys2[i]);
				System.out.println(node2values2[i]);
			}
			
			
			
			//Now let's test a few invalid updates
			//invalid column name
			String badUpdateOne = "UPDATE YCStudent SET SSNum=10 WHERE FirstName='Avi';";
			System.out.println("Query String: " + badUpdateOne);
			testDB.execute(badUpdateOne);
			//invalid table name
			String badUpdateTwo = "UPDATE SYMSStudent SET GPA=4.00 WHERE FirstName='Test' AND LastName='Banks';";
			System.out.println("Query String: " + badUpdateTwo);
			testDB.execute(badUpdateTwo);
			//proposed entries don't meet the data parameters
			String badUpdateThree = "UPDATE YCStudent SET GPA=40.00 WHERE FirstName='Joe';";
			System.out.println("Query String: " + badUpdateThree);
			testDB.execute(badUpdateThree);
			String badUpdateFour = "UPDATE YCStudent SET FirstName='aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' WHERE LastName='Saks';";
			System.out.println("Query String: " + badUpdateFour);
			testDB.execute(badUpdateFour);
			System.out.println("");
			
			System.out.println("----------------DONE WITH UPDATE TESTING-----------------");
			
			
			//--------------------DELETE TEST----------------------------
			System.out.println("---------------STARTING DELETE TESTING------------------");
			//now lets delete various entries, and along with it test some more conditions
			String deleteOne = "DELETE FROM YCStudent WHERE FirstName='Joe';";
			testDB.execute(deleteOne);
			String deleteTwo = "DELETE FROM YCStudent WHERE BannerID=800459;";
			testDB.execute(deleteTwo);
			
			// print out the table to see that deleted 
			System.out.println("Print the table : Query String: " + selectAll1);
			testDB.execute(selectAll1);
			//table should have deleted the entry with Joe as first name and the entry with banner ID 800459 - it does and only 6 entries remaining
			
			//to Delete All just give a delete statement with no where 
			String deleteAll = "DELETE FROM YCStudent;";
			testDB.execute(deleteAll);
			// print out the table to see that everything was deleted 
			System.out.println("Print the table : Query String: " + selectAll1);
			testDB.execute(selectAll1);
			
			
			System.out.println("-------------DONE WITH DELETE TESTING---------------");
			System.out.println("-------------DONE WITH PROJECT---------------");
			System.out.println("-------------Thank you Hashem!!!---------------");
	}
	
	
}
