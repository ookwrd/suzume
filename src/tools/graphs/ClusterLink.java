package tools.graphs;
class ClusterLink {
	String weight; // should be private for good practice
	int id;

	public ClusterLink(String weight) {
		//this.id = edgeCount++; // This is defined in the outer class.
		this.weight = weight;
	}

	public String toString() { // Always good for debugging
		//return "E" + id;
		return this.weight;
	}

	public String getWeight() {
		return weight;
	}
}