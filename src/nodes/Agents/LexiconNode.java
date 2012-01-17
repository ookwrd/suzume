package nodes.Agents;

public class LexiconNode {

	public LexiconNode[] subnodes;
	public boolean occupied = false;
	
	
	public LexiconNode(int phonemes){
		subnodes = new LexiconNode[phonemes];
	}
	
	public void add(int[] word){
		add(word, 0);
	}
	
	public void add(int[] word, int position){
		if(position >= word.length){
			occupied = true;
			return;
		}
		
		int currentKey = word[position]; 
		if(subnodes[currentKey] == null){
			subnodes[currentKey] = new LexiconNode(subnodes.length);
		}
		
		subnodes[currentKey].add(word, position+1);
	}
	
	public boolean contains(int[] word){
		return distance(word) == -1;
	}
	
	public int distance(int[] word){
		return distance(word, 0);
	}
	
	public int distance(int[] word, int position){
		if(position >= word.length){
			if(occupied){
				return -1;
			} else {
				return 0;
			}
		}
		
		if(subnodes[word[position]] == null){
			return word.length - position;
		}else{
			return subnodes[word[position]].distance(word, position+1);
		}
	}
	
	public void print(){
		print("");
	}
	
	public void print(String base){
		if(occupied){
			System.out.println(base);
		}
		for(int pos = 0; pos < subnodes.length; pos++){
			if(subnodes[pos] != null){
				subnodes[pos].print(base+pos);
			}
		}
	}
	
	public static void main(String[] args){
		
		LexiconNode node = new LexiconNode(10);
		
		node.add(new int[]{1,5,6});
		node.add(new int[]{1,5,6,8});
		node.add(new int[]{1,5,4});
		node.add(new int[]{1,5,6,8});
		node.add(new int[]{1,2,4});
		node.add(new int[]{4});
		node.add(new int[]{0});
		
		node.print();
		
		System.out.println();

		System.out.println(node.distance(new int[]{1,5}));
		System.out.println(node.distance(new int[]{1,5,6}));
		System.out.println(node.distance(new int[]{1,5,7}));
		System.out.println(node.distance(new int[]{1,5,7,8}));
		
	}
}
