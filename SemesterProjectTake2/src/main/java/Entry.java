import java.util.ArrayList;

public class Entry<T> {

	private ArrayList<Value> values;
	//private Value val1;
	private Node child; 
	private Key key;
	
	public Entry(Key keyVal, Value val, Node kid)
	{
		values = new ArrayList<Value>();
		//val1 = new Value(val);
		Value val1 = val;
		values.add(val1);
		child = kid;
		key = keyVal;
	}
	
	public Node getChild()
	{
		return child;
	}
	
	public void setChild(Node kid)
	{
		child = kid;
	}
	
	public ArrayList<Value> getEntryVal()
	{
		return values;
	}
	
	public void clearArrayOfValues()
	{
		values = new ArrayList<Value>();
	}
	
	public void addEntryVal(Value newVal)
	{
		values.add(newVal);
	}
	
	public Key getKey()
	{
		return key;
	}
	
	public void setKey(Key newKey)
	{
		key = newKey;
	}
	
	public void deleteVal(Value toDelete)
	{
		ArrayList<Value> toRemove = new ArrayList<Value>();
		//Object dsd = toDelete.getVal();
		//Object sds = values.get(0).getVal();
		
		for(Value v: values)
		{
			if(v.getVal().equals(toDelete.getVal()))
			{
				toRemove.add(v);
				
			}
		}
		values.removeAll(toRemove);
	}
}
