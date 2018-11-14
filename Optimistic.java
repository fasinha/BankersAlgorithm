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
	
	public void run(ArrayList<Task> tasklist)
	{
		//System.out.println(justReleased.length);
		int currentcycle = 0;
		
		for (Task t : tasklist)
		{
			active.add(t);
		}
		
		//System.out.println(active.size());
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
						
						current.currentindex +=1; 
						ok.add(current);
					}
					else if ( act.getActivity().equals("request"))
					{
						System.out.println("request task " + current.getID());
						request(current, act);
					}
					else if ( act.getActivity().equals("release"))
					{
						release(current, act);
					}
					else if ( act.getActivity().equals("terminate"))
					{
						current.terminate(currentcycle);
						System.out.println("terminating curr cycle " + currentcycle);
						current.results[0] = (currentcycle) + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) (currentcycle) ) * 100) + "%";
					} 
					else if ( act.getActivity().equals("compute"))
					{	
						int comp = act.getB();
						current.setComputeTime(comp);
						current.compute--;
						//current.currentindex += 1;
						
						//current.incIndex();
						//act = current.getList().get(index);
						if (current.getComputeTime() == 0)
						{
							current.currentindex += 1;
							
						}
						ok.add(current);
						/*
						if (current.isDone() && current.getComputeTime() == 0)
						{
							current.terminate(currentcycle);
							System.out.println("terminating computing curr cycle " + currentcycle);
							current.results[0] = currentcycle + "";
							current.results[1] = current.waiting + "";
							current.results[2] = (int) (((double) current.waiting / (double) currentcycle ) * 100) + "%";
						} */
					}
				}
				else {
					//the task is currently computing
					//System.out.println("hola");
					//current.setComputeTime(current.getComputeTime()-1);
					current.compute--;
					if (current.getComputeTime() == 0)
					{
						current.currentindex += 1;
						
					}
					ok.add(current);
					/*
					if (current.getComputeTime() == 0 && current.isDone())
					{
						current.terminate(currentcycle);
						current.finish = currentcycle;
						current.terminated = true;
						current.results[0] = currentcycle + "";
						current.results[1] = current.waiting + "";
						current.results[2] = (int) (((double) current.waiting / (double) current.finish ) * 100) + "%";
					} */
				}
		
			}
			
			//System.out.println("reached ");
			if (blocked.size() > 0 && ok.size() == 0)
			{
				System.out.println("deadlock reached here");
				deadlockmethod(currentcycle);
				//System.exit(0);
				//break;
			}
			
			//System.out.println(resourcelist.length);
			//System.out.println(justReleased.length);
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
			System.out.println(active.size());
			currentcycle++;
			System.out.println("current cycle " + currentcycle);
			//System.out.println("reached end");
		}		
	}

	public void deadlockmethod(int currentcycle) 
	{
		int aborted = blocked.size() -1;
		int lowest = 500;
		int abort_index = 0; 
		for (int i = 0; i < aborted; i++)
		{
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
			System.out.println(toabort.getID() + " ABORTED");
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
		System.out.println("amt requested by task " + current.getID() + " " + amtrequested);
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
				System.out.println("Task " + current.getID() + " request granted");
			}
			else {
				current.waiting++;
				blocked.add(current);
				System.out.println("task " + current.getID() + " request cannot be granted");
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
