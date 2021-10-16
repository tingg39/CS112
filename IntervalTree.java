package structures;

import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = 
							getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) 
	{
		ArrayList<Interval> temp = new ArrayList<Interval>();
		
		if(lr == 'l')
		{
			while(!intervals.isEmpty())
			{
				int min = Integer.MAX_VALUE;
				for(int i = 0; i < intervals.size(); i++)
					if(intervals.get(i).leftEndPoint < min)
						min = intervals.get(i).leftEndPoint;
				for(int i = 0; i < intervals.size(); i++)
					if(intervals.get(i).leftEndPoint == min)
					{
						temp.add(intervals.remove(i));
						i--;
					}
			}
		}
		else if(lr == 'r')
		{
			while(!intervals.isEmpty())
			{
				int min = Integer.MAX_VALUE;
				for(int i = 0; i < intervals.size(); i++)
					if(intervals.get(i).rightEndPoint < min)
						min = intervals.get(i).rightEndPoint;
				for(int i = 0; i < intervals.size(); i++)
					if(intervals.get(i).rightEndPoint == min)
					{
						temp.add(intervals.remove(i));
						i--;
					}
			}
		}
		
		for(int i = 0; i < temp.size(); i++)
			intervals.add(temp.get(i));
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) 
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < leftSortedIntervals.size(); i++)
		{
			if(i == 0)
				result.add(leftSortedIntervals.get(i).leftEndPoint);
			else
			if(leftSortedIntervals.get(i).leftEndPoint != leftSortedIntervals.get(i-1).leftEndPoint)
				result.add(leftSortedIntervals.get(i).leftEndPoint);
		}
		int j = 0;
		for(int i = 0; i < result.size() && j < rightSortedIntervals.size();)
		{
			if(result.get(i) == rightSortedIntervals.get(j).rightEndPoint)
				j++;
			else if(result.get(i) < rightSortedIntervals.get(j).rightEndPoint)
				i++;
			else if(result.get(i) > rightSortedIntervals.get(j).rightEndPoint)
			{
				result.add(i,rightSortedIntervals.get(j).rightEndPoint);
				j++;
			}
		}
		for(; j < rightSortedIntervals.size(); j++)
		{
			if(rightSortedIntervals.get(j).rightEndPoint != rightSortedIntervals.get(j-1).rightEndPoint)
				result.add(rightSortedIntervals.get(j).rightEndPoint);
		}
		
		return result;
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		
		Queue<IntervalTreeNode> q = new Queue<IntervalTreeNode>();
		for(int i = 0; i < endPoints.size(); i++)
		{
			IntervalTreeNode tree = new IntervalTreeNode(endPoints.get(i), endPoints.get(i), endPoints.get(i));
			q.enqueue(tree);
		}
		
		int s = q.size();
		while(s > 1)
		{
			s = q.size;
			int temps = s;
			while(temps > 1)
			{
				IntervalTreeNode T1 = q.dequeue();
				IntervalTreeNode T2 = q.dequeue();
				float v1 = T1.maxSplitValue;
				float v2 = T2.minSplitValue;
				
				IntervalTreeNode N = new IntervalTreeNode((v1 + v2)/2, T1.minSplitValue, T2.maxSplitValue);
				
				//Create a new tree T with N as root, T1 as left child of N, and T2 as right child of N
				N.leftChild = T1;
				N.rightChild = T2;
				q.enqueue(N);
				temps = temps - 2;
			}
			if(temps == 1)
				q.enqueue(q.dequeue());
		}
		return q.dequeue();
	}
	/*Let s be the size of Q 
           if s == 1 then 
              T = dequeue Q
              T is the root of the interval tree
              go to step 7
           endif
           
	 * temps <- s
    while temps > 1 do
        T1 = dequeue Q
        T2 = dequeue Q
        Let v1 be the MAXIMUM split value of leaf nodes in T1
        Let v2 be the MINIMUM split value of leaf nodes in T2
        Create a new node N containing split value x, where x = (v1+v2)/2
        Create a new tree T with N as root, T1 as left child of N, and T2 as right child of N
        enqueue N into Q
        temps = temps - 2
    endwhile
    if temps == 1 do
       dequeue from Q and enqueue back into Q 
    endif*/  
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) 
	{
		IntervalTreeNode temp = root;
		for(int i = 0; i < leftSortedIntervals.size(); i++)
		{
			temp = root;
			while(temp.leftChild != null && temp.rightChild != null)
			{
				if(temp.splitValue >= leftSortedIntervals.get(i).leftEndPoint && temp.splitValue <= leftSortedIntervals.get(i).rightEndPoint)
				{
					if(temp.leftIntervals == null)
						temp.leftIntervals = new ArrayList<Interval>();
					temp.leftIntervals.add(leftSortedIntervals.get(i));
					break;
				}
				else if(temp.splitValue > leftSortedIntervals.get(i).leftEndPoint)
				{
					temp = temp.leftChild;
				}
				else if(temp.splitValue < leftSortedIntervals.get(i).leftEndPoint)
				{
					temp = temp.rightChild;
				}
			}
		}
		
		for(int i = 0; i < rightSortedIntervals.size(); i++)
		{
			temp = root;
			while(temp.leftChild != null && temp.rightChild != null)
			{
				if(temp.splitValue >= rightSortedIntervals.get(i).leftEndPoint && temp.splitValue <= rightSortedIntervals.get(i).rightEndPoint)
				{
					if(temp.rightIntervals == null)
						temp.rightIntervals = new ArrayList<Interval>();
					temp.rightIntervals.add(rightSortedIntervals.get(i));
					break;
				}
				else if(temp.splitValue > rightSortedIntervals.get(i).rightEndPoint)
				{
					temp = temp.leftChild;
				}
				else if(temp.splitValue < rightSortedIntervals.get(i).rightEndPoint)
				{
					temp = temp.rightChild;
				}
			}
		}
		//root = temp;
		/*
		 * Let the interval tree constructed in Step 6 be T
          for each interval [x,y] in Lsort do 
              starting at the root, 
                  search in the interval tree for the first (highest) node, N, whose split value is contained in [x,y]
              add [x,y] to the LEFT LIST of node N
          endfor
          for each interval [x,y] in Rsort do 
             starting at the root, 
                  search in the interval tree for the first (highest) node, N, whose split value is contained in [x,y]
             add [x,y] to the RIGHT LIST of node N
          endfor
		 */
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q)
	{
		ArrayList<Interval> ResultList = new ArrayList<Interval>();
		IntervalTreeNode R = root;
		float SplitVal = R.splitValue;
		ArrayList<Interval> LList = new ArrayList<Interval>();
		ArrayList<Interval> RList = new ArrayList<Interval>();
		LList = R.leftIntervals;
		RList = R.rightIntervals;
		IntervalTreeNode Lsub = R.leftChild;
		IntervalTreeNode Rsub = R.rightChild;
		if(Lsub == null && Rsub == null)
			return ResultList;
		
		if(q.contains(SplitVal))
		{
			for(int i = 0; i < LList.size(); i++)
			{
				ResultList.add(LList.get(i));
			}
			MLeft(Lsub, q, ResultList);
			MRight(Rsub, q, ResultList);
			//Lsub.matchLeft(q, ResultList);
			//Rsub.matchRight(q, ResultList);
			//Query Rsub and add the results to ResultList
            //Query Lsub and add the results to ResultList
		}
		else if(SplitVal < q.leftEndPoint)
		{
			int i = RList.size()-1;
			while(i >= 0 && RList.get(i).intersects(q))
			{
				ResultList.add(RList.get(i));
				i--;
			}
			MRight(Rsub, q, ResultList);
			//Rsub.matchRight(q, ResultList);
			//Query Rsub and add the results to ResultList
		}
		else if(SplitVal > q.rightEndPoint)
		{
			int i = 0;
			while(i < LList.size() && RList.get(i).intersects(q))
			{
				ResultList.add(LList.get(i));
				i++;
			}
			MLeft(Lsub, q, ResultList);
			//Lsub.matchLeft(q, ResultList);
			//Query Lsub and add the results to ResultList
		}
		
		return ResultList;
			/*
			 * 
			 * 
			 * 
	   Input: Interval tree T, query interval Iq
       Output: ResultList, a list of intervals from T that intersect Iq

       Let ResultList be empty.
       Let R be the root node of T
       Let SplitVal be the split value stored in R
       Let Llist be the list of intervals stored in R that is sorted by left endpoint
       Let Rlist be the list of intervals stored in R that is sorted by right endpoint
       Let Lsub be the left subtree of R
       Let Rsub be the right subtree of R

       1. If R is a leaf, return empty list.

       2. If SplitVal falls within Iq then
                   Add all intervals in Llist to ResultList
                   Query Rsub and add the results to ResultList
                   Query Lsub and add the results to ResultList
          else if SplitVal falls to the left of Iq then
                Let i be the size of Rlist
                while (i >= 0 and the i-th interval in Rlist intersects Iq)
                      Add the i-th interval to ResultList
                      i = i - 1
                endwhile
                Query Rsub and add the results to ResultList
          else if SplitVal falls to the right of Iq then
                Let i be 0
                while (i < the size of Llist and the i-th interval in Llist intersects Iq)
                      Add the ith interval to ResultList
                      i = i + 1
                endwhile
                Query Lsub and add the results to ResultList
          endif

       3. Return ResultList
	*/
	}
	
	private void MLeft(IntervalTreeNode r, Interval q, ArrayList<Interval> retval) {
		if (r.leftIntervals == null) { 
			return;
		}
		
		//int i=0;
		for(int i = 0; i < r.leftIntervals.size(); i++)
		{
			if(r.leftIntervals.get(i).intersects(q))
			{
				retval.add(r.leftIntervals.get(i));
			}
		}
		//while (i < r.leftIntervals.size() &&
				//(r.leftIntervals.get(i)).intersects(q)) {
		//	retval.add(r.leftIntervals.get(i));
		//	i++;
	}
	
	private void MRight(IntervalTreeNode r, Interval q, ArrayList<Interval> retval) {
		if (r.rightIntervals == null) { 
			return;
		}
		
		//int i=0;
		for(int i = 0; i < r.rightIntervals.size(); i++)
		{
			if(r.rightIntervals.get(i).intersects(q))
			{
				retval.add(r.rightIntervals.get(i));
			}
		}
		//while (i < r.leftIntervals.size() &&
				//(r.leftIntervals.get(i)).intersects(q)) {
		//	retval.add(r.leftIntervals.get(i));
		//	i++;
	}
}

