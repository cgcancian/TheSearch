/*
 * Project: The Search - IN204
 * ENSTA ParisTech - Mars 2018
 * Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * Version 1.0
 */
package ExternalSearch;

/**
 *
 * @author Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * @version 1.0
 */
public abstract class ExternalSearchEngine {
    
    protected String EngineURL;
    
    /**
     * Constructor
     * @param EngineURL 
     */
    public ExternalSearchEngine(String EngineURL) {
        this.EngineURL = EngineURL;
    }
    
    /**
     * Getter
     * @return String
     */
    public String getEngineURL() {
        return EngineURL;
    }

    /**
     * Setter
     * @param EngineURL 
     */
    public void setEngineURL(String EngineURL) {
        this.EngineURL = EngineURL;
    }
    
    /**
     * Concatenates the SearchQuery and the EngineURL to generate a valid web search request  
     * @param EngineURL
     * @param SearchQuery
     * @return String corresponding to the correctly formated URL
     */
    protected String formatSearchQuery(String EngineURL, String SearchQuery) {
       
        String formatedURL;
        formatedURL = EngineURL + "/search?q=";
        
        String[] words = SearchQuery.split("[^\\p{L}0-9']+");
        
        formatedURL += words[0];
        for (int i = 1; i < words.length; i++) {
            formatedURL += "+" + words[i];
        }
        
        formatedURL += "&nfpr=1"; // Prevents google from auto-correcting the search query. 
        
        return formatedURL;
    }
    
    /**
     * Sends SearchQuery to search engine and verifies if there is any correction suggestion; should be
     * implemented in the derived classes, respecting each search engine.
     * 
     * @param SearchQuery
     * @return String with the external search engine suggestions
     */
    public abstract String getSearchSuggestion(String SearchQuery);
    
    /**
     * Sends SearchQuery to search engine and gets the number of results found; should be
     * implemented in the derived classes, respecting each search engine.
     * 
     * @param SearchQuery
     * @return String with the number of results 
     */
    public abstract String getSearchStats(String SearchQuery);
        
}
