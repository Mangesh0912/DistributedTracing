package com.democompany.distributedtracing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

class Graph {
	private Map<String, Map<String, Integer>> adjList = new HashMap<String, Map<String, Integer>>();

	public void addEdge(String start, String end, int weight) {
		adjList.putIfAbsent(start, new HashMap<>());
		adjList.get(start).put(end, weight);
	}

	public Integer getEdgeWeight(String start, String end) {
		return adjList.getOrDefault(start, new HashMap<>()).getOrDefault(end, null);
	}

	public Map<String, Map<String, Integer>> getAdjList() {
		return adjList;
	}

}

class Node {
	private String vertex;
	private int distance;

	public Node(String vertex, int distance) {
		super();
		this.vertex = vertex;
		this.distance = distance;
	}

	public String getVertex() {
		return vertex;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public String toString() {
		return "Node [vertex=" + vertex + ", distance=" + distance + "]";
	}

}

class AnalyzeTrace {

	private Graph graph;

	public AnalyzeTrace(Graph graph) {
		// TODO Auto-generated constructor stub
		this.graph = graph;
	}

	public String getLatencyForService(String... services) {
		int totalLatency = 0;

		for (int i = 0; i < services.length - 1; i++) {
			Integer latency = graph.getEdgeWeight(services[i], services[i + 1]);
			if (latency == null) {
				return "NO SUCH TRACE";
			}
			totalLatency = totalLatency + latency;
		}

		return String.valueOf(totalLatency);

	}

	// Using Djistrka shortest Path algorithm
	public int shortestPath(String start, String end) {

		Map<String, Integer> distances = new HashMap<String, Integer>();
		Set<String> visited = new HashSet<String>();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				return o1.getDistance() - o2.getDistance();
			}

		});

		for (String vertex : graph.getAdjList().keySet()) {
			distances.put(vertex, Integer.MAX_VALUE);
		}

		distances.put(start, 0);
		queue.add(new Node(start, 0));

		while (!queue.isEmpty()) {
			Node currentNode = queue.poll();
			String currentVertex = currentNode.getVertex();

			if (!visited.contains(currentVertex)) {
				visited.add(currentVertex);

				// get the neighbors
				for (Map.Entry<String, Integer> entry : graph.getAdjList().getOrDefault(currentVertex, new HashMap<>())
						.entrySet()) {
					String neighborVertex = entry.getKey();
					int edgeWeight = entry.getValue();

					if (!visited.contains(neighborVertex) || (visited.contains(neighborVertex) && start.equals(end))) {
						int newDistance = distances.get(currentVertex) + edgeWeight;
						if (newDistance < distances.get(neighborVertex)) {
							distances.put(neighborVertex, newDistance);
							queue.add(new Node(neighborVertex, distances.get(neighborVertex)));
							// edge case when start and finish are same

						}
						if (start.equals(end) && neighborVertex.equals(start)) {
							if (distances.get(start) == 0) {
								distances.put(start, newDistance);
							} else {
								distances.put(start, Math.min(newDistance, distances.get(start)));
							}
						}

					}
				}
			}

		}

		return distances.get(end);
	}

	// use DFS for this problem
	public int calculateMaxHops(String start, String end, int maxHops) {
		return dfs(start, end, 0, maxHops);
	}

	private int dfs(String current, String end, int hops, int maxHops) {
		if (hops > maxHops) {
			return 0;
		}
		if (current.equals(end) && hops > 0) {
			return 1;
		}

		// find neighbors of current
		int count = 0;
		for (Map.Entry<String, Integer> entry : graph.getAdjList().get(current).entrySet()) {
			count = count + dfs(entry.getKey(), end, hops + 1, maxHops);
		}
		return count;
	}

	public int countPathsExactHops(String start, String end, int exactHops) {
		return dfsExact(start, end, 0, exactHops);
	}

	private int dfsExact(String current, String end, int currentHops, int exactHops) {
		if (currentHops > exactHops) {
			return 0;
		}
		if (current.equals(end) && currentHops == exactHops) {
			return 1;
		}
		int count = 0;
		for (Map.Entry<String, Integer> entry : graph.getAdjList().get(current).entrySet()) {
			count = count + dfsExact(entry.getKey(), end, currentHops + 1, exactHops);

		}
		return count;
	}

	public int countPathsMaxLatency(String start, String end, int maxLatency) {
		return dfsLatency(start, end, 0, maxLatency);
	}

	private int dfsLatency(String current, String end, int latency, int maxLatency) {
		if (latency >= maxLatency)
			return 0;
		int count = current.equals(end) && latency != 0 ? 1 : 0;

		for (Map.Entry<String, Integer> entry : graph.getAdjList().get(current).entrySet()) {
			count = count + dfsLatency(entry.getKey(), end, latency + entry.getValue(), maxLatency);
		}

		return count;

	}

}

public class DistributedTracing {

	public Graph populateGraph() {
		Scanner scanner = null;
		Graph graph = new Graph();
		try {
			File file = new File("/Users/mangeshkalsulkar/Documents/input.txt");
			scanner = new Scanner(file);

			while (scanner.hasNext()) {
				String edge = scanner.next();
				String start = String.valueOf(edge.charAt(0));
				String end = String.valueOf(edge.charAt(1));
				int weight = Integer.parseInt(String.valueOf(edge.charAt(2)));
				graph.addEdge(start, end, weight);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return graph;

	}

	public static void main(String[] args) {
		DistributedTracing dt = new DistributedTracing();
        Graph graph = new Graph();
        graph = dt.populateGraph();
		AnalyzeTrace trace = new AnalyzeTrace(graph);
		System.out.println(trace.getLatencyForService("A", "B", "C"));
		System.out.println(trace.getLatencyForService("A", "D"));
		System.out.println(trace.getLatencyForService("A", "D", "C"));
		System.out.println(trace.getLatencyForService("A", "E", "B", "C", "D"));
		System.out.println(trace.getLatencyForService("A", "E", "D"));
		System.out.println(trace.calculateMaxHops("C", "C", 3));
		System.out.println(trace.countPathsExactHops("A", "C", 4));
		System.out.println(trace.shortestPath("A", "C"));
		System.out.println(trace.shortestPath("B", "B"));
		System.out.println(trace.countPathsMaxLatency("C", "C", 30));

	}

}
