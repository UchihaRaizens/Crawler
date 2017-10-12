
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    private String url;
    Writer writer;

    private static int count = 0; 
    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.url = url;
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
               // this.links.add(link.absUrl("href"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    
    /**
     * 
     * @return
     */
    private String parseUserInformations() {
    	String nickName = htmlDocument.getElementsByClass("actual_persona_name").text().trim();
    	String name = htmlDocument.getElementsByClass("header_real_name ellipsis").select("bdi").first().text().trim();
    	String level = htmlDocument.getElementsByClass("friendPlayerLevelNum").first().text();
    	String numberOfGames = htmlDocument.select("a[href="+url+"/games/?tab=all]").first().text().trim();
    	String numberOfBudgets = htmlDocument.select("a[href="+url+"/badges/]").first().text().trim();
    	String numberOfFriends = htmlDocument.select("a[href="+url+"/friends/]").first().text().trim();;
    	String city = "";
    	String state ="";
    	
    	Element cityAndState = htmlDocument.getElementsByClass("profile_flag").first();
    	if(cityAndState != null) {
    		 String tempString = cityAndState.nextSibling().toString().trim();
    		 city = tempString.substring(0, tempString.indexOf(",")==-1?0:tempString.indexOf(",")).trim();
    		 state = tempString.substring(tempString.indexOf(",")==-1?0:tempString.indexOf(",")+1, tempString.length()).trim();
    	}
    //	PrintWriter writer;
		try {
			
			writer = new OutputStreamWriter(new FileOutputStream(
				    new File("pers.txt"),true), "UTF-8");
			writer.append("Nickname: " + nickName).append(System.lineSeparator());
			writer.append("Name: " +name).append(System.lineSeparator());
			writer.append("Level: " +level).append(System.lineSeparator());
			writer.append("Games: " +numberOfGames).append(System.lineSeparator());
			writer.append("Budgets: " + numberOfBudgets).append(System.lineSeparator());
			writer.append("Friends: " +numberOfFriends).append(System.lineSeparator());
			writer.append("City: " +city).append(System.lineSeparator());
			writer.append("State: " +state).append(System.lineSeparator());
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println("Nickname: " + nickName);
    	System.out.println("Meno: " + name);
    	System.out.println("Mesto: " +city);
    	System.out.println("Štát: " + state);
    	System.out.println("Level: " + level);
    	System.out.println("Poèet  hier: " + numberOfGames);
    	System.out.println("Poèet odznakov: "  + numberOfBudgets);
    	System.out.println("Poèet priate¾ov: "  + numberOfFriends);
    	
    	
    	
    	return " ";
    }

    
    /**
     * 
     */
    private void getUsers() {
    	crawl(url + "/friends/");
    	Elements linkOnPage = htmlDocument.getElementById("memberList").select("a[href]");
    	for(Element e: linkOnPage) {
    		System.out.println(e.absUrl("href"));
    		this.links.add(e.absUrl("href"));
    	}
    	
    }
    
    
    /**
     * Performs a search on the body of on the HTML document that is retrieved. This method should
     * only be called after a successful crawl.
     * 
     * @param searchWord
     *            - The word or string to look for
     * @return whether or not the word was found
     */
    public boolean searchForWord(String searchWord)
    {
        // Defensive coding. This method should only be used after a successful crawl.
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        
        System.out.println("Searching for the word " + searchWord + "...");
        parseUserInformations();
        getUsers();
        
      /*  Element nameHero = htmlDocument.getElementById("binding-5746");
        
        String bodyText = nameHero.data();
        System.out.println(bodyText + " toto som crawlol");*/
        return false;
      //  return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }


    public List<String> getLinks()
    {
        return this.links;
    }

}