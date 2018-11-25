import java.io.FileNotFoundException;
import java.util.*;

public class SinhaPaging 
{
	public static void main(String[] args) throws FileNotFoundException
	{
		int machine = 0; 
		int pagesize = 0; 
		int process_size = 0; 
		int job = 0; 
		int number_refs = 0;
		int numOfPages = 0;
		int numOfFrames= 0;
		String algo = "";
		if (args[0] == null)
		{
			System.exit(0);
		}
		else {
			machine = Integer.parseInt(args[0]);
			pagesize = Integer.parseInt(args[1]);
			process_size = Integer.parseInt(args[2]);
			job = Integer.parseInt(args[3]);
			number_refs = Integer.parseInt(args[4]);
			algo = args[5];
			numOfPages = process_size / pagesize;
			numOfFrames = machine / pagesize;
		}
		
		ArrayList<SinhaProcess> processes = new ArrayList<SinhaProcess>();
		if (job == 1)
		{
			SinhaProcess p1 = new SinhaProcess(1, 1, 0, 0, process_size, numOfPages);
			p1.fillPageTable();
			processes.add(p1);
		}
		else if (job == 2)
		{
			for (int i = 0; i < 4; i++)
			{
				SinhaProcess p2 = new SinhaProcess(i+1, 1, 0, 0, process_size, numOfPages);
				p2.fillPageTable();
				processes.add(p2);
			}
		}
		else if (job == 3)
		{
			SinhaProcess p3 = new SinhaProcess(1, 0, 0, 0, process_size, numOfPages);
			p3.fillPageTable();
			processes.add(p3);
			
		}
		else if (job == 4)
		{
			SinhaProcess p4 = new SinhaProcess(1, .75, .25, 0, process_size, numOfPages);
			SinhaProcess p5 = new SinhaProcess(2, .75, 0, .25, process_size, numOfPages);
			SinhaProcess p6 = new SinhaProcess(3, .75, .125, .125, process_size, numOfPages);
			SinhaProcess p7 = new SinhaProcess(4, .5, .125, .125, process_size, numOfPages);
			processes.add(p4);
			processes.add(p5);
			processes.add(p6);
			processes.add(p7);
			for (SinhaProcess process : processes)
			{
				process.fillPageTable();
			}
		}
		
		SinhaFrameTable frametable = new SinhaFrameTable(numOfFrames);
		roundRobin(processes, frametable, algo);
		
		System.out.println("The machine size is " + machine);
		System.out.println("The page size is " + pagesize);
		System.out.println("The process size is " + process_size);
		System.out.println("The job mix number is " + job);
		System.out.println("The number of references per process is " + number_refs);
		System.out.println("The replacement algorithm is " + algo);
		System.out.println("The level of debugging output is " + args[6]);
		System.out.println();
		for (SinhaProcess p : processes)
		{
			System.out.println("Process " + p.getID() + " had " + p.faults + " faults.");
	
		}
		
	}
	
	public static void roundRobin(ArrayList<SinhaProcess> processes, SinhaFrameTable frametable, String algo) throws FileNotFoundException
	{
		ArrayList<SinhaProcess> temp = new ArrayList<SinhaProcess>();
		for (SinhaProcess p : processes)
		{
			temp.add(p);
		}
		int quantum = 3; 
		int currentcycle = 1;
		SinhaProcess current; 
		while (temp.size() > 0)
		{
			for (int i = 0; i < temp.size(); i++)
			{
				current = temp.get(i);
				for (int ref = 0; ref < quantum; ref++)
				{
					current.nextWordToRef();
					
					if (current.currentpage.getFrameFromPage() == -1) //page fault
					{
						current.faults++;
						frametable.setLargestFreeFrame();
						if (frametable.getLargestFreeFrame() != -1) //if there is still a free frame 
						{
							int frametoload = frametable.getLargestFreeFrame();
							frametable.ft[frametoload] = current.currentpage;
							current.currentpage.setFrame(frametoload);
							frametable.setLargestFreeFrame();
						}
						else {
							//we must do the page replacement algorithm 
							if (algo.equals("lru"))
							{
								frametable.lru_replace();
							}
							else if (algo.equals("lifo"))
							{
								frametable.lifo_replace();
							}
							else if (algo.equals("random"))
							{
								frametable.random_replace();
							}
						}
						current.reference++;
					}
					else {
						//we have a hit 
						current.reference++;
					}
					currentcycle++;
					current.nextWordToRef();
					
					
				}
				temp.remove(current);
			}
			//temp.remove(current);
		}
	}
}
