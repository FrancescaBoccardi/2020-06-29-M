package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private ImdbDAO dao;
	private Graph<Director,DefaultWeightedEdge> grafo;
	private Map<Integer,Director> idMap;
	private List<Adiacenza> adiacenze;
	private int c;
	private int massimo;
	private List<Director> result;
	private int somma;
	
	public Model() {
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<Integer,Director>();
		this.dao.listAllDirectors(idMap); //popolo l'idMap
	}
	
	public void creaGrafo(int year) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(idMap, year));
		
		// aggiungo gli archi
		
		this.adiacenze = dao.getArchi(idMap, year);
		
		for(Adiacenza a : adiacenze) {
			Graphs.addEdge(this.grafo, a.getD1(), a.getD2(), a.getPeso());
		}
		
		for(Adiacenza a : adiacenze) {
			System.out.println(a.toString());
		}
		
	}
	
	public List<Adiacenza> getAdiacenze(Director d){
		List<Adiacenza> result = new ArrayList<>();

		for(Adiacenza a : adiacenze) {
			if(a.getD1().equals(d) || a.getD2().equals(d)) {
				result.add(a);
			} 
		}
		
		Collections.sort(result);
		return result;
	}
	
	public List<Director> ricorsione(Director partenza, int c) {
		this.c=c;
		this.massimo=0;
		List<Director> parziale = new LinkedList<>();
		parziale.add(partenza);
		this.run(0, parziale, partenza,0);
		return result;
	}
	
	private void run(int L, List<Director> parziale, Director partenza, int somma) {
		
		if(somma<=c) {
			
			if(parziale.size()>massimo){
				this.somma=somma;
				massimo = parziale.size();
				result = new LinkedList<>(parziale);
			}
			
		}
		
		if(somma>c) {
			return;
		}
		
		List<Director> vicini = Graphs.neighborListOf(this.grafo, partenza);
		
		for(Director d : vicini) {
			
			if(!parziale.contains(d)) {
				parziale.add(d);
				somma += this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, d));
				this.run(L+1, parziale, d, somma);
				parziale.remove(parziale.size()-1);
				somma -= this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, d));
			}
			
		}
	}

	public Graph<Director, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	public int getSomma() {
		return somma;
	}
	
}
