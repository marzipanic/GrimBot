package grimbot.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {
	public Connection conn = null;
	
	public SQLiteJDBC() {
	    try {
	      Class.forName("org.sqlite.JDBC");
	      conn = DriverManager.getConnection("jdbc:sqlite:test.db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("SUCCESS: SQLite database ready.");
	}
		
	
	public String initializeTable(String name, String columns){
		String pluginName = new Exception().getStackTrace()[1].getClassName();
		String[] split = pluginName.toLowerCase().split("\\.");
		String tableName = split[split.length -1]+"_"+name;
		String sql = "CREATE TABLE IF NOT EXISTS "+tableName+" ("+columns+")";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			System.out.println("DATABASE: table "+tableName+" loaded.");
			return tableName;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
