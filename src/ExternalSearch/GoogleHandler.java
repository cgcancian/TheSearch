/*
 * Project: The Search - IN204
 * ENSTA ParisTech - Mars 2018
 * Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * Version 1.0
 */
package ExternalSearch;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author cgcan
 */
public class GoogleHandler extends ExternalSearchEngine {

    public GoogleHandler(String EngineURL) {
        super(EngineURL);
    }
    
    @Override
    public String getSearchSuggestion(String SearchQuery) {
        String sug = "";
        String URL = formatSearchQuery(this.EngineURL,SearchQuery);
        
        try {
            Connection c = Jsoup.connect(URL);
            Document d = c.get();
            if ((d.select("a.spell").first() == null) || !(d.select("a.spell").first().hasText()) || (d.select("a.spell").first().text() == "") || (d.select("a.spell").first().text() == null))
                return "No suggestions from Google";
            Element link = d.select("a.spell").first();
            sug = link.text();
        } catch (IOException e) {
            System.out.println("Failed to connect to google");
        }
      
        return sug; 
    }

    @Override
    public String getSearchStats(String SearchQuery) {
        String stats = "";
        String URL = formatSearchQuery(this.EngineURL,SearchQuery);
        String[] words;
        
         try {
            Connection c = Jsoup.connect(URL);
            Document d = c.get();
            if ((d.select("div#resultStats").first() == null) || !(d.select("div#resultStats").first().hasText()) || (d.select("div#resultStats").first().text() == "") || (d.select("div#resultStats").first().text() == null))
                return stats;
            Element link = d.select("div#resultStats").first();
            words = link.text().split("\\s+");
            
            for (int i = 1; i < words.length - 3; i++) {
                stats += words[i];
            }
            //stats[1] = words[words.length - 2].split("\\(")[1];       
        } catch (IOException e) {
            System.out.println("Failed to connect to google");
        }
        
        return stats;
    }
    
}