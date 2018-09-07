import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import net.sf.jsqlparser.JSQLParserException;

public class MyClass {

	public void parseSomeSQL(String someSQL) throws JSQLParserException
	{
		SQLParser parser = new SQLParser();
		SelectQuery result = (SelectQuery)parser.parse("SELECT first, last, id FROM students,teachers,schools;");
		ColumnID[] colsNamedInQuery = result.getSelectedColumnNames();
		System.out.println("The query asked to select the following columns: ");
		for(ColumnID col : colsNamedInQuery)
		{
			System.out.println(col.getTableName() + "." + col.getColumnName());
		}
	} 
}
