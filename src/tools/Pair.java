package tools;

/**
 * A standard implementation of a comparable pair class.
 * 
 * @author Luke Mccrohon
 *
 * @param <A>
 * @param <B>
 */
public  class Pair<A extends Comparable<A>,B> implements Comparable<Pair<A, B>> {

	public A first;
	public B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString(){
		return ""+first + " " +second;
	}

	@Override
	public int compareTo(Pair<A, B> pair){
		return first.compareTo(pair.first);
	}

}