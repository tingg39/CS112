package apps;

import structures.*;
import java.util.ArrayList;

public class MST 
{
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) 
	{
		PartialTreeList L = new PartialTreeList();
		int s = graph.vertices.length;
		
		for(int i = 0; i < s; i++)
		{
			Vertex v = graph.vertices[i];
			PartialTree T = new PartialTree(v);
			Vertex.Neighbor n = v.neighbors;
			MinHeap<PartialTree.Arc> P = T.getArcs();

			for(Vertex.Neighbor temp = n; temp != null; temp = temp.next)
			{
				PartialTree.Arc a = new PartialTree.Arc(v, temp.vertex, temp.weight);
				P.insert(a);
			}
			L.append(T);
		}
		
		return L;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) 
	{	
		ArrayList<PartialTree.Arc> result = new ArrayList<PartialTree.Arc>();
		while(ptlist.size() >= 2)
		{
			PartialTree PTX = ptlist.remove();
			MinHeap<PartialTree.Arc> PQX = PTX.getArcs();
			PartialTree.Arc a;
			Vertex v1 = null;
			Vertex v2 = null;
			while(true)
			{
				a = PQX.deleteMin();
				Vertex temp = PTX.getRoot();
				Vertex temp2 = a.v1;
				temp2 = temp2.getRoot();
				if(temp != temp2)
				{
					v1 = a.v2;
					v2 = a.v1;
					
				}
				else
				{
					v1 = a.v1;
					v2 = a.v2;
				}
				temp = PTX.getRoot();
				temp2 = v2;
				temp2 = temp2.getRoot();
				if(temp != temp2)
					break;
				else
					continue;
			}
			result.add(a);
			PartialTree PTY = ptlist.removeTreeContaining(v2);
			PTX.merge(PTY);
			ptlist.append(PTX);
		}
		return result;
		
	}
		
}
