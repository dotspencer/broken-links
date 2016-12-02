package program;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private String base;
	
	public Crawler(String url){
		
		base = url.trim();
		if(base.endsWith("/")){
			base = base.substring(0, base.length() - 2);
		}
		
		checkLinksOnPage(base);
	}
	
	private void checkLinksOnPage(String url){
		ArrayList<String> links = getLinksFromPage(base);
		HashMap<String, Boolean> result = new HashMap<>();
		
		for(String link : links){
			boolean live =  pageIsLive(base + link);
			System.out.println(live + "\t" + link);
			result.put(link,  live);
		}
	}
	
	private boolean pageIsLive(String url){
		try {
			URL u = new URL(url);
			u.openConnection().connect();
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
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
		boolean externalLink = !url.contains(base) && !url.startsWith("/");
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
		return url.replace(base, "");
	}
}
