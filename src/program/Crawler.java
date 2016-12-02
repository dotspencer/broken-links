package program;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private String base;
	
	public Crawler(){
		
		String url = "https://egi.utah.edu"; // Move this to parameter
		base = url.trim();
		if(base.endsWith("/")){
			base = base.substring(0, base.length() - 2);
		}
		
		ArrayList<String> links = getLinksFromPage(base);
		for(String link : links){
			System.out.println(link);
		}
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
