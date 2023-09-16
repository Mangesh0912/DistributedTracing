package com.democompany.distributedtracing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class DistributedTracingTest {
	
	@Test
	public void testComputeDistributedTracing() throws FileNotFoundException {
		DistributedTracing dt = new DistributedTracing();
		//dt.computeDistributedTracing();
		//dt.computeAverageLatencyOfTrace();
		Graph graph = dt.populateGraph();
		AnalyzeTrace at = new AnalyzeTrace(graph);
		
		//Test Latency for service when no trace present
		String result = at.getLatencyForService("A", "B", "F");
		assertEquals(result, "NO SUCH TRACE");
		
		//Test Latency for service when no trace present and having bad inputs
		result = at.getLatencyForService("A", "B", null);
		assertEquals(result, "NO SUCH TRACE");
		
		
		//Test Latency for service when proper trace is present
	    result = at.getLatencyForService("A", "B", "C");
		assertEquals(result, "9");
		
		
		//Test Max hops
		int maxHops = at.calculateMaxHops("C", "C", 3);
		assertEquals(maxHops, 2);
		
		
		//Test Exact Paths
		int exactPaths = at.countPathsExactHops("A", "C", 4);
		assertEquals(exactPaths, 3);
		
		//Test Shortest path
		int shortTestPathLengh = at.shortestPath("A", "C");
		assertEquals(shortTestPathLengh, 9);
		
		//Test Shortest path
		int countOfPathsWithMaxLatency = at.countPathsMaxLatency("C", "C", 30);
		assertEquals(countOfPathsWithMaxLatency, 7);
	}

}
