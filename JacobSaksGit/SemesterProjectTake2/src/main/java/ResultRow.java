import java.util.ArrayList;

public class ResultRow {
	
	private ArrayList<Object> row;
	
	public ResultRow()
	{
	row = new ArrayList<Object>();
	}
	
	public ResultRow(ArrayList<Object> ao)
	{
		row = new ArrayList<Object>();
		row.addAll(ao);
	}
	
	public void addObject(Object val)
	{
		row.add(val);
	}
	
	public ArrayList<Object> getRow()
	{
		return row;
	}
	public int numbOfCells()
	{
		return row.size();
	}
	public String getObjectAtIndex(int index)
	{
		if(row.get(index) != null)
		{
			return row.get(index).toString();
		}
		return null;
	}
	
}
