package tools.graphs;
class ClusterNode {
	
	String name;
	int id; // good coding practice would have this as private
	
	public ClusterNode(int id, String name) {
		this.name = name;
		this.id = id;
	}

	public String toString() { // Always a good idea for debuging
		//return "V" + id; // JUNG2 makes good use of these.
		return name;

	}
}