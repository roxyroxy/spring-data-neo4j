package fi.gapps.intra.thesis.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fi.gapps.intra.thesis.model.Edge;
import fi.gapps.intra.thesis.model.Vertex;
import fi.gapps.intra.thesis.service.EdgeService;
import fi.gapps.intra.thesis.service.VertexService;

@RestController
@RequestMapping("/api/v1/")
public class GraphController {

	@Autowired
	private VertexService vertexService;
	
	@Autowired
	private EdgeService edgeService;
	@Transactional
	@RequestMapping(value = "vertex/all", method = RequestMethod.POST, consumes = "application/json")
	public void addVertex(@RequestBody List<Vertex> vertices) {
		for (Vertex v : vertices) {
			Vertex old = vertexService.findByEmail(v.getEmail());
			if (old == null) {
				vertexService.create(v);
			}else{
				for(Edge e: v.getTeammates()){
					boolean found = false;
					for(Edge t: old.getTeammates()){
						if (e.equals(t)){
							found = true;
						}
					}
					if(found == false)
					old.worksWith(e);
				}
			//	vertexService.create(old);
			}
		}
	
		System.out.println("Done");

	}
	
	
		@RequestMapping(value = "/community", method = RequestMethod.POST)
		@ResponseBody
		public void updateBatch( @RequestParam final String vertices ) throws IOException {
			
			System.out.println("I have been called!!!");
			System.out.println(vertices);
			try {
				TaskQueueSample.run();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	

	@Transactional
	@RequestMapping(value = "vertex", method = RequestMethod.POST, consumes = "application/json")
	public Vertex insert(@RequestBody Vertex v) {
		System.out.println("POST");
			Vertex old = vertexService.findByEmail(v.getEmail());
			if (old == null) 
//			{
				return vertexService.create(v);
//			}else{
//				for(Edge e: v.getTeammates()){
//					boolean found = false;
//					for(Edge t: old.getTeammates()){
//						if (e.equals(t)){
//							found = true;
//						}
//					}
//					if(found == false)
//					old.worksWith(e);
//				}
				return old;
			//}

	}
	
	@Transactional(readOnly = true)
	@RequestMapping(value = "vertex", method = RequestMethod.GET)
	public Iterable<Vertex> getAllVertices() {
		return vertexService.findAll();
	}
	
	@Transactional(readOnly = true)
	@RequestMapping(value = "vertex/teammates", method = RequestMethod.GET)
	public List<String> getCommunity(@RequestHeader (name="email") String email) {
		System.out.println("Email " + email);
		return vertexService.getCommunity(email);
	}
	
	@Transactional(readOnly = true)
	@RequestMapping(value = "vertex/topFriends", method = RequestMethod.GET)
	public List<String> getTopFriends(@RequestHeader (name="email") String email) {
		System.out.println("Email " + email);
		return vertexService.getTopThree(email);
	}
	
	@Transactional
	@RequestMapping(value = "vertex/delete", method = RequestMethod.DELETE)
	public void deleteAll() {
		Iterable<Vertex> vertices  = vertexService.findAll();
		for(Vertex v: vertices ){
			vertexService.delete(v);
		}
		Iterable<Edge> edges = edgeService.findAll();
		for(Edge e: edges){
			edgeService.delete(e);
		}

	}
}
