package kdtree;

import static org.junit.Assert.*;

import java.awt.*;
import java.util.ArrayList;
import org.junit.Test;

public class KdTreeTest {

	ArrayList<Point2i> grid() {
		ArrayList<Point2i> v_tree_points = new ArrayList<Point2i>();
		for(int i=0; i<10; ++i) {
			for(int j=0; j<10; ++j) {
				v_tree_points.add(new Point2i(10*i,10*j));
			}
		}
		return v_tree_points;
	}
	
	@Test
	public void testNearestNeighbor() {

		// Init vector of points

		ArrayList<Point2i> v_tree_points = grid();
		
		// Create kd-tree
		
		KdTree<Point2i> tree = new KdTree<Point2i>(2, v_tree_points,Integer.MAX_VALUE);
		
		// Create a vector of query Point
		ArrayList<Point2i> v_query_points = new ArrayList<Point2i>();
		v_query_points.add(new Point2i(0,0));
		v_query_points.add(new Point2i(100,100));
		v_query_points.add(new Point2i(200,200));
		v_query_points.add(new Point2i(25,150));
		v_query_points.add(new Point2i(25,55));
		v_query_points.add(new Point2i(33,25));
		
		// Compare result of linear search with kdtree search

		for (Point2i p : v_query_points) {

			//TODO: move that in linear search class
			float l_min = Float.MAX_VALUE;
	        for (Point2i pi: v_tree_points) {
	        	float sqr_dist = pi.sqrDist(p);
	        	if(sqr_dist < l_min) {
	        		l_min = sqr_dist;
	        	}
	        }	        	
	        
	        Point2i np = tree.getNN(p);
	        float t_min = p.sqrDist(np);
		    assertTrue(t_min==l_min);
		}
	}

	@Test
	public void testInsert() {
		// Init vector of points

		Point2i insPoint = new Point2i(100,100);
		ArrayList<Point2i> v_tree_points = grid();
		ArrayList<Point2i> arbre2 = (ArrayList<Point2i>) v_tree_points.clone();
		arbre2.add(insPoint);

		// Create kd-tree

		KdTree<Point2i> tree = new KdTree<Point2i>(2, v_tree_points,Integer.MAX_VALUE);
		KdTree<Point2i> tree2 = new KdTree<>(2, arbre2, Integer.MAX_VALUE);

		tree.insert(insPoint);

		assertEquals("Les deux arbres sont différents",tree, tree2);
	}

	@Test
	public void testDelete() {
		// Init vector of points

		Point2i insPoint = new Point2i(100,100);
		ArrayList<Point2i> v_tree_points = grid();
		ArrayList<Point2i> arbre2 = (ArrayList<Point2i>) v_tree_points.clone();
		arbre2.add(insPoint);

		// Create kd-tree

		KdTree<Point2i> tree = new KdTree<Point2i>(2, v_tree_points,Integer.MAX_VALUE);
		KdTree<Point2i> tree2 = new KdTree<>(2, arbre2, Integer.MAX_VALUE);

		tree2.delete(insPoint);

		assertEquals("Les deux arbres sont différents",tree, tree2);
	}

	@Test
    public void testKdtree() {

        KdTree<Point2i> arbre = new KdTree<Point2i>(1);
        Point2i rootPoint = new Point2i(0,0);

        assertTrue("L'arbre n'est pas vide", arbre.nb_points() == 0);
        assertTrue("L'arbre n'est pas de dimension 1", arbre.dimension() == 1);

        arbre.insert(rootPoint);
        assertTrue("Le point n'est pas compris dans l'arbre",arbre.contains(rootPoint));
        assertEquals("Le point ROOT n'est pas la racine de l'arbre.",rootPoint, arbre.getNN(rootPoint));
    }
	
}
