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
			String pageURL = "https://egi.utah.edu/corporate-associate-program/training-certifications-degrees/";
			String pageHTML = getHTML(pageURL);
			String[] links = getLinks(pageHTML);
			
			links = filterLinks(links);
			links = makeAbsolute(links, pageURL);
			
			for(String link : links){
				int connection = checkConnection(link);
				if(connection == 404){
					System.out.println("\n" + connection + " " + link);
				} else {
					System.out.print("|");
				}
			}
			
			System.out.println("\n-- End of Links --");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String[] makeAbsolute(String[] links, String pageURL){
		
		// Reference: http://stackoverflow.com/a/4071178/3498950
		//
		// TODO links starting with ./
		// TODO links starting with ?
		// TODO links starting with <nothing>
		
		
		ArrayList<String> list = new ArrayList<>();
		for(String link : links){
			if(link.startsWith("//")){
				String absolute = getProtocol(pageURL) + link;
				list.add(absolute);
				continue;
			}
			if(link.startsWith("/")){
				String absolute = getDomain(pageURL) + link;
				list.add(absolute);
				continue;
			}
			list.add(link);
		}
		return list.toArray(new String[list.size()]);
	}
	
	public static String getProtocol(String link){
		Pattern pattern = Pattern.compile("(https?:)");
		Matcher m = pattern.matcher(link);
		
		while(m.find()){
			return m.group(1);
		}
		
		return link;
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
			invalid = invalid || link.startsWith("#");
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
		if(response == 301 || response == 302){
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
		
		// Stripping away <script> tags because they sometimes have anchor string literals
		Pattern pattern1 = Pattern.compile("<script>.*?<\\/script>", Pattern.DOTALL);
		Matcher m1 = pattern1.matcher(html);
		
		while(m1.find()){
			html = html.replace(m1.group(), "");
		}
		
		// Gathering all links
		HashSet<String> set = new HashSet<>();
		
		Pattern pattern = Pattern.compile("<\\s*a.*?href=\"(.*?)\".*?>");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			set.add(m.group(1).trim());
		}
		
		return set.toArray(new String[set.size()]);
	}
	
	public class ConnectionState{
		public String url;
		public int status;
		
		public ConnectionState(String url, int status){
			this.url = url;
			this.status = status;
		}
	}
}


