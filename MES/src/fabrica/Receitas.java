package fabrica;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dijkstra.DijkstraAlgorithm;
import dijkstra.Edge;
import dijkstra.Graph;
import dijkstra.Vertex;

public abstract class Receitas {

	public Receitas() {
		// TODO Auto-generated constructor stub
	}

	static List<Vertex> nodes;
	static List<Edge> edges;
	/** maquina | tempo ! ferramenta
	 * 
	 * */
	public static List<String> rotaMaquinas(String origem, String destino, int tempoRestanteMaquina) {
		List<String> rota = new ArrayList<String>();
		int origemInt = Integer.valueOf(origem.substring(1, 2));
		int destinoInt = Integer.valueOf(destino.substring(1, 2));
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		for (int i = 0; i < 10; i++) {
			Vertex location = new Vertex("P" + (i), "P" + (i));
			nodes.add(location);
		}
		int x=tempoRestanteMaquina;


		destino=destino.toUpperCase();
		origem = origem.toUpperCase();
		/*maquina ferramenta*/
		addLane("A 1", 1, 2, 15);
		addLane("A 1", 2, 3, 15);
		addLane("A 2", 2, 6, 15);
		addLane("A 3", 6, 9, 15);

		addLane("B 1", 1, 3, 20);
		addLane("B 1", 3, 4, 15);
		addLane("B 2", 3, 7, 20);
		addLane("B 3", 7, 9, 20);
		
		if((origem.equals("P1") || origem.equals("P3")) && destino.equals("P9"))
			addLane("C 2", 4, 8, 10+x);//vai ter de esperar o tempo que ela termine, mais o resto
		else
			addLane("C 2", 4, 8, 10);//vai ter de esperar o tempo que ela termine, mais o resto

			
		addLane("C 1", 1, 4, 10);
		addLane("C 1", 4, 5, 30);
		addLane("C 3", 8, 9, 10);

		// Lets check from location Loc_1 to Loc_10
		Graph graph = new Graph(nodes, edges);
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		dijkstra.execute(nodes.get(origemInt));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(destinoInt));
		if (path != null) {
			for (int i = 0; i < path.size(); i++) {
				for (Edge edge : edges) {
					if (i != path.size() - 1) {
						if (edge.getSource().getName().equals(path.get(i).getId())
								&& edge.getDestination().getName().equals(path.get(i + 1).getId())) {
							rota.add(""+edge.getId().charAt(0));//coloca maquina
							if(edge.getSource().getName().equals("P4") && edge.getDestination().getName().equals("P8")) {
								rota.add(""+10);

							}else{
								rota.add(""+edge.getWeight());

							}
							rota.add(""+edge.getId().charAt(2));//coloca ferramenta

							
						}
					}
				}
			}
		}
		return rota;
	}

	private static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
		Edge lane = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
		edges.add(lane);
	}

}
