package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void listAllDirectors(Map<Integer,Director> idMap){
		String sql = "SELECT * FROM directors";
	
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("id"))) {
					idMap.put(res.getInt("id"), new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name")));
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
	
	public List<Director> getVertici(Map<Integer,Director> idMap, int year) {
		
		String sql = "SELECT DISTINCT d.id "
				+ "FROM movies m, movies_directors md, directors d "
				+ "WHERE m.id=md.movie_id AND d.id=md.director_id AND YEAR=?";
		
		List<Director> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				result.add(idMap.get(res.getInt("id")));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(Map<Integer,Director> idMap,int year){
		
		String sql = "SELECT md1.director_id as d1, md2.director_id as d2, COUNT(*) AS peso "
				+ "FROM roles r1, roles r2, movies_directors md1, movies_directors md2, movies m1, movies m2 "
				+ "WHERE m1.year=m2.year AND m2.year = ? "
				+ "AND (md1.movie_id=r1.movie_id AND r1.movie_id=m1.id) "
				+ "AND (md2.movie_id=r2.movie_id AND r2.movie_id=m2.id) "
				+ "AND md1.director_id>md2.director_id "
				+ "AND r1.actor_id=r2.actor_id "
				+ "GROUP BY md1.director_id, md2.director_id";
		
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				result.add(new Adiacenza(idMap.get(res.getInt("d1")), idMap.get(res.getInt("d2")), res.getInt("peso")));
			}
			
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
}
