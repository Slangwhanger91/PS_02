package Modified_files;

/**
 * this class represents a simple ORDERD set of vertices. used by Cliques
 * The code was written in C like flavor - no java.util, no abstraction  
 * @author Boaz
 *
 */
public class VertexSet {
	//public static int[] count = new int[70]; 
			
	private int[] _set;
	private int _sp;
	public final static int INIT_SIZE = 10, INC = 20;//MODIFIED (from 20 and 50)

	public VertexSet() {
		//count[0]++;//delete
		_set = new int[INIT_SIZE];
		_sp = 0;
	}

	public VertexSet(VertexSet ot) {
		//count[0]++;//delete
		_set = new int[INIT_SIZE];
		_sp = 0;
		for(int i = 0; i < ot._sp; ++i) this.add(ot.at(i));//_sp
	}

	public void add(int a) {
		if(_sp == _set.length) resize();
		_set[_sp] = a;
		//--count[_sp];//delete
		++_sp;
		//++count[_sp];//delete
	}
	/*public void print_shit(){//DELETE
		for (int i = 0; i < count.length; i++)
			System.out.print(i+":"+count[i] + ", ");
		System.out.println();
	}*/
	public int size() {return _sp;}
	public int at(int i) {return _set[i];}

	public String toString() {//MODIFIED
		StringBuilder sb = new StringBuilder();

		sb.append("Set: |");
		sb.append(size());
		sb.append("| ");
		for(int i = 0; i < size(); ++i){
			sb.append(this.at(i));
			sb.append(", ");
		}

		return sb.toString();

		/*String ans = "Set: |" + size() + "| ";
		for(int i = 0; i < size(); ++i) ans+=this.at(i) + ", ";
		return ans;*/
	}

	public String toFile() {//MODIFIED
		StringBuilder sb = new StringBuilder();

		sb.append(" ");
		for(int i = 0; i < size(); ++i){
			sb.append(this.at(i));
			sb.append(", ");
		}

		return sb.toString();

		/*String ans = " ";
		for(int i = 0; i < size(); ++i){
			ans += this.at(i) + ", ";
		}
		return ans;*/
	}
	/**
	 * this method computes the intersection between this set and ot set.
	 * @param ot - the other set
	 */
	public VertexSet intersection(VertexSet ot) {
		VertexSet ans = new VertexSet();
		int ot_set[] = ot._set;//MODIFIED direct ref
		int i1 = 0, i2 = 0, a1, a2;//MODIFIED

		while(i1 < _sp & i2 < ot._sp) {//MODIFIED
			a1 = _set[i1];//MODIFIED
			a2 = ot_set[i2];//MODIFIED
			if(a1 == a2) {ans.add(a1); ++i1; ++i2;}
			else if(a1 < a2) {++i1;}
			else ++i2;
		}
		return ans;
	}

	private void resize() {
		int[] tmp = new int[_sp + INC];
		for(int i = 0; i < _sp; ++i) tmp[i] = _set[i];
		_set = tmp;
	}

}
