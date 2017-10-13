
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {
	// We'll use a fake USER_AGENT so the web server thinks the robot is a normal
	// web browser.
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
	private String url;
	Writer writer;

	private static int count = 0;

	/**
	 * This performs all the work. It makes an HTTP request, checks the response,
	 * and then gathers up all the links on the page. Perform a searchForWord after
	 * the successful crawl
	 * 
	 * @param url
	 *            - The URL to visit
	 * @return whether or not the crawl was successful
	 */
	public boolean crawl(String url) {
		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).timeout(3000);
			Document htmlDocument = connection.get();
			this.url = url;
			this.htmlDocument = htmlDocument;
			if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
															// indicating that everything is great.
			{
				System.out.println("\n**Visiting** Received web page at " + url);
			}
			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("**Failure** Retrieved something other than HTML");
				return false;
			}
			Elements linksOnPage = htmlDocument.select("a[href]");
			System.out.println("Found (" + linksOnPage.size() + ") links");
			for (Element link : linksOnPage) {
				// this.links.add(link.absUrl("href"));
			}
			return true;
		} catch (IOException ioe) {
			// We were not successful in our HTTP request
			return false;
		}
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	private List<String> parseGamesFromJSON(String json) {
		List<String> JSONs = new ArrayList<>();
		int position = -1;
		while (true) {
			position = json.lastIndexOf("{\"appid\"");
			String temp = json.substring(position, json.length());
			JSONs.add(temp);
			System.out.println(temp);
			if (position == 0) {
				break;
			}
			json = json.substring(0, position - 1);
		}
		return JSONs;
	}

	/**
	 * 
	 * @return
	 */
	private String parseUserInformations() {
		String nickName = htmlDocument.getElementsByClass("actual_persona_name").text().trim();
		String name = htmlDocument.getElementsByClass("header_real_name ellipsis").select("bdi").first().text().trim();
		String level = htmlDocument.getElementsByClass("friendPlayerLevelNum").first().text();
		String numberOfGames = htmlDocument.select("a[href=" + url + "/games/?tab=all]").first().text().trim();
		String numberOfBudgets = htmlDocument.select("a[href=" + url + "/badges/]").first().text().trim();
		String numberOfFriends = htmlDocument.select("a[href=" + url + "/friends/]").first().text().trim();
		;
		String city = "";
		String state = "";

		Element cityAndState = htmlDocument.getElementsByClass("profile_flag").first();
		if (cityAndState != null) {
			String tempString = cityAndState.nextSibling().toString().trim();
			city = tempString.substring(0, tempString.indexOf(",") == -1 ? 0 : tempString.indexOf(",")).trim();
			state = tempString
					.substring(tempString.indexOf(",") == -1 ? 0 : tempString.indexOf(",") + 1, tempString.length())
					.trim();
		}
		// PrintWriter writer;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(new File("pers.txt"), true), "UTF-8");
			writer.append("Nickname: " + nickName).append(System.lineSeparator())
				  .append("Name: " + name).append(System.lineSeparator())
				  .append("Level: " + level).append(System.lineSeparator())
				  .append("Games: " + numberOfGames).append(System.lineSeparator())
				  .append("Budgets: " + numberOfBudgets).append(System.lineSeparator())
				  .append("Friends: " + numberOfFriends).append(System.lineSeparator())
				  .append("City: " + city).append(System.lineSeparator())
				  .append("State: " + state).append(System.lineSeparator())
				  .close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Nickname: " + nickName);
		System.out.println("Meno: " + name);
		System.out.println("Mesto: " + city);
		System.out.println("Štát: " + state);
		System.out.println("Level: " + level);
		System.out.println(numberOfGames);
		System.out.println(numberOfBudgets);
		System.out.println(numberOfFriends);
		return " ";
	}

	/**
	 * Perform a search on html document and get links on all friends
	 */
	private void getUsers() {
		String url = this.url;
		crawl(url + "/friends/");
		Elements linkOnPage = htmlDocument.getElementById("memberList").select("a[href]");
		for (Element e : linkOnPage) {
			System.out.println(e.absUrl("href"));
			this.links.add(e.absUrl("href"));
		}
		setUrl(url);
	}

	/**
	 * Get all games which user contain
	 */
	private void getGames() {
		try {
			String key = "var rgChangingGames";
			crawl(url + "/games/?tab=all");
			// System.out.println(htmlDocument.body());

			String scriptElements = htmlDocument.getElementsByTag("script").last().toString().trim();
			scriptElements = scriptElements.substring(0, scriptElements.lastIndexOf(key));
			scriptElements = scriptElements
					.substring(scriptElements.lastIndexOf("var rgGames"), scriptElements.length()).trim();
			scriptElements = scriptElements.substring(scriptElements.indexOf("{"), scriptElements.length() - 2);
			writer = new OutputStreamWriter(new FileOutputStream(new File("pers.txt"), true), "UTF-8");

			for (String game : parseGamesFromJSON(scriptElements)) {
				JSONObject object = new JSONObject(game);
				writer.append("Game: " + object.get("name")).append(System.lineSeparator());
				if (!object.isNull("hours_forever")) {
					writer.append("Time_played: " + object.get("hours_forever")).append(System.lineSeparator());
				}
			}
			writer.append(System.lineSeparator());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Performs a search on the body of on the HTML document that is retrieved. This
	 * method should only be called after a successful crawl.
	 * 
	 * @param searchWord
	 *            - The word or string to look for
	 * @return whether or not the word was found
	 */
	public boolean searchForWord(String searchWord) {
		// Defensive coding. This method should only be used after a successful crawl.
		if (this.htmlDocument == null) {
			System.out.println("ERROR! Call crawl() before performing analysis on the document");
			return false;
		}

		System.out.println("Searching for the word " + searchWord + "...");
		parseUserInformations();
		getUsers();
		getGames();
		return false;

	}

	public List<String> getLinks() {
		return this.links;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}