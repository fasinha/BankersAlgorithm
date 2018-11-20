import java.util.*;

/*
 * This creates an instance of the optimistic resource manager
 * System can be deadlocked and deadlock is addressed according to the project specs
 * @author Flavia Sinha 
 * @version 11/18/2018
 */
public class Optimistic 
{
	int numoftasks; //number of tasks
	int numresources; //number of resources
	int[] resourcelist; //array with resources and units per resource. 
	//NOTE: 0th slot is empty because resource numbering starts at 1
	
	ArrayList<Task> running; //tasks that are neither terminated nor aborted 
	ArrayList<Task> blocked; //tasks that are currently blocked and waiting 
	ArrayList<Task> ok; //non-blocked tasks
	
	int[] justReleased; //units that have been released this cycle and will be added to resourcelist at end of cycle
	
	/*
	 * instantiates an instance of the optimistic resource manager 
	 */
	public Optimistic(int numoftasks, int numresources, int[] resourcelist)
	{
		this.numoftasks = numoftasks;
		this.numresources = numresources;
		this.resourcelist = resourcelist;
		running = new ArrayList<Task>();
		blocked = new ArrayList<Task>();
		ok = new ArrayList<Task>();
		justReleased = new int[numresources+1];
	}
	
	/*
	 * run the optimistic resource manager for a particular array list of tasks 
	 */
	public void run(ArrayList<Task> tasklist)
	{
		
		int currentcycle = 0; //initiate the current cycle
		
		//we add all the lists to the list of tasks that are neither terminated nor aborted 
		for (Task t : tasklist)
		{
			running.add(t);
		}
		
		//as long as there are still tasks that we can run, we keep looping 
		while (running.size() > 0)
		{
			//loop through all the elements in the running task list 
			for (int i = 0; i < running.size(); i++)
			{
				Task current = running.get(i);
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
						current.results[2] = (int) Math.round((((double) current.waiting / (double) (currentcycle) ) * 100)) + "%";
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
				System.out.println("Deadlock detected in cycle " +  currentcycle);
				//System.out.println();
				deadlockmethod(currentcycle); //resolve deadlock by aborting task with lowest ID 
			}
			
			//add the resources released in this cycle to the array of available resources
			for (int i = 1; i < resourcelist.length; i++)
			{
				resourcelist[i] += justReleased[i];
				justReleased[i] = 0;
				
			}
			
			running.clear();
			running.addAll(blocked);
			running.addAll(ok);
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
		int end = blocked.size() - 1;
		int num_abortions = 0;
		
		int abort_index = 0; 
		
		//loop through potential tasks
		while (num_abortions != end)
		{
			int minID = 100000;
			//loop through the blocked tasks and find the one with the lowest index 
			for (int j = 0; j < blocked.size(); j++)
			{
				Task b = blocked.get(j);
				if (b.getID() < minID)
				{
					minID = b.getID(); //this is the new lowest ID 
					//System.out.println("lowest id " + lowest);
					abort_index = j; //the index of the lowest ID
					//System.out.println("aborting task cycle " + currentcycle + " " + blocked.get(abort_index).getID());
				}
			}
			
			Task toabort = blocked.get(abort_index); //task that we will abort momentarily 
			blocked.remove(toabort); //remove the aborted task from the blocked list 
			toabort.abort(currentcycle); //abort the task 
			System.out.println("Deadlock: task " + toabort.getID() + " aborted in cycle " + currentcycle);
			//release the resources of the task and set the resources that the aborted task owns to be 0 
			for (int k = 1; k < numresources+1; k++)
			{
				justReleased[k] += toabort.resourcesOwn[k];
				toabort.resourcesOwn[k] = 0; 
			}
			num_abortions++;
			
		}
	}

	/*
	 * this method will request resources for a particular task 
	 */
	public void request(Task current, Action act) 
	{
		int resource = act.getB(); //get the resource 
		int amtrequested = act.getC(); //get the units of the resource
		int available = resourcelist[resource]; //get the amt of units available for this resource 
		
		{
			//if there are more units available than were requested, we can grant the request
			if (available >= amtrequested)
			{
				resourcelist[resource] -= amtrequested; //update the resource list 
				current.getUnits(resource, amtrequested); //update the task's resource list 
				current.currentindex += 1; //move on to the next activity 
				ok.add(current); //add the task to the nonblocked task list 
			}
			else {
				current.waiting++; //cannot grant request, so block the task and increment waiting time 
				blocked.add(current);
				
			}
		}
	}
	
	/*
	 * This method tries to release units of a resource for a particular task
	 */
	public void release(Task current, Action act)
	{
		int resource = act.getB(); //get resource number
		int amtreleased = act.getC(); //get number of units for release
		int currentown = current.resourcesOwn[resource]; //get the amount of units this resource owns 
		 
		{
			//if the task owns more than it is trying to release, release is okay
			if (currentown >= amtreleased)
			{
				justReleased[resource] += amtreleased; //update the just released array
				current.releaseUnits(resource, amtreleased); //update the task's resource arrays
				current.currentindex += 1; //move on to the next activity
				ok.add(current); //add the task to the nonblocked task list 
				
			}
		}
	}
}
