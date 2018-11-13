import java.util.*;
public class Optimistic {

	int numoftasks;
	int numresources;
	int[] resourcelist;
	
	ArrayList<Task> active;
	ArrayList<Task> blocked; 
	ArrayList<Task> ok; 
	
	int[] justReleased = new int[numresources];
	
	public Optimistic(int numoftasks, int numresources, int[] resourcelist)
	{
		this.numoftasks = numoftasks;
		this.numresources = numresources;
		this.resourcelist = resourcelist;
	}
	
	public void run(ArrayList<Task> tasklist)
	{
		int currentcycle = 0;
		
		for (Task t : tasklist)
		{
			active.add(t);
		}
		
		while (true)
		{
			for (int i = 0; i < active.size(); i++)
			{
				Task current = active.get(i);
				int index = current.getIndex();
				Action act = current.getList().get(index);
				if ( act.getActivity().equals("initiate"))
				{
					current.incIndex();
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
				}
				else if ( act.getActivity().equals("compute"))
				{
					current.setComputeTime(act.getB());
					current.incIndex();
					act = current.getList().get(index);
					if (current.isDone() && current.getComputeTime() == 0)
					{
						current.terminate(currentcycle);
					}
				}
			}
			
			if (blocked.size() > 0 && ok.size() == 0)
			{
				deadlockmethod(currentcycle);
			}
			
			for (int i = 0; i < numresources; i++)
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
			for (int k = 0; k < numresources; k++)
			{
				justReleased[k] += blocked.get(k).resourcesOwn[k];
			}
			blocked.remove(toabort);
			break;
		}
	}

	public void request(Task current, Action act) 
	{
		int resource = act.getA();
		int amtrequested = act.getB();
		int available = resourcelist[resource];
		
		if (current.getComputeTime() > 0)
		{
			current.setComputeTime(current.getComputeTime()-1);
		}
		else {
			if (available >= amtrequested)
			{
				resourcelist[resource] -= amtrequested;
				current.receive(resource, amtrequested);
				current.incIndex();
				ok.add(current);
			}
			else {
				current.waiting++;
				blocked.add(current);
			}
		}
	}
	
	public void release(Task current, Action act)
	{
		int resource = act.getA();
		int amtreleased = act.getB();
		int currentown = current.resourcesOwn[resource];
		
		if (current.getComputeTime() > 0)
		{
			current.setComputeTime(current.getComputeTime()-1);
			ok.add(current);
		}
		else {
			if (currentown >= amtreleased)
			{
				justReleased[resource] += amtreleased;
				current.release(resource, amtreleased);
				current.incIndex();
				ok.add(current);
			}
			else {
				current.waiting++;
				blocked.add(current);
			}
		}
	}
}
