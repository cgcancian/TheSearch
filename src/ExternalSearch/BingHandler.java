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
public class BingHandler extends ExternalSearchEngine{

    public BingHandler(String EngineURL) {
        super(EngineURL);
    }

    @Override
    public String getSearchSuggestion(String SearchQuery) {
        String sug = "";
        String[] aux;
        String URL = formatSearchQuery(this.EngineURL,SearchQuery);
        
        try {
            Connection c = Jsoup.connect(URL);
            Document d = c.get();
            if ((d.select("div#sp_requery").first() == null) || !(d.select("div#sp_requery").first().hasText()) || (d.select("div#sp_requery").first().text() == "") || (d.select("div#sp_requery").first().text() == null))
                return "No suggestions from Bing";
            Element link = d.select("div#sp_requery").first();
            aux = link.text().split("[^\\p{L}0-9']+");
            for (int i = 2; i < aux.length - 2; i++) {
                sug += aux[i] + " ";
            }
            sug += aux[aux.length - 2];
        } catch (IOException e) {
            System.out.println("Failed to connect to bing");
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
            if ((d.select("span.sb_count").first() == null) || !(d.select("span.sb_count").first().hasText()) || (d.select("span.sb_count").first().text() == "") || (d.select("span.sb_count").first().text() == null))
                return stats;
            Element link = d.select("span.sb_count").first();
            words = link.text().split("\\s+");
            
            for (int i = 0; i < words.length - 1; i++) {
                stats += words[i];
            }
                  
        } catch (IOException e) {
            System.out.println("Failed to connect to google");
        }
        
        return stats;
    }
    
    
}