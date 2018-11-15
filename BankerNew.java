import java.util.*;

public class BankerNew
{
	int numoftasks;
	int numresources;
	int[] resourcelist;
	
	ArrayList<Task> active;
	ArrayList<Task> blocked; 
	ArrayList<Task> ok; 
	
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
		for (int i = 0; i < resourcelist.length; i++)
		{
			//System.out.print(resourcelist[i] + " ");
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
			//System.out.println("Begin CYCLE "+ currentcycle+"\n");
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
						
						if(act.getC() > resourcelist[r])
						{   
							current.abort(currentcycle);
						}
						else {
							current.maxclaim[r] = act.getC();
							current.resourcesNeed[r] = act.getC(); //set resources need to amount needed
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
						release(current, act);
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle);
						//System.out.println("terminating curr cycle " + currentcycle);
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) (currentcycle) ) * 100) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{
						current.setComputeTime(act.getB());
						//current.currentindex += 1;
						current.compute--;
						//current.incIndex();
						//act = current.getList().get(index);
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
		//System.out.println("amtreq " + amtrequested);
		
		int available = resourcelist[resource];
		//System.out.println("available " + available);
		//System.out.println("resourc);
		//System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
		if (current.getComputeTime() > 0)
		{
			current.compute-=1;
			ok.add(current);
			//current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			if (amtrequested > current.resourcesNeed[resource])
			{   
				for (int k = 1; k < numresources+1; k++)
				{
					resourcelist[k] += current.resourcesOwn[k];
					current.release(k, current.resourcesOwn[k]);
				}
				current.abort(currentcycle);
				//System.out.println("aborted");
			}
			else {
				int r = act.getB();
				int amtrequest = act.getC();
				current.resourcesNeed[r] -= amtrequest;
				current.resourcesOwn[r] += amtrequest;
				resourcelist[resource]-=amtrequest;
				boolean safe = isSafeState(current, act);
				//System.out.println("safe? " + safe);
				if (available >= amtrequested && safe)
				{  
					//System.out.println("hi");
					current.currentindex++;
					ok.add(current);
				}
				else if (available >= amtrequested && !safe)
				{  current.resourcesNeed[r] += amtrequest;
				   current.resourcesOwn[r] -= amtrequest;
				   resourcelist[resource]+=amtrequest;
					//System.out.println("not safe ");
					current.waiting++;
					blocked.add(current);
				}
				else {
					current.resourcesNeed[r] += amtrequest;
					current.resourcesOwn[r] -= amtrequest;
					resourcelist[resource]+=amtrequest;
					//System.out.println("what");
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
				resourcelist[resource] += amtreleased;
				//System.out.println(resourcelist[resource]+"DOG");
				current.release(resource, amtreleased);
				current.currentindex += 1;
				ok.add(current);
				//System.out.println(current.id+" RELEASED "+amtreleased);
				
			}
		}
	}
	
	public boolean isSafeState(Task current, Action act) 
	{
		//Task temp = current;
		ArrayList<Task> temptasklist = new ArrayList<Task>();
		for (Task t : active)
		{
			temptasklist.add(t);
		}
		boolean[] finish=new boolean [temptasklist.size()];
		
		int[] tempresourcelist = new int[numresources+1];
		
		for (int i = 0; i < tempresourcelist.length; i++)
		{
			tempresourcelist[i] = resourcelist[i];
		}
		
		int count=0;
		while (temptasklist.size() > count)
		{
			boolean potentiallysafe = false;
			for (int i = 0; i < temptasklist.size(); i++)
			{   int j = 1;
				for ( j = 1; j < numresources+1; j++)
				{ if(finish[i]==false) {
					if (temptasklist.get(i).resourcesNeed[j] > tempresourcelist[j])
					{  
						break;
					}
				 }
				}
				if(j==numresources+1) {
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
