package program;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private String domain;
	private String startPage;
	
	public Crawler(String url){
		
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		
		domain = uri.getScheme() + "://" + uri.getHost();
		startPage = domain + uri.getPath();
		
		//System.out.println(pageIsLive("https://familysearch.org/volunteer"));
		checkLinksOnPage(startPage);
	}
	
	private void checkLinksOnPage(String url){
		
		if(!pageIsLive(url).equals("200")){
			System.out.println("Base url not live");
			return;
		}
		
		ArrayList<String> links = getLinksFromPage(url);
		HashMap<String, String> result = new HashMap<>();
		
		for(String link : links){
			String live =  pageIsLive(domain + link);
			if(live.equals("200")){
				System.out.println(live + "\t" + link);
			} else {
				System.err.println(live + "\t" + link);
			}
			
			result.put(link,  live);
		}
	}
	
	private String pageIsLive(String url){
		
		int code;
		
		try {
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			code = connection.getResponseCode();
			
		} catch (MalformedURLException e) {
			return "mal";
		} catch (IOException e) {
			return "io";
		}
		return code + "";
	}
	
	
	private ArrayList<String> getLinksFromPage(String url){
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Elements elements = doc.select("a");
		
		ArrayList<String> links = new ArrayList<>();
		for(Element e : elements){
			String href = e.attr("href");
			
			href = urlFilter(href);
			if(href == null){
				continue;
			}
			links.add(href);
		}
		return links;
	}
	
	/**
	 * Strips away invalid url strings.
	 * @param url
	 * @return
	 */
	private String urlFilter(String url){
		url = url.trim();
		
		// Must be an external link
		boolean externalLink = !url.contains(domain) && !url.startsWith("/");
		url = makeRelative(url);
		
		boolean invalid = false;
		invalid = invalid || externalLink;
		invalid = invalid || url.equals("#");
		invalid = invalid || url.equals("/");
		invalid = invalid || url.contains("mailto:");
		invalid = invalid || url.isEmpty();
		
		if(invalid){
			return null;
		}
		return url;
	}
	
	private String makeRelative(String url){
		if(url.startsWith("/")){
			return url;
		}
		return url.replace(domain, "");
	}
}
