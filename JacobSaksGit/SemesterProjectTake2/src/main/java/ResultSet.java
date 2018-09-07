import java.util.ArrayList;

public class ResultSet {

	private ArrayList<ResultRow> results;
	
	public ResultSet()
	{
		results = new ArrayList<ResultRow>();
	}
	public ResultSet(ArrayList<ArrayList<Object>> toEnter)
	{
		results = new ArrayList<ResultRow>();
		for(ArrayList<Object> ao: toEnter)
		{
			ResultRow toAdd = new ResultRow(ao);
			results.add(toAdd);
		}
	}
	
	public void addRow(ResultRow toAdd)
	{
		results.add(toAdd);
	}
	
	public void addRowAtIndex(ResultRow toAdd, int index)
	{
		results.add(index, toAdd);
	}
	
	public ArrayList<ResultRow> getArrayList()
	{
		return results;
	}
	
	public ArrayList<ArrayList<Object>> getArrayListOfRows()
	{
		ArrayList<ArrayList<Object>> give = new ArrayList<ArrayList<Object>>();
		for(ResultRow r : results)
		{
			give.add(r.getRow());
		}
		return give;
	}
	public int numbOfRows()
	{
		return results.size();
	}
	public ResultRow getRowAtIndex(int index)
	{
		return results.get(index);
	}
	public void deleteRow(int index)
	{
		results.remove(index);
	}
	
}
