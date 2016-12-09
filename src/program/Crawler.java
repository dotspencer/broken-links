package program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {
	
	public static void main(String[] args){
		try {
			String pageHTML = getHTML("https://egi.utah.edu/");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		
		ArrayList<String> list = new ArrayList<>();
		
		Pattern pattern = Pattern.compile("/<\\s*a.*?href=\"(.*?)\".*?>/");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			list.add(m.group(1));
		}
		
		return (String[]) list.toArray();
	}
	
}
