import java.util.*;

public class BankerNew
{
	int numoftasks; //number of tasks
	int numresources; //number of resources
	int[] resourcelist; //array of units for each resource 
	
	ArrayList<Task> active; //list of actively working tasks
	ArrayList<Task> blocked; //list of blocked tasks
	ArrayList<Task> ok; //list of nonblocked tasks 
	
	int[] justReleased;
	
	public BankerNew(int numoftasks, int numresources, int[] resourcelist)
	{
		this.numoftasks = numoftasks;
		this.numresources = numresources;
		this.resourcelist = resourcelist;
		active = new ArrayList<Task>();
		blocked = new ArrayList<Task>();
		ok = new ArrayList<Task>();
		justReleased = new int[numresources+1];
		
	}
	
	public void run(ArrayList<Task> tasklist)
	{
		int currentcycle = 0;
		//Task current; 
		
		for (Task t : tasklist)
		{
			active.add(t);
		}
		
		while (active.size() > 0)
		{   
			//System.out.println("Begin CYCLE "+ currentcycle+"\n");
			for (int i = 0; i < active.size(); i++)
			{
				Task current = active.get(i); //get the i'th task
				int index = current.getIndex(); //get the current index of the activity list 
				Action act = current.getList().get(index); //get the current activity 
				//if the task is not currently computing 
				if (current.getComputeTime() == 0)
				{
					//go through the possible activities and execute necessary steps for each one 
					if ( act.getActivity().equals("initiate"))
					{
						//System.out.println("initiated task " + current.getID());
						int r = act.getB(); //resource type 
						
						//if the claim is greater than the amount of units we have for that resource
						//then we abort the task 
						if(act.getC() > resourcelist[r])
						{   
							current.abort(currentcycle);
						}
						else {
							//set the max claim and resources need arrays for this task 
							current.maxclaim[r] = act.getC();
							current.resourcesNeed[r] = act.getC(); //set resources need to amount needed
							current.currentindex +=1; //increment index to go to the next activity
							ok.add(current); //add the task to the list of okay tasks. 
						}
						
					}
					else if ( act.getActivity().equals("request"))
					{
						//System.out.println("request task " + current.getID());
						request(current, act, currentcycle); //call the banker request method
					}
					else if ( act.getActivity().equals("release"))
					{
						release(current, act); //call the release method
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle); //terminate the task at this cycle 
						//System.out.println("terminating curr cycle " + currentcycle);
						//set the tasks's result array with the finish time, waiting time and percent spent waiting
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) Math.round((((double) current.waiting / (double) (currentcycle) ) * 100)) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{
						//set the task's compute time 
						current.setComputeTime(act.getB());
						//decrement the task's compute time starting now 
						current.compute--;
						
						//if the task is done computing already then move on to the next activity
						if (current.getComputeTime() == 0)
						{
							current.currentindex += 1;
							
						}
						ok.add(current);
					}
				}
				else {
					//the task is currently in a computing phase 
					current.compute--;
					//if the computing is done, go to the next activity
					if (current.getComputeTime() == 0)
					{
						current.currentindex += 1;
						
					}
					ok.add(current);
				}
		
			}
			
			//clear the active array list and add first the blocked tasks and then the ok tasks 
			active.clear();
			active.addAll(blocked);
			active.addAll(ok);
			blocked.clear();
			ok.clear();
			currentcycle++;
		}
		
		
	}

	/*
	 * This method makes a request and checks to see if this results in a safe state or not 
	 * If the resulting state is safe then the request is granted. if not, then the request is not granted.
	 */
	public void request(Task current, Action act, int currentcycle) 
	{
		int resource = act.getB(); //resource requested
		int amtrequested = act.getC(); //amount of resource requested
		int available = resourcelist[resource]; //units of resource available 
		
		//if the task is computing then decrement the compute time
		if (current.getComputeTime() > 0)
		{
			current.compute-=1;
			ok.add(current);
			//current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			//if the amount requested is greater than the claim then we abort the task 
			if (amtrequested > current.resourcesNeed[resource])
			{   
				//release the resources owned by the task 
				for (int k = 1; k < numresources+1; k++)
				{
					resourcelist[k] += current.resourcesOwn[k];
					current.release(k, current.resourcesOwn[k]);
				}
				current.abort(currentcycle); //abort the task
				//System.out.println("aborted");
			}
			else {
				int r = act.getB();
				int amtrequest = act.getC();
				//grant the request so that then we can check the resulting safety of the state 
				current.resourcesNeed[r] -= amtrequest; 
				current.resourcesOwn[r] += amtrequest;
				resourcelist[resource] -= amtrequest;
				boolean safe = isSafeState(current, act); //check whether the resulting state is safe

				//if there are more units available than the amount requested and the state is safe then we proceed
				//move on to the next activity and add the task to the list of ok tasks
				if (available >= amtrequested && safe)
				{  
					//System.out.println("hi");
					current.currentindex++;
					ok.add(current);
				}
				// if the state is not safe, then we reverse the request that we had just granted 
				else if (available >= amtrequested && !safe)
				{  current.resourcesNeed[r] += amtrequest;
				   current.resourcesOwn[r] -= amtrequest;
				   resourcelist[resource] += amtrequest;
					//increment the task waiting time and add the task to the list of blocked tasks 
					current.waiting++;
					blocked.add(current);
				}
				else {
					current.resourcesNeed[r] += amtrequest;
					current.resourcesOwn[r] -= amtrequest;
					resourcelist[resource] += amtrequest;
					//System.out.println("what");
					current.waiting++;
					blocked.add(current);
				}
			}
		}
		
	}

	/*
	 * This method releases a task's resources by the amount specified by the task's activity. 
	 */
	public void release(Task current, Action act)
	{
		int resource = act.getB(); //get resource
		int amtreleased = act.getC(); //get the number of units releasing
		int currentown = current.resourcesOwn[resource]; //get the number of resources that the resource owns
		
		if(current.getComputeTime() > 0)
		{
			current.compute-=1;
			//current.setComputeTime(current.getComputeTime()-1);
			ok.add(current);
		}
		else {
			//if the task owns more or the same number of units that it is trying to release
			if (currentown >= amtreleased)
			{  
				resourcelist[resource] += amtreleased; //return the resources to the available resources
				current.release(resource, amtreleased); //release this task's resources
				current.currentindex += 1; //move on to the next activity
				ok.add(current); //add this task to the list of ok tasks
				//System.out.println(current.id+" RELEASED "+amtreleased);
				
			}
		}
	}
	
	/*
	 * This method checks whether the current state is safe
	 * returns true if the state is safe, and false otherwise 
	 */
	public boolean isSafeState(Task current, Action act) 
	{
		//create temporary task list which is a copy of the active tasks 
		ArrayList<Task> temptasklist = new ArrayList<Task>(); 
		for (Task t : active)
		{
			temptasklist.add(t); //copy tasks from active into temporary list 
		}
		
		//create an array with boolean values representing whether each task can complete or not 
		boolean[] finish = new boolean [temptasklist.size()];
		
		//create copy of the array of resources 
		int[] tempresourcelist = new int[numresources+1];
		
		for (int i = 0; i < tempresourcelist.length; i++)
		{
			//fil the array with the number of units for each resource 
			tempresourcelist[i] = resourcelist[i];
		}
		
		int count=0;
		//while we haven't looked through all the elements in temptasklist
		while (temptasklist.size() > count)
		{
			boolean potentiallysafe = false; //we do not know if we are in a safe state or not 
			//loop through the list of tasks 
			for (int i = 0; i < temptasklist.size(); i++)
			{   
				int j = 1;
				for ( j = 1; j < numresources+1; j++)
				{ 
					//if this particular task cannot be completed, then 
					if(finish[i] == false) 
					{
						//if the number of units of the resource needed are more than the units of the resource left
						//break out of the loop altogether 
						if (temptasklist.get(i).resourcesNeed[j] > tempresourcelist[j])
						{  
							break;
						}
					}
				}
				if(j == numresources+1 ) 
				{
					for (int m = 1; m < numresources+1; m++)
					{
						tempresourcelist[m] += temptasklist.get(i).resourcesOwn[m];
					}
					finish[i]=true;
					potentiallysafe=true;
					count++;
				}
				
				
			}
			if (potentiallysafe == false) return false;
		}
		return true; 
		
	}
	
}
