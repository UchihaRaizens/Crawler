import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {

	private static final int MAX_PAGES_TO_SEARCH = 10;
	private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
	
	private String nextURL() {
		String nextUrl;
        do
        {
            nextUrl = this.pagesToVisit.remove(0);
        } while(this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;	
	}
	
	
	 public void search(String url, String searchWord)
	  {
	      while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
	      {
	          String currentUrl;
	          SpiderLeg leg = new SpiderLeg();
	          if(this.pagesToVisit.isEmpty())
	          {
	              currentUrl = url;
	              this.pagesVisited.add(url);
	          }
	          else
	          {
	              currentUrl = nextURL();
	          }
	          leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
	                                 // SpiderLeg
	          boolean success = leg.searchForWord(searchWord);
	          if(success)
	          {
	              System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
	              break;
	          }
	          this.pagesToVisit.addAll(leg.getLinks());
	      }
	      System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
	  }
	 
	public static void main(String[] args) {
		Crawler spider = new Crawler();
        spider.search("http://steamcommunity.com/profiles/76561198071606967", "shyvana");
	//	spider.search("https://jsoup.org/download", "shyvana");
	//	SpiderLeg sp = new SpiderLeg();
	//	sp.crawl("http://matchhistory.eune.leagueoflegends.com/en/#match-history/EUN1/207106450");
	}

}
