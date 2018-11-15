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
		for (int i = 0; i < resourcelist.length; i++)
		{
			System.out.print(resourcelist[i] + " ");
		}
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
						//System.out.println("initiated task " + current.getID());
						int r = act.getB(); //resource type 
						int maxclaim = act.getC();
						if(maxclaim > resourcelist[r])
						{
							System.out.println("initial claim exceeds amount of units for resource");
							current.abort(currentcycle);
							for (int k = 1; k < numresources+1; k++)
							{
								
								resourcelist[k] += current.resourcesOwn[k];
								current.release(k, current.resourcesOwn[k]);
							}
						}
						else {
							current.resourcesNeed[r] = act.getC(); //set resources need to amount needed
							current.maxclaim[r] = act.getC();
							current.currentindex +=1; 
							ok.add(current);
						}
						
					}
					else if ( act.getActivity().equals("request"))
					{
						//System.out.println("request task " + current.getID());
						request(current, act, currentcycle);
					}
					else if ( act.getActivity().equals("release"))
					{
						System.out.println("releasing");
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
			//System.out.println("Done here");
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
		System.out.println(act.toString());
		int resource = act.getB();
		
		int amtrequested = act.getC();
		System.out.println("amtreq " + amtrequested);
		
		int available = resourcelist[resource];
		System.out.println("available " + available);
		//System.out.println("resourc);
		System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
		if (current.getComputeTime() > 0)
		{
			current.compute-=1;
			ok.add(current);
			//current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			if (amtrequested > current.maxclaim[resource])
			{
				current.abort(currentcycle); //abort the task
				//release the task's resources
				for (int k = 1; k < numresources+1; k++)
				{
					resourcelist[k] += current.resourcesOwn[k];
					current.release(k, current.resourcesOwn[k]);
				}
				System.out.println("Task " + current.getID() + " aborted. Amt requested exceeded max claim");
			}
			else {
				System.out.println("reached here ihjjgh");
				boolean safe = isSafeState(current, act);
				System.out.println("safe? " + safe);
				if (available >= amtrequested && safe)
				{
					System.out.println("hi");
					resourcelist[resource] -= amtrequested;
					current.receive(resource, amtrequested);
					current.currentindex+=1;
					ok.add(current);
				}
				else if (available >= amtrequested && (safe == false))
				{
					System.out.println("not safe ");
					current.waiting++;
					blocked.add(current);
				}
				else {
					System.out.println("what");
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
		
		for (int i = 1; i < numresources+1; i++)
		{
			tempresourcelist[i] = resourcelist[i];
		}
		
		//now simulate the request
		int r = act.getB();
		int amtrequest = act.getC();
		temp.resourcesNeed[r] -= amtrequest;
		temp.resourcesOwn[r] += amtrequest;
		
		boolean potentiallysafe = true;
		while (potentiallysafe)
		{
			potentiallysafe = false;
			System.out.println(act.toString());
			//boolean potentiallysafe = false;
			for (int i = 0; i < temptasklist.size(); i++)
			{
				//
				temptasklist.get(i).canFinish = true;
				for (int j = 1; j < numresources+1; j++)
				{
					System.out.println("reached here");
					if (temptasklist.get(i).resourcesNeed[j] > tempresourcelist[j])
					{
						temptasklist.get(i).canFinish = false;
						System.out.println("set can finish to false");
					}
				}
				
				for (int k = 0; k < temptasklist.size(); k++)
				{
					if (temptasklist.get(k).canFinish == true)
					{
						for (int m = 1; m < numresources+1; m++)
						{
							tempresourcelist[m] += temptasklist.get(k).resourcesOwn[m];
							temptasklist.get(k).release(k, temptasklist.get(k).resourcesOwn[m]);
						}
						temptasklist.remove(k);
						System.out.println("size " + temptasklist.size());
					}
					
				}
				
			}
			if (temptasklist.size() == 0) return true;
		}
		return false; 
		
	}
	
}
