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
			String pageURL = "https://egi.utah.edu/corporate-associate-program/corporate-associate-list/";
			String pageHTML = getHTML(pageURL);
			String[] links = getLinks(pageHTML);
			
			links = filterLinks(links);
			links = makeAbsolute(links, pageURL);
			
			for(String link : links){
				System.out.println(checkConnection(link) + " : " + link);
			}
			
			System.out.println("-- End of Links --");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String[] makeAbsolute(String[] links, String pageURL){
		ArrayList<String> list = new ArrayList<>();
		for(String link : links){
			if(link.startsWith("/")){
				String absolute = getDomain(pageURL) + link;
				list.add(absolute);
				continue;
			}
			list.add(link);
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static String getDomain(String link){
		
		Pattern pattern = Pattern.compile("(https*:\\/\\/(\\w+(\\.\\w+)+))");
		Matcher m = pattern.matcher(link);
		
		while(m.find()){
			return m.group(1);
		}
		
		return link;
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
	
	/**
	 * Driver method for checking the connection at the web address
	 * @param address
	 * @return The status code
	 */
	public static int checkConnection(String address){
		return checkConnection(address, 0);
	}
	
	/**
	 * Helper method (recursive with followRedirect) for checking the connection at the web address
	 * @param address
	 * @return The status code
	 */
	public static int checkConnection(String address, int depth){
		
		URL url;
		int response = 0;
		depth++;
		
		if(depth > 3){
			System.err.println("Too many redirects");
			return -1;
		}
		
		try {
			url = new URL(address);
			
			if(address.matches("^http:.*")){
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				response = followRedirect(connection, depth);
			}
			
			if(address.matches("^https:.*")){
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				response = followRedirect(connection, depth);
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			return -1;
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param connection
	 * @return The status code
	 * @throws IOException
	 */
	public static int followRedirect(HttpURLConnection connection, int depth) throws IOException{
		int response = connection.getResponseCode();
		if(response == 301){
			return checkConnection(connection.getHeaderField("Location"), depth);
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


