
public class Node {
	
	private int entryCount;
	private Entry[] entries;
	private int max;

	
	//constructor for Node after split occurs 
	public Node(int initialCount)
	{
		max = 6;
		entryCount = initialCount;
		entries = new Entry[max];
	}
	
	//constructor for new Node
	public Node()
	{
		max = 6;
		entryCount = 0;
		entries = new Entry[max];
	}
	
	public int getMax()
	{
		return max;
	}
	
	public void addEntry(Entry enter)
	{
		entries[entryCount]= enter;
		entryCount++;
	}	
	
	public Entry[] getEntries()
	{
		return entries;
	}
	
	public Entry getEntryInIndex(int index)
	{
		return entries[index];
	}
	
	public void setEntryInIndex(int index, Entry newEntry)
	{
		entries[index]= newEntry;
	}
	
	public int entryCount()
	{
		return entryCount;
	}
	
	public void setEntryCount(int newCount)
	{
		entryCount = newCount;
	}
	
	public void incrementEntryCount()
	{
		entryCount++;
	}
	
	public void decrementEntryCount()
	{
		entryCount--;
	}
	
}
