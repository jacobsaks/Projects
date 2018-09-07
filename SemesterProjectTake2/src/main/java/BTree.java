import java.lang.*;
import java.util.ArrayList;


public class BTree<K extends Comparable<K>, V> {

	private String bTreeName;
	private String keyColumnName;
	private Node root;
	private Entry sentinel;
	private Key sentinelKey;
	private int height;
	private int max; 
	private ArrayList<Value> allAbove;
	private ArrayList<Value> allBelow;

	
	//public BTree(String keyClmName, String nameOfBTree, K sentinelVal)
	public BTree(String keyClmName, K sentinelVal)
	{
		sentinelKey = new Key (sentinelVal);
		sentinel = new Entry(sentinelKey, null, null);
		root = new Node(0);
		root.addEntry(sentinel);
		max = root.getMax();
		height = 0;
		//bTreeName = nameOfBTree;
		keyColumnName = keyClmName;
		
	}
	

	
	public static void main(String[] args)
	{
		//Key sentinel = new Key(0);
		BTree root = new BTree("hi", 0);
		//Key find = new Key (9);
		root.put(5, "hello5");
		root.put(10, "hey10");
		root.put(15, "hello15");
		root.put(20, "20ello");
		root.put(14, "14hello");
		root.put(6, "6hello");
		root.put(11, "11hello");
		root.put(17, "hello17");
		root.put(18, "hey18");
		root.put(12, "hello12");
		root.put(22, "22ello");
		root.put(1, "1hello");
		root.put(7, "7hello");
		root.put(25, "25hello");
		root.put(7, "7redohello");
		root.put(7, "7333");
		root.put(7, "74444");
		root.put(35, "hello35");
		root.put(40, "hey40");
		root.put(45, "hello45");
		root.put(30, "30ello");
		root.put(34, "34hello");
		root.put(36, "36hello");
		root.put(37, "37hello");
		root.put(47, "hello47");
		root.put(38, "hey38");
		root.put(42, "hello42");
		root.put(42, "42ello");
		root.put(57, "57hello");
		root.put(48, "48hello");
		root.put(49, "49hello");
		//root.deleteEntry(7);
		root.put(67, "67llo");
		ArrayList<Value> catchit = root.getValue(6);
		root.put(46, "fshjkdf");
		int j = 1;
		//Value val = new Value("7hello");
		root.deleteVal(7, "7hellojkjkj");
		j= j+1;
		//ArrayList<Value> resul = root.getAllValuesAbove(12);
		//ArrayList<Value> resul2 = root.getAllValuesBelow(13);
		
		//root.getValue(find);
		//sentinel.getVal();
		
	}
	
	//public method to get all Values Below a given key(inclusive if key matches a value- that value is returned in the list)
	public ArrayList<Value> getAllValuesBelow( K keyValue)
	{
		//create the list in which return values are added 
		allBelow = new ArrayList<Value>();
		return this.getAllValuesBelow(root, keyValue, height);
	}
	
	//the private method to get all values below
	private ArrayList<Value> getAllValuesBelow(Node currentNode, K keyValue, int height)
	{
		Key key = new Key(keyValue);
		//the index of the node under which the key value is located 
		int indexOfSubtree = getIndexOfSubtree(currentNode, key);
		Entry[] entries = currentNode.getEntries();
		
		//if at a leaf node- add the values to the left of the key value to the list 
		if(height == 0)
		{
			//bug - will miss the value of the exact key- if statement didnt work 
			//if(entries[indexOfSubtree].getKey().compareTo(key) == 0)
			for(int v= 0; v < indexOfSubtree + 1; v++)
			{
				for(int i = 0; i <entries[v].getEntryVal().size(); i++)
				{
					allBelow.add((Value)entries[v].getEntryVal().get(i));
				}
				//allBelow.add(entries[v].getEntryVal());
			}
		} 
		//if not a leaf node, recursively call getAllValuesBelow until get to a leaf, additionally get all values of 
		//subtrees that are less than the key value on each level 
		else
		{
			getAllValuesBelow(entries[indexOfSubtree].getChild(), keyValue, --height);
		
			//Add all values in subtrees to the left of indexOfSubtree
			for (int i = 0 ; i < indexOfSubtree; i++ )
			{
				Node holdThis = entries[i].getChild();
				addAllValuesInSubtree(holdThis, height, allBelow);	
			}
		}	
		return allBelow;
	}
	
	
	
	//public method to get all Values above a given key(exclusive if key matches a value- that value is not returned in the list)
	public ArrayList<Value> getAllValuesAbove( K keyValue)
	{
		//create the list in which return values are added
		allAbove = new ArrayList<Value>();
		return this.getAllValuesAbove(root, keyValue, height);
	}
	
	//the private method to get all values above
	private ArrayList<Value> getAllValuesAbove(Node currentNode, K keyValue, int height)
	{
		Key key = new Key(keyValue);
		//the index of the node under which the key value is located 
		int indexOfSubtree = getIndexOfSubtree(currentNode, key);
		Entry[] entries = currentNode.getEntries();
		//if at a leaf node- add the values to the right of the key value to the list 
		if(height == 0)
		{
			//bug - will miss the value of the exact key- if statement didnt work 
			//if(entries[indexOfSubtree].getKey().compareTo(key) == 0)
			for(int v= indexOfSubtree + 1; v < currentNode.entryCount(); v++)
			{
				for(int i = 0; i <entries[v].getEntryVal().size(); i++)
				{
					allAbove.add((Value)entries[v].getEntryVal().get(i));
				}
				//allAbove.add(entries[v].getEntryVal());
			}
		} 
		//if not a leaf node, recursively call getAllValuesAbove until get to a leaf, additionally get all values of 
		//subtrees that are greater than the key value on each level 
		else
		{
			getAllValuesAbove(entries[indexOfSubtree].getChild(), keyValue, --height);
		
			//Add all values in subtrees to the right of indexOfSubtree
			for (int i = indexOfSubtree + 1 ; i < currentNode.entryCount(); i++ )
			{
				Node holdThis = entries[i].getChild();
				addAllValuesInSubtree(holdThis, height, allAbove);	
			}
		}	
		return allAbove;
	}
	
	
	//returns all values under a certain Node
	private void addAllValuesInSubtree(Node current, int height, ArrayList<Value> toAddTo)
	{	
		Entry[] entries = current.getEntries();
		if(height == 0)
		{
			for(int v= 0; v < current.entryCount(); v++)
			{
				for(int i = 0; i <entries[v].getEntryVal().size(); i++)
				{
					toAddTo.add((Value)entries[v].getEntryVal().get(i));
				}
				//toAddTo.add(entries[v].getEntryVal());
			}
		} else
		{
			for(int v= 0; v < current.entryCount(); v++)
			{
				addAllValuesInSubtree(entries[v].getChild(), --height, toAddTo);
			}
		}
	}
	
	//returns the index of the node where the key is in its subtree 
	private int getIndexOfSubtree(Node current, Key key)
	{
		Entry[] entries = current.getEntries();
		int j;
			for (j = 0; j < current.entryCount(); j++)
			{
				//Value cmp1 = (Value)key.getVal();
				//Value cmp2 = (Value)entries[j].getKey().getVal();
				if (j + 1 == current.entryCount() || key.compareTo(entries[j + 1].getKey()) < 0)
				//if(cmp1.compareTo( cmp2) == 0)
				{
					//found desired key. Return its index
					break;
				}
			}
			return j;
	}
	
	//Searches through the tree and returns the value for the given keyValue
	public ArrayList<Value> getValue(K keyVal)
	{
		Key key = new Key(keyVal);
		return this.get(this.root, key, this.height);
	}
	
	//private method for getValue
	private ArrayList<Value> get(Node currentNode, Key key, int height)
	{
		Entry[] entries = currentNode.getEntries();
		//current node is external (i.e. height == 0)
		if (height == 0)
		{
			for (int j = 0; j < currentNode.entryCount(); j++)
			{
				//Value cmp1 = (Value)key.getVal();
				//Value cmp2 = (Value)entries[j].getKey().getVal();
				if(key.compareTo( entries[j].getKey()) == 0)
				//if(cmp1.compareTo( cmp2) == 0)
				{
					//found desired key. Return its value
					return entries[j].getEntryVal();
				}
			}
			//didn't find the key
			return null;
		}
		
		//current node is internal (height > 0)
		else
		{
			for (int j = 0; j < currentNode.entryCount(); j++)
			{
				//if (we are at the last key in this node OR the key we
				//are looking for is less than the next key, i.e. the
				//desired key must be in the subtree below the current entry),
				//then recurse into the current entry’s child
				if (j + 1 == currentNode.entryCount() || key.compareTo(entries[j + 1].getKey()) < 0)//second half want key<entries 
				{
					return this.get(entries[j].getChild(), key, height - 1);
				}
			}
			//didn't find the key
			return null;
		}
		
	}
	
	
	//creates a new entry with the given key-value and value, then adds it into the tree 
	public void put(K keyVal, V value)
	{	
		Key key = new Key(keyVal);
		Value val = new Value(value);
		
		Node newNode = this.put(this.root, key, val, this.height);
		//? some counter of nodes or something -- this.n++;
		if(newNode == null)
		{
			return;
		}
		//split the root:
		//Create a new node to be the root.
		//Set the old root to be new root's first entry.
		//Set the node returned from the call to put to be new root's second entry
		Node newRoot = new Node(2);
		newRoot.setEntryInIndex(0, new Entry(this.root.getEntryInIndex(0).getKey(), null, this.root));
		newRoot.setEntryInIndex(1, new Entry(newNode.getEntryInIndex(0).getKey(), null, newNode));
		this.root = newRoot;
		//a split at the root always increases the tree height by 1
		this.height++;
	}
	
	//private method for put
	private Node put(Node currentNode, Key key, Value val, int height)
	{
		int j;
		Entry newEntry = new Entry(key, val, null);
		//external node
		if (height == 0)
		{
			//find index in currentNode’s entry[] to insert new entry
			for (j = 0; j < currentNode.entryCount(); j++)
			{
				//if (less(key, currentNode.getEntryInIndex(j).getKey()))
				if ((key.compareTo(currentNode.getEntryInIndex(j).getKey())) < 0)
				{
					break;
				}
			}
			//if(currentNode.entryCount() == j)
			//if(currentNode.getEntryInIndex(j) == null)
			{
				
			}
			if(currentNode.getEntryInIndex(j - 1).getKey().compareTo(key) == 0)	
			{
				currentNode.getEntryInIndex(j - 1).addEntryVal(val);
				//currentNode.decrementEntryCount();
				return null;
			}
		}
		else
		{
			//find index in node entry array to insert the new entry
			for (j = 0; j < currentNode.entryCount(); j++)
			{
				//if (we are at the last key in this node OR the key we
				//are looking for is less than the next key, i.e. the
				//desired key must be added to the subtree below the current entry),
				//then do a recursive call to put on the current entry’s child
				if ((j + 1 == currentNode.entryCount()) || (key.compareTo(currentNode.getEntryInIndex(j + 1).getKey()) < 0))
				//if ((j + 1 == currentNode.entryCount()) || less(key, currentNode.getEntryInIndex(j + 1).getKey()))	
				{
					//increment j (j++) after the call so that a new entry created by a split
					//will be inserted in the next slot
					Node newNode = this.put(currentNode.getEntryInIndex(j++).getChild(),
				key, val, height - 1);
					if (newNode == null)
					{
						return null;
					}
					//if the call to put returned a node, it means I need to add a new entry to
					//the current node
					newEntry.setKey(newNode.getEntryInIndex(0).getKey());
					newEntry.setChild(newNode);
					break;
				}
			}
		}
		//if key already exists just update value
		//if(currentNode.getEntryInIndex(j).getKey().compareTo(key) == 0)	
		//{
			//currentNode.getEntryInIndex(j).setEntryVal(val);
			//return null;
		//}else
		{	//shift entries over one place to make room for new entry
			for (int i = currentNode.entryCount(); i > j; i--)
			{
				//currentNode.entries[i] = currentNode.entries[i - 1];
				currentNode.setEntryInIndex(i, currentNode.getEntryInIndex(i - 1));
			}
			//add new entry
			currentNode.setEntryInIndex(j, newEntry);
			currentNode.incrementEntryCount();;
			
			if (currentNode.entryCount() < max)
			{
				//no structural changes needed in the tree
				//so just return null
				return null;
			}
			else
			{
				//will have to create new entry in the parent due
				//to the split, so return the new node, which is
				//the node for which the new entry will be created
				return this.split(currentNode);
			}
		}	
			
	}
	
	
	
	// split node in half and adjust children values for higher up nodes 
	private Node split(Node currentNode)
	{
			Node newNode = new Node(max / 2);
			//by changing currentNode.entryCount, we will treat any value
			//at index higher than the new currentNode.entryCount as if
			//it doesn't exist
			currentNode.setEntryCount(max / 2);
			//copy top half of h into t
			for (int j = 0; j < max / 2; j++)
			{
				//newNode.entries[j] = currentNode.entries[max / 2 + j];
				newNode.setEntryInIndex(j, currentNode.getEntryInIndex(max / 2 + j));
			}
			return newNode;
	}
	
	
	public void deleteVal(K keyVal, V theVal)
	{
		Key key = new Key(keyVal);
		Value val = new Value(theVal);
		this.delete(this.root, key, val, this.height);
	}
	
	//private method for delete
	private void delete(Node currentNode, Key key, Value val, int height)
	{
		Entry[] entries = currentNode.getEntries();
		//current node is external (i.e. height == 0)
		if (height == 0)
		{
			for (int j = 0; j < currentNode.entryCount(); j++)
			{
				//Value cmp1 = (Value)key.getVal();
				//Value cmp2 = (Value)entries[j].getKey().getVal();
				if(key.compareTo( entries[j].getKey()) == 0)
				//if(cmp1.compareTo( cmp2) == 0)
				{
					//found desired key. Delete the value
					
					entries[j].deleteVal(val);
					//return entries[j].getEntryVal();
				}
			}
		}
		
		//current node is internal (height > 0)
		else
		{
			for (int j = 0; j < currentNode.entryCount(); j++)
			{
				//if (we are at the last key in this node OR the key we
				//are looking for is less than the next key, i.e. the
				//desired key must be in the subtree below the current entry),
				//then recurse into the current entry’s child
				if (j + 1 == currentNode.entryCount() || key.compareTo(entries[j + 1].getKey()) < 0)//second half want key<entries 
				{
					this.delete(entries[j].getChild(), key, val, height - 1);
				}
			}
		}
		
	}
	public Node getRoot()
	{
		return root;
	}
	
	
}
	

