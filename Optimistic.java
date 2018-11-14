import java.util.*;

public class Optimistic 
{
	int numoftasks;
	int numresources;
	int[] resourcelist;
	
	ArrayList<Task> active;
	ArrayList<Task> blocked; 
	ArrayList<Task> ok; 
	
	int[] justReleased;
	
	/*
	 * instantiates an instance of the optimistic resource manager 
	 */
	public Optimistic(int numoftasks, int numresources, int[] resourcelist)
	{
		this.numoftasks = numoftasks;
		this.numresources = numresources;
		this.resourcelist = resourcelist;
		active = new ArrayList<Task>();
		blocked = new ArrayList<Task>();
		ok = new ArrayList<Task>();
		justReleased = new int[numresources+1];
	}
	
	/*
	 * run the optimistic resource manager for a particular array list of tasks 
	 */
	public void run(ArrayList<Task> tasklist)
	{
		
		int currentcycle = 0;
		
		//we add all the lists to a list representing active tasks
		for (Task t : tasklist)
		{
			active.add(t);
		}
		
		
		while (active.size() > 0)
		{
			//loop through all the elements in the active task list 
			for (int i = 0; i < active.size(); i++)
			{
				Task current = active.get(i);
				int index = current.getIndex(); //get the index we are currently at in the list of activities for the task 
				
				Action act = current.getList().get(index); //get the current activity
				//while this task is not computing right now 
				if (current.getComputeTime() == 0)
				{
					if ( act.getActivity().equals("initiate"))
					{			
						//the optimistic manager does not make any claims so we just go to the next activity 
						current.currentindex +=1; 
						ok.add(current);
					}
					else if ( act.getActivity().equals("request"))
					{
						request(current, act);
					}
					else if ( act.getActivity().equals("release"))
					{
						release(current, act);
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle);
						
						//add the total and waiting times for this task to its result array
						current.results[0] = (currentcycle) + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) (currentcycle) ) * 100) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{	
						int comp = act.getB(); //get the compute time from the activity string 
						current.setComputeTime(comp); //set the compute time for the task
						current.compute--; //decrement the compute time 
					
						//if we are done computing, move to the next activity
						if (current.getComputeTime() == 0)
						{
							current.currentindex += 1;
							
						}
						ok.add(current);
					}
				}
				else {
					//the task is currently computing
					
					current.compute--;
					//if the computing is done, go to the next activity
					if (current.getComputeTime() == 0)
					{
						current.currentindex += 1;
						
					}
					ok.add(current);
				}
		
			}
			
			//if there are no ok tasks and there is at least one task in the blocked list then we have deadlock 
			if (blocked.size() > 0 && ok.size() == 0)
			{
				deadlockmethod(currentcycle);
			}
			
			//add the resources released in this cycle to the array of available resources
			for (int i = 1; i < resourcelist.length; i++)
			{
				resourcelist[i] += justReleased[i];
				justReleased[i] = 0;
				
			}
			
			active.clear();
			active.addAll(blocked);
			active.addAll(ok);
			blocked.clear();
			ok.clear();
			
			currentcycle++;
			
		}		
	}

	/*
	 * This method resolves deadlock by aborting the task with the lowest index 
	 */
	public void deadlockmethod(int currentcycle) 
	{
		int aborted = blocked.size() -1;
		int lowest = 500;
		int abort_index = 0; 
		
		//loop through potential tasks
		for (int i = 0; i < aborted; i++)
		{
			//loop through the blocked tasks and find the one with the lowest index 
			for (int j = 0; j < blocked.size(); j++)
			{
				Task b = blocked.get(j);
				if (b.getID() < lowest)
				{
					lowest = b.getID();
					abort_index = j;
				}
			}
			
			Task toabort = blocked.get(abort_index);
			toabort.abort(currentcycle);
			for (int k = 1; k < numresources+1; k++)
			{
				justReleased[k] += toabort.resourcesOwn[k];
			}
			//System.out.println(toabort.getID() + " ABORTED");
			blocked.remove(toabort);
			//break;
		}
	}

	public void request(Task current, Action act) 
	{
		int resource = act.getB();
		//System.out.println("resource " + resource);
		//System.out.println(resourcelist.length);
		int amtrequested = act.getC();
		//System.out.println(Arrays.toString(resourcelist));
		//System.exit(0);
		int available = resourcelist[resource];
		//System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
		//System.out.println("available = " + available);
		
		if (current.getComputeTime() > 0)
		{
			current.compute-=1;
			ok.add(current);
			//current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			if (available >= amtrequested)
			{
				
				resourcelist[resource] -= amtrequested;
				current.receive(resource, amtrequested);
				current.currentindex += 1;
				ok.add(current);
				//System.out.println("Task " + current.getID() + " request granted");
			}
			else {
				current.waiting++;
				blocked.add(current);
				//System.out.println("task " + current.getID() + " request cannot be granted");
			}
		}
	}
	
	public void release(Task current, Action act)
	{
		int resource = act.getB();
		int amtreleased = act.getC();
		int currentown = current.resourcesOwn[resource];
		
		if(current.getComputeTime() > 0)
		{
			current.compute-=1;
			//current.setComputeTime(current.getComputeTime()-1);
			ok.add(current);
		}
		else {
			if (currentown >= amtreleased)
			{
				justReleased[resource] += amtreleased;
				current.release(resource, amtreleased);
				current.currentindex += 1;
				ok.add(current);
				
			}
			/*
			else {
				current.waiting++;
				blocked.add(current);
			} */
		}
	}
}
