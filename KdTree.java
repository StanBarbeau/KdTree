package kdtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class KdTree<Point extends PointI>
{

	/** A node in the KdTree
	 */
	private class KdNode {
        KdNode child_left_, child_right_;
        Point pos_;
        int d_;    /// dimension in which the cut occurs

        KdNode(Point p, int d) {
            this.pos_ = p;
            this.d_ = d;
            this.child_left_ = null;
            this.child_right_ = null;
        }

        KdNode(Point p, int d, KdNode l_child, KdNode r_child) {
            this.pos_ = p;
            this.d_ = d;
            this.child_left_ = l_child;
            this.child_right_ = r_child;
        }

        /**
         * if strictly negative the query point is in the left tree
         * TODO: equality is problematic if we want a truly balanced tree
         */
        int dist1D(Point p) {
            return p.get(d_) - pos_.get(d_);
        }

        Point getPos_() {
            return this.pos_;
        }
    }
	/////////////////
    /// Attributs ///
    /////////////////

	private final int dim_; /// dimension of space
	private int n_points_; /// number of points in the KdTree

	private KdNode root_; /// root node of the KdTree

    //////////////////
    /// Constructor///
    //////////////////

	/** Initialize an empty kd-tree
	 */
	KdTree(int dim) {
		this.dim_ = dim;
		this.root_ = null;
		this.n_points_ = 0;
	}

	/** Initialize the kd-tree from the input point set
	 *  The input dimension should match the one of the points
     *  The max_depth int is the depth maximal of the tree, has root have depth = 0;
	 */
	KdTree(int dim, ArrayList<Point> points, int max_depth) {
		this.dim_ = dim;
		this.n_points_=0;

		this.medianeConstruct(points, dim, 0, max_depth);
	
	}
	  
	/////////////////
	/// Accessors ///
	/////////////////

	int dimension() { return dim_; }

	int nb_points() { return n_points_; }

	void getPointsFromLeaf(ArrayList<Point> points) {
		getPointsFromLeaf(root_, points);
	}

	 
	///////////////
	/// Mutator ///
	///////////////

	/** Insert a new point in the KdTree.
	 */
	void insert(Point p, int dim) {
		n_points_ += 1;
		
		if(root_==null) {
			root_ = new KdNode(p, 0);
		}
		else
		{
			KdNode node = getParent(p);
			if (node.dist1D(p) < 0) {
				assert (node.child_left_ == null);
				node.child_left_ = new KdNode(p, dim);
			} else {
				assert (node.child_right_ == null);
				node.child_right_ = new KdNode(p, dim);
			}
		}
	}


	void delete(Point p) {
		assert false;
	}

	///////////////////////
	/// Query Functions ///
	///////////////////////

	/** Return the node that would be the parent of p if it has to be inserted in the tree
	 */
	KdNode getParent(Point p) {
		assert(p!=null);
		
		KdNode next = root_, node = null;

		while (next != null) {
			node = next;
			if ( node.dist1D(p) < 0 ){
				next = node.child_left_;
			} else {
				next = node.child_right_;
			}
		}
		
		return node;
	}
	
	/** Check if p is a point registered in the tree
	 */
	boolean contains(Point p) {
        return contains(root_, p);
	}

	/** Get the nearest neighbor of point p
	 */
    public Point getNN(Point p)
    {
    	assert(root_!=null);
        return getNN(root_, p, root_.pos_);
    }

	///////////////////////
	/// Helper Function ///
	///////////////////////

    /** Add the points in the leaf nodes of the subrre defined by root 'node'
     * to the array 'point'
     */
	private void getPointsFromLeaf(KdNode node, ArrayList<Point> points)
	{
		if(node.child_left_==null && node.child_right_==null) {
			points.add(node.pos_);
		} else {
		    if(node.child_left_!=null)
		    	getPointsFromLeaf(node.child_left_, points);
		    if(node.child_right_!=null)
		    	getPointsFromLeaf(node.child_right_, points);
		}
	 }
	
	/** Search for a better solution than the candidate in the subtree with root 'node'
	 *  if no better solution is found, return candidate
	 */
	 private Point getNN(KdNode node, Point point, Point candidate)
	 {
	    if ( point.sqrDist(node.pos_) <  point.sqrDist(candidate)) 
	    	candidate = node.pos_;

	    int dist_1D = node.dist1D(point);
	    KdNode n1, n2;
	    if( dist_1D < 0 ) {
	    	n1 = node.child_left_;
	    	n2 = node.child_right_;
	    } else {
	    	// start by the right node
	    	n1 = node.child_right_;
	    	n2 = node.child_left_;
	    }

	    if(n1!=null)
	    	candidate = getNN(n1, point, candidate);

	    if(n2!=null && dist_1D*dist_1D < point.sqrDist(candidate)) 
	    	candidate = getNN(n2, point, candidate);
		 
		 return candidate;
	 }
	 
	private boolean contains(KdNode node, Point p) {
        if (node == null) return false;
        if (p.equals(node.pos_)) return true;

        //TODO : assume the "property" is strictly verified
        if (node.dist1D(p)<0)
            return contains(node.child_left_, p);
        else
            return contains(node.child_right_, p);
	}

	private void medianeConstruct(List<Point> array, int dims, int act_depth, int max_depth){

		int dirDecoupe = optimalDirCalc(array, dims);

        /*
         * thie first statement is execute when 'array' is empty ... and do nothing.
         */
        if (array.size() == 0){

        }

        /*
         * the next statement is exectute when depth overflow. The leaf is meanned with Points from 'array'
         */
		else if (act_depth > max_depth) {
            if (array.size() > 0 && !(this.contains(array.get(0)))){
                KdNode parent = this.getParent(array.get(0));

                array.add(parent.getPos_());

                int moys[] = new int[dims];
                for(int i = 0; i < dims; i++) {
                    for (int j = 0; j < array.size(); j++) {
                        moys[i] += array.get(j).get(i);
                    }
                    moys[i] /= array.size();
                }
                parent.pos_.set(moys);
            }
		}
		/*
		 * third statement is when depth is respected and only 1 point last in 'array'
		 */
		else if(array.size() == 1) {
			this.insert(array.get(0), dirDecoupe);
		}
		/*
		 * the final statement is execute when depth is respected and it rest at least 2 points to points to place
		 * in the Kdtree.
		 */
		else {



			Comparator<Point> pointComparator = new Comparator<Point>() {
				@Override
				public int compare(Point o1, Point o2) {
					return o2.get(dirDecoupe) - o1.get(dirDecoupe);
				}

			};
			array.sort(pointComparator);

			Point milieu = array.get(array.size()/2);
			this.insert(milieu,dirDecoupe);

			medianeConstruct( array.subList( 0, array.size() / 2 ), dims, act_depth + 1, max_depth);
			medianeConstruct( array.subList(array.size() / 2 + 1, array.size() ), dims, act_depth + 1, max_depth);
		}
	}

	/** optimalDirCalc
	 * calculate the optimal dimension to cut in in our Kdtree.
	 * @param list the list of points.
	 * @param dims the number of dimensions.
	 * @return the optimal direction.
	 */
	private int optimalDirCalc(List<Point> list, int dims)
	{
		int res = 0;
		double moys[] = new double[dims];
		double var[] = new double[dims];

		for (int i = 0 ; i < dims ; i++)
		{
			moys[i] = 0.;
			var[i] = 0.;
			for(int p = 0 ; p < list.size() ; p++)
			{
				moys[i] += list.get(p).get(i);
			}
			moys[i] /= list.size();

			for(int p = 0 ; p < list.size() ; p++)
			{
				var[i] += Math.pow(moys[i] - list.get(p).get(i),2);
			}
		}

		for( int i = 0 ; i < dims ; i++)
		{

			if (var[res] < var[i])
			{
				res = i;
			}
		}

		return res;
	}
	
}


