package grimbot.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {
	private Connection connection = null;
	
	public SQLiteJDBC() {
	    try {
	      Class.forName("org.sqlite.JDBC");
	      connection = DriverManager.getConnection("jdbc:sqlite:test.db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Opened database successfully");
	}
	
	public void initializeTable(String name, String columns){
		String prefix = new Exception().getStackTrace()[1].getClassName();
		String query = "create table if not exists "+prefix+"_"+name+" ("+columns+")";
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
