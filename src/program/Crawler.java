package program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Crawler {
	
	public static void main(String[] args){
		
		//System.out.println(checkConnection("http://egi.utah.edu"));
		
		try {
			String pageHTML = getHTML("https://egi.utah.edu/corporate-associate-program/corporate-associate-list/");
			String[] links = getLinks(pageHTML);
			
			links = filterLinks(links);
			
			for(String link : links){
				System.out.println(checkConnection(link) + " : " + link);
			}
			
			System.out.println("-- End of Links --");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String[] filterLinks(String[] links){
		ArrayList<String> list = new ArrayList<>();
		for(String link : links){
			boolean invalid = false;
			invalid = invalid || link.matches("^mailto.*");
			invalid = invalid || link.equals("#");
			if(!invalid){
				list.add(link);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static int checkConnection(String address){
		
		URL url;
		int response = -1;
		
		try {
			url = new URL(address);
			
			if(address.matches("^http:.*")){
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				response = followRedirect(connection);
			}
			
			if(address.matches("^https:.*")){
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				response = connection.getResponseCode();
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return response;
	}
	
	public static int followRedirect(HttpURLConnection connection) throws IOException{
		int response = connection.getResponseCode();
		if(response == 301){
			return checkConnection(connection.getHeaderField("Location"));
		}
		
		return response;
	}
	
	
	public static String getHTML(String url) throws IOException{
	
		URL address = new URL(url);
		URLConnection connection = address.openConnection();
		InputStream input = connection.getInputStream();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder builder = new StringBuilder();
		
		String line = "";
		while((line = reader.readLine()) != null){
			if(!line.isEmpty()){
				builder.append(line.trim() + "\n");
			}
		}
		
		return builder.toString();
			
	}
	
	public static String[] getLinks(String html){
		
		HashSet<String> set = new HashSet<>();
		
		Pattern pattern = Pattern.compile("<\\s*a.*?href=\"(.*?)\".*?>");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			set.add(m.group(1).trim());
		}
		
		return set.toArray(new String[set.size()]);
	}
	
	public class ConnectionState{
		public String state;
	}
}


