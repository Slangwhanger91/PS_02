package Modified_files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * this class represents an undirected 0/1 sparse Graph
 *
 * @author Boaz
 *
 */
class Graph {

	private String _file_name;
	private ArrayList<VertexSet> _V;
	private double _TH; // the threshold value
	private int _E_size = 0;
	private boolean _mat_flag = true;

	Graph(String file, double th) {
		this._file_name = file;
		_TH = th;
		_V = new ArrayList<VertexSet>(10);
		init();
	}

	private void init() {
		FileReader fr = null;
		try {
			fr = new FileReader(this._file_name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader is = new BufferedReader(fr);
		try {
			String s = is.readLine();
			StringTokenizer st = new StringTokenizer(s, ", ");
			int len = st.countTokens();
			int line = 0;

			//String ll = "0%   20%   40%   60%   80%   100%";
			//int t = Math.max(1,len/ll.length());
			if (Clique_Tester.Debug) {
				System.out.print("Reading a corrolation matrix of size: " + len + "*" + len + " this may take a while\n");
				//	System.out.println(ll);
			}
			_mat_flag = true;
			if (s.startsWith("A")) {
				if (Clique_Tester.Debug) {
					System.out.print("Assumes compact representation! two line haeder!!!\n");
					System.out.print("Header Line1: " + s + "\n");
					s = is.readLine();
					System.out.print("Header Line2: " + s + "\n");
					s = is.readLine();
					st = new StringTokenizer(s, ", ");
					_mat_flag = false;
				}
			}

			while (s != null) {

				/*if(Clique_Tester.Debug){
                 if(line%t==0) System.out.print(".");                                
                 }*/
				VertexSet vs = new VertexSet();
				if (_mat_flag) {
					for (int i = 0; i < len; ++i) {
						//float v = new Double(st.nextToken()).floatValue();//?????
						float v = new Float(st.nextToken()).floatValue();//MODIFIED(?)
						if (v > _TH & line < i) {
							vs.add(i);
							++_E_size;
						}
					}
				} else {
					st.nextToken();
					while (st.hasMoreTokens()) {
						int ind = new Integer(st.nextToken()).intValue();//?????
						// bug fixed as for Ronens format.
						if (line < ind) {
							vs.add(ind);
						}
					}
				}
				_V.add(vs);
				++line;
				s = is.readLine();
				if (s != null) {
					st = new StringTokenizer(s, ", ");
				}
			}//while

			if (_mat_flag & Clique_Tester.Convert) {
				write2file();
			}
			if (Clique_Tester.Debug) {
				System.out.print("\ndone reading the graph! ");
				this.print();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public VertexSet Ni(int i) {
		return _V.get(i);//MODIFIED
	}

	public void print() {
		System.out.print("Graph: |V|=" + this._V.size() + " ,  |E|=" + _E_size + "\n");
	}

	/**
	 * computes all the 2 cliques --> i.e. all the edges
	 *
	 * @return
	 */
	private ArrayList<VertexSet> allEdges() { // all edges – all cliques of size 2/
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		int size = _V.size();//MODIFIED
		for (int i = 0; i < size; ++i) {
			VertexSet curr = _V.get(i);
			int size2 = curr.size();
			for (int a = 0; a < size2; ++a) {
				if (i < curr.at(a)) {
					VertexSet tmp = new VertexSet();
					tmp.add(i);
					tmp.add(curr.at(a));
					ans.add(tmp);
				}
			}
		}
		return ans;
	}

	/**
	 * This method computes all cliques of size [min,max] or less using a memory
	 * efficient DFS like algorithm. The implementation was written with CUDA in
	 * mind - as a based code for a possibly implementation of parallel cernal.
	 *
	 */
	/*ArrayList<VertexSet> All_Cliques_DFS(int min_size, int max_size) {
        Clique.init(this);
        ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
        ArrayList<VertexSet> C0 = allEdges(); // all edges – all cliques of size 2/
        //	ans.addAll(C0);
        int len = C0.size();
        //System.out.println("|E|= "+len);
        int count = 0;
        for (int i = 0; i < len; i++) {

            VertexSet curr_edge = C0.get(i);
            Clique edge = new Clique(curr_edge.at(0), curr_edge.at(1));
            ArrayList<Clique> C1 = allC_seed(edge, min_size, max_size);
            count += C1.size();
            //System.out.println("alg2 "+i+") edge:["+curr_edge.at(0)+","+curr_edge.at(1)+"]"+C1.size() +"  total: "+count);
            addToSet(ans, C1);
        } // for
        return ans;
    }*/

	/**
	 *
	 * @param min_size
	 * @param max_size
	 */
	public void All_Cliques_DFS(String out_file, int min_size, int max_size) {
		Clique.init(this);
		ArrayList<VertexSet> C0 = allEdges(); // all edges – all cliques of size 2/
		int len = C0.size();
		System.out.print("|E|= " + len + "\n");
		int count = 0;

		FileWriter fw = null;
		try {
			fw = new FileWriter(out_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter os = new PrintWriter(fw);
		//os.println("A");

		//String ll = "0%   20%   40%   60%   80%   100%";
		//int t = Math.max(1, len / ll.length());
		if (Clique_Tester.Debug) {
			System.out.print("Computing all cliques of size[" + min_size + "," + max_size + "] based on " + len + " edges graph, this may take a while\n");
			//System.out.print(ll + "\n");
		}
		os.print("All Cliques: file [min max] TH," + this._file_name + "," + min_size + ", " + max_size + ", " + this._TH + "\n");
		os.println("index, edge, clique size, c0, c1, c2, c3, c4,  c5, c6, c7, c8, c9");
		for (int i = 0; i < len; ++i) {
			VertexSet curr_edge = C0.get(i);
			Clique edge = new Clique(curr_edge.at(0), curr_edge.at(1));
			ArrayList<Clique> C1 = allC_seed(edge, min_size, max_size);

			int size = C1.size();
			for (int b = 0; b < size; ++b) {
				Clique c = C1.get(b);
				if (c.size() >= min_size) {
					os.print(count + ", " + i + "," + c.size() + ", " + c.toFile() + "\n");
					++count;
				}
			}
			if (count > Clique_Tester.MAX_CLIQUE) {
				os.print("ERROR: too many cliques! - cutting off at " + Clique_Tester.MAX_CLIQUE + " for larger files change the default Clique_Tester.MAX_CLIQUE param\n");
				i = len;
			}
			/*if(i%t==0) {//MODIFIED
             System.out.print(".");
             }*/
		} //for
		System.out.println();

		os.close();
		try {fw.close();} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * this function simply add the clique (with no added intersection data) to
	 * the set of cliques)
	 *
	 * @param ans
	 * @param C1
	 */
	private void addToSet(ArrayList<VertexSet> ans, ArrayList<Clique> C1) {//???????
		for (int i = 0; i < C1.size(); i++) {
			ans.add(C1.get(i).clique());
		}
	}

	ArrayList<Clique> allC_seed(Clique edge, int min_size, int max_size) {
		ArrayList<Clique> ans = new ArrayList<Clique>();
		ans.add(edge);
		int i = 0;
		//	int size = 2;
		while (ans.size() > i) {
			Clique curr = ans.get(i);
			if (curr.size() < max_size) {
				VertexSet Ni = curr.commonNi();
				int size = Ni.size();//MODIFIED
				for (int a = 0; a < size; ++a) {
					Clique c = new Clique(curr, Ni.at(a));
					ans.add(c);
				}
			} else {
				i = ans.size();
			} // speedup trick 
			++i;
		}

		return ans;
	}

	public void write2file() {
		FileWriter fw = null;
		try {
			fw = new FileWriter(this._file_name + "_DG.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//PrintWriter os = new PrintWriter(fw);//MODIFIED
		BufferedWriter os = new BufferedWriter(fw);
		try {
			os.append("ALL_Cliques: of file: " + _file_name + ",  TH:" + this._TH);
			os.newLine();os.newLine();
			//os.print("ALL_Cliques: of file: "+_file_name+",  TH:"+this._TH + "\n\n");
			int size = _V.size();
			for (int i = 0; i < size; ++i) {
				VertexSet curr = _V.get(i);
				os.append(i + ", " + curr.toFile());
				os.newLine();
				//os.print(i+", "+curr.toFile() + "\n");
			}
			os.close();

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
