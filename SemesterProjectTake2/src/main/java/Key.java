

public class Key<T extends Comparable<T>> implements Comparable<T> {
	
	private T keyValue;
	
	public Key(T val)
	{
		keyValue = val;
	}
	public T getVal()
	{
		return keyValue;
	}
	
	public int compareTo(T a)
	{
		return getVal().compareTo(a);
	}
	
	public int compareTo(Key<T> other)
    {
        return getVal().compareTo(other.getVal());
    }
	
	
	public static void main(String[] args)
	{
		Key ex = new Key("hi");
		   //Key ex1 = new Key (ex);
		Key cmp = new Key(8);
		int rtn = ex.compareTo(cmp);
		int val = rtn;
		//System.out.println(x);
	}
	
	
}
