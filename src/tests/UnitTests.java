package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import program.Crawler;

public class UnitTests {
	
	@Test
	public void LinkStartingWithLetterToAbsoluteAndBaseWithEndingSlash(){
		String link = "downloads";
		String base = "http://dotspencer.com/page/";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals(base + link, result);
	}
	
	@Test
	public void LinkStartingWithLetterToAbsoluteAndBaseWithEndingNoSlash(){
		String link = "downloads";
		String base = "http://dotspencer.com/page";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals(base + "/" + link, result);
	}
	
	@Test
	public void LinkStartingWithSlashToAbsoluteAndBaseWithEndingSlash(){
		String link = "/downloads";
		String base = "http://dotspencer.com/page/";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals("http://dotspencer.com/downloads", result);
	}
	
	@Test
	public void LinkStartingWithSlashToAbsoluteAndBaseWithEndingNoSlash(){
		String link = "/downloads";
		String base = "http://dotspencer.com/page";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals("http://dotspencer.com/downloads", result);
	}
	
	@Test
	public void LinkStartingWithSlashToAbsoluteAndOnlyBaseWithEndingNoSlash(){
		String link = "/downloads";
		String base = "http://dotspencer.com";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals("http://dotspencer.com/downloads", result);
	}
	
	@Test
	public void LinkStartingWithSlashToAbsoluteAndOnlyBaseWithEndingSlash(){
		String link = "/downloads";
		String base = "http://dotspencer.com/";
		
		String result = Crawler.makeAbsolute(link, base);
		
		assertEquals("http://dotspencer.com/downloads", result);
	}
}
