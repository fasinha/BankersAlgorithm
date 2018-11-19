import java.util.*;

/*
 * This creates an instance of a particular task.
 * @author Flavia Sinha
 * @version 11/18/2018
 */
public class Task 
{
	int id; //id number
	boolean aborted; //if the task is aborted or not
	ArrayList<Action> instructions; //list of actions relevant to task
	int total; //total time from initiate to finish
	int compute; //time for computing
	int currentindex; //th index in the list of instructions
	
	int[] maxclaim; //array of the maximum claims of the task
	int[] resourcesOwn; //array of the resources this task owns
	int[] resourcesNeed; //array of the resources this task needs
	boolean terminated; //if the task is done or not
	boolean canFinish; //whether this task can complete or not 
	
	int finish; //to set the finish time 
	int waiting; //counts the time spent blocked/waiting
	
	String[] results = new String[3]; //array that contains the three results we will print 
	
	/*
	 * creates an instance of Task
	 */
	public Task (int id, int numResources)
	{
		this.id = id;
		this.instructions = new ArrayList<Action>();
		resourcesOwn = new int[numResources+1];
		resourcesNeed = new int[numResources+1];
		maxclaim = new int[numResources+1];
		this.finish = 0; 
		this.currentindex = 0;
		this.waiting = 0;
		canFinish = false; 
	}
	
	/*
	 * creates an instance of Task
	 */
	public Task(int id, ArrayList<Action> instructions, int numResources)
	{
		this.id = id;
		this.instructions = instructions;
		resourcesOwn = new int[numResources+1];
		resourcesNeed = new int[numResources+1];
		maxclaim = new int[numResources+1];
		this.finish = 0; 
		this.currentindex = 0;
		this.waiting = 0;
		canFinish = false;
	}
	
	/*
	 * task receives units and updates the resourcesOwn and resourcesNeed array accordingly
	 */
	public void getUnits(int resource, int amt)
	{
		resourcesOwn[resource] += amt;
		resourcesNeed[resource] -= amt;
	}
	
	/*
	 * task releases units and updates the resourcesOwn and resourcesNeed array accordingly
	 */
	public void releaseUnits(int resource, int amt)
	{
		resourcesOwn[resource] -= amt;
		resourcesNeed[resource] += amt;
	}
	
	/*
	 * abort this task by setting the finish to the current cycle and changing aborted to true
	 */
	public void abort(int currentcycle)
	{
		this.aborted = true;
		this.finish = currentcycle;
	}
	
	/*
	 * terminate this task by setting terminated to true and setting the finish to the current cycle
	 */
	public void terminate(int currentcycle)
	{
		this.terminated = true;
		this.finish = currentcycle; 
	}
	
	/*
	 * returns this task's ID
	 */
	public int getID()
	{
		return this.id;
	}
	
	/*
	 * returns this task's list
	 */
	public ArrayList<Action> getList()
	{
		return this.instructions;
	}
	
	
	/*
	 * returns the current index of the list.
	 */
	public int getIndex()
	{
		return this.currentindex;
	}
	
	/*
	 * returns the compute time 
	 */
	public int getComputeTime()
	{
		return this.compute;
	}
	
	/*
	 * sets the compute time to parameter c
	 */
	public void setComputeTime(int c)
	{
		this.compute = c;
	}
	
	/*
	 * prints out the whole list of activities
	 */
	public void printList()
	{
		for (Action a : this.getList())
		{
			System.out.println(a.toString());
		}
	}
	
	/*
	 * prints the three results for this task
	 * total time, wait time, and percentage of time spent waiting
	 */
	public void printResults()
	{
		/*
		for (int i = 0; i < results.length; i++)
		{
			System.out.print(results[i] + "   ");
		} */
		System.out.format("%2s %2s   %3s", results[0], results[1], results[2]);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
