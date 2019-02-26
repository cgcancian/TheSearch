/*
 * Project: The Search - IN204
 * ENSTA ParisTech - Mars 2018
 * Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * Version 1.0
 */
package querycorrection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Norvig extends QuerySuggestion {
    private final HashMap<String, Integer> nWords = new HashMap();                        

    public Norvig(String file) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            Pattern p = Pattern.compile("\\w+");
            for(String temp = ""; temp != null; temp = in.readLine()){
                Matcher m = p.matcher(temp.toLowerCase());
                while(m.find()) nWords.put((temp = m.group()), nWords.containsKey(temp) ? nWords.get(temp) + 1 : 1);
            }
        }
    }

    @Override
    public final HashSet<String> Edits(String word, int editDistance, HashSet<String> result) {		
            for(int i=0; i < word.length(); ++i) result.add(word.substring(0, i) + word.substring(i+1));
            for(int i=0; i < word.length()-1; ++i) result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
            for(int i=0; i < word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
            for(int i=0; i <= word.length(); ++i) for(char c='a'; c <= 'z'; ++c) result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
            return result;
    }

    @Override
    public final List<List<QuerySuggestion.suggestItem>> Correct(String[] query, String language) {
        List<List<QuerySuggestion.suggestItem>> suggestions = new ArrayList<>();            
        for (String m : query) {                
            String word = m.toLowerCase();

            HashSet<String> edits = Edits(word, 2, new HashSet<>());
            if(nWords.containsKey(word)) 
                edits.add(word);                                                              

            List<QuerySuggestion.suggestItem> candidates = new ArrayList<>();

            for(String s : edits) {
                if(nWords.containsKey(s) && nWords.get(s) != null){
                    QuerySuggestion.suggestItem sug = new QuerySuggestion.suggestItem();
                    sug.term = s;
                    sug.count = nWords.get(s);
                    candidates.add(sug);
                }
            }
            if(MAXDISTANCE == 2){                    
                for(String s : edits) {
                    for(String w : Edits(s, 2, new HashSet<>())) {
                        if(nWords.containsKey(w) && nWords.get(s) != null){
                            QuerySuggestion.suggestItem sug = new QuerySuggestion.suggestItem();                                
                            sug.term = w;
                            sug.count = nWords.get(s);
                            if(!candidates.contains(sug)){                                
                                candidates.add(sug);
                            }
                        }
                    }
                }
            }
            suggestions.add(candidates);
        }          			
        return suggestions;
    }
}