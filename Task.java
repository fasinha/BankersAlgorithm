import java.util.*;
public class Task {
	int id;
	boolean aborted;
	ArrayList<Action> instructions; 
	int total; 
	int compute;
	int currentindex;
	
	int[] resourcesOwn;
	int[] resourcesNeed;
	boolean terminated;
	
	int finish; 
	int waiting;
	
	public Task(int id, ArrayList<Action> instructions, int numResources)
	{
		this.id = id;
		this.instructions = instructions;
		resourcesOwn = new int[numResources+1];
		resourcesNeed = new int[numResources+1];
		this.finish = 0; 
		this.currentindex = 0;
		this.waiting = 0;
	}
	
	public void receive(int resource, int amt)
	{
		resourcesOwn[resource] += amt;
		resourcesNeed[resource] -= amt;
	}
	
	public void release(int resource, int amt)
	{
		resourcesOwn[resource] -= amt;
		resourcesNeed[resource] += amt;
	}
	
	public void abort(int currentcycle)
	{
		this.aborted = true;
		this.finish = currentcycle;
	}
	
	public void terminate(int currentcycle)
	{
		this.terminated = true;
		this.finish = currentcycle; 
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public ArrayList<Action> getList()
	{
		return this.instructions;
	}
	
	public void incIndex()
	{
		this.currentindex++;
	}
	
	public int getIndex()
	{
		return this.currentindex;
	}
	
	public boolean isDone()
	{
		if (instructions.get(currentindex).getActivity().equals("terminate"))
		{
			return true;
		}
		else return false;
	}
	
	public int getComputeTime()
	{
		return this.compute;
	}
	
	public void setComputeTime(int c)
	{
		this.compute = c;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
