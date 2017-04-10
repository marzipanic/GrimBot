package grimbot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	
	public static String[] getBotFileAsString(String path) {
		try {
			String file = new String(Files.readAllBytes(Paths.get(path)));
			return file.split("\n");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[1];
	}
	
	public static HashMap<Integer, String> getBotFileAsMap(String path) {
		String[] file = getBotFileAsString(path);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		try {
			for (int i=0; i <= file.length -1; i++){
				String[] parts = file[i].split(":", 2);
				//System.out.println("#:" + parts[0] + ", ITEM:" +parts[1]);
				map.put(Integer.parseInt(parts[0]), parts[1]);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public static List<HashMap<String,Object>> getResultSetAsList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

	    while (rs.next()) {
	        HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	            row.put(md.getColumnName(i),rs.getObject(i));
	        }
	        list.add(row);
	    }

	    return list;
	}
}
