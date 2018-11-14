import java.util.*;

public class Banker 
{
	int numoftasks;
	int numresources;
	int[] resourcelist;
	
	ArrayList<Task> active;
	ArrayList<Task> blocked; 
	ArrayList<Task> ok; 
	
	int[] justReleased;
	
	public Banker(int numoftasks, int numresources, int[] resourcelist)
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
			for (int i = 0; i < active.size(); i++)
			{
				Task current = active.get(i);
				int index = current.getIndex(); 
				//System.out.println(index);
				Action act = current.getList().get(index);
				if (current.getComputeTime() == 0)
				{
					if ( act.getActivity().equals("initiate"))
					{
						System.out.println("initiated task " + current.getID());
						int r = act.getB(); //resource type 
						
						if(act.getC() > resourcelist[r])
						{
							current.abort(currentcycle);
						}
						else {
							current.resourcesNeed[r] = act.getC(); //set resources need to amount needed
							current.currentindex +=1; 
							ok.add(current);
						}
						
					}
					else if ( act.getActivity().equals("request"))
					{
						System.out.println("request task " + current.getID());
						request(current, act, currentcycle);
					}
					else if ( act.getActivity().equals("release"))
					{
						release(current, act);
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle);
						System.out.println("terminating curr cycle " + currentcycle);
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) (currentcycle) ) * 100) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{
						current.setComputeTime(act.getB());
						current.currentindex += 1;
						//current.incIndex();
						//act = current.getList().get(index);
						if (current.isDone() && current.getComputeTime() == 0)
						{
							current.terminate(currentcycle);
							System.out.println("terminating computing curr cycle " + currentcycle);
							current.results[0] = currentcycle + "";
							current.results[1] = current.waiting + "";
							current.results[2] = (int) (((double) current.waiting / (double) currentcycle ) * 100) + "%";
						}
					}
				}
				else {
					//the task is currently computing
					//System.out.println("hola");
					//current.setComputeTime(current.getComputeTime()-1);
					current.compute -=1;
					if (current.getComputeTime() == 0 && current.isDone())
					{
						current.terminate(currentcycle);
						current.finish = currentcycle;
						current.terminated = true;
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) current.finish ) * 100) + "%";
					}
				}
		
			}
			
			active.clear();
			active.addAll(blocked);
			active.addAll(ok);
			blocked.clear();
			ok.clear();
			
			currentcycle++;
		}
		
		
	}

	public void request(Task current, Action act, int currentcycle) 
	{
		int resource = act.getB();
		
		int amtrequested = act.getC();
		
		int available = resourcelist[resource];
		System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
		if (current.getComputeTime() > 0)
		{
			current.compute-=1;
			ok.add(current);
			//current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			if (amtrequested > current.resourcesNeed[resource])
			{
				current.abort(currentcycle);
			}
			else {
				if (available >= amtrequested && isSafeState(current, act))
				{
					resourcelist[resource] -= amtrequested;
					current.receive(resource, amtrequested);
					current.currentindex++;
					ok.add(current);
				}
				else if (available >= amtrequested && !isSafeState(current, act))
				{
					current.waiting++;
					blocked.add(current);
				}
				else {
					current.waiting++;
					blocked.add(current);
				}
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
		}
	}
	
	public boolean isSafeState(Task current, Action act) 
	{
		Task temp = current;
		ArrayList<Task> temptasklist = new ArrayList<Task>();
		for (Task t : active)
		{
			temptasklist.add(t);
		}
		
		int[] tempresourcelist = new int[numresources+1];
		
		for (int i = 0; i < tempresourcelist.length; i++)
		{
			tempresourcelist[i] = resourcelist[i];
		}
		
		//now simulate the request
		int r = act.getB();
		int amtrequest = act.getC();
		temp.resourcesNeed[r] -= amtrequest;
		temp.resourcesOwn[r] += amtrequest;
		
		while (temptasklist.size() > 0)
		{
			boolean potentiallysafe = false;
			for (int i = 0; i < temptasklist.size(); i++)
			{
				for (int j = 1; j < numresources+1; j++)
				{
					if (temptasklist.get(i).resourcesNeed[j] > tempresourcelist[j])
					{
						temptasklist.get(i).canFinish = false;
					}
				}
				
				for (int k = 0; k < temptasklist.size(); k++)
				{
					if (temptasklist.get(k).canFinish)
					{
						for (int m = 1; m < numresources+1; m++)
						{
							tempresourcelist[m] += temptasklist.get(k).resourcesOwn[m];
						}
						temptasklist.remove(k);
					}
					
				}
				
			}
			if (temptasklist.size() == 0) return true;
		}
		return false; 
		
	}
	
}
