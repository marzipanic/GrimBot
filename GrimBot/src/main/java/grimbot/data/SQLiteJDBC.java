package grimbot.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {
	public Connection connection = null;
	
	public SQLiteJDBC() {
	    try {
	      Class.forName("org.sqlite.JDBC");
	      connection = DriverManager.getConnection("jdbc:sqlite:test.db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("SUCCESS: SQLite database ready.");
	}
	
	public void initializeTable(String name, String columns){
		String pluginName = new Exception().getStackTrace()[1].getClassName();
		String[] split = pluginName.toLowerCase().split("\\.");
		String query = "create table if not exists "+split[split.length -1]+"_"+name+" ("+columns+")";
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(query);
			statement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
