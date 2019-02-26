/*
 * Project: The Search - IN204
 * ENSTA ParisTech - Mars 2018
 * Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * Version 1.0
 */
package querycorrection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * @version 1.0
 */
public abstract class QuerySuggestion {        
    /**
     * Criterion stablished to different Levenshtein distances
     */
    private static final double CRITERION[] = {0.95, 0.04, 0.01};
    protected int MAXDISTANCE = 1;
    protected HashMap<Ngram,Double> ngramDict;

    public void setMAXDISTANCE(int MAXDISTANCE) {
        this.MAXDISTANCE = MAXDISTANCE;
    }
    

    public void setNgramDict(HashMap<Ngram, Double> ngramDict) {
        this.ngramDict = ngramDict;
    }
    
    /**
     * suggestItem with its frequency in Unigram and its word
     */
    public static class suggestItem {
        public String term = "";
        public int distance = 0;
        public int count = 0;

        @Override
        public boolean equals(Object obj)
        {
            return term.equals(((suggestItem)obj).term);
        }
        
        @Override
        public int hashCode()
        {
            return term.hashCode();
        }       
    }    
    
    /**
     * Abstract methods implemented by classes Norvig and SymSpell
     * @param word original word from query
     * @param editDistance maximal distance for edition
     * @param suggestions real and non-real words to be validated
     * @return HashSet with real words
     */    
    public abstract HashSet<String> Edits(String word, int editDistance, HashSet<String> suggestions);    
    public abstract List<List<suggestItem>> Correct(String[] query, String language);
    
    /**
     * This method creates the HashMap with the Ngrams and their frequencies
     * @param n Ngram order
     * @param file file name with extension
     * @return HashMap with a Ngram dictionary
     * @throws java.io.IOException
     */       
    public HashMap<Ngram,Double> CreateNgramDict(int n, String file) throws IOException{
        Ngram mygram = new Ngram(n);                                
        System.out.println("Creating Ngram...");               
        HashMap<Ngram,Double> trigram = mygram.loadFromFile("w2.txt");        
        System.out.println("Ngram created!");            
        return trigram;
    }
    
    /**
     * This method reads a query from the keyboard
     * @throws java.io.IOException
     */    
    public void ReadFromStdIn() throws IOException{              
        String query;                              
        
        
        ngramDict = CreateNgramDict(2, "w2.txt");
        
        BufferedReader br =  new BufferedReader(new InputStreamReader(System.in));        
        System.out.println("Insert your query... ('exit' to stop)");     
        try {
            while ((query = br.readLine())!=null)
            {         
                if (query.equals("exit"))
                    break;
                
                String finalstring = CorrectedQuery(query);                                                                                                      
                System.out.println("Insert your query... ('exit' to stop)");
            }
        } catch (IOException e) {}        
    }
    
    /**
     * This method returns a list of lists with all the possible Ngrams with the current query
     * @param list suggestions list from Norvig or SymsSpell algorithms
     * @param dict_ngram Ngram dictionary
     * @return list of lists of Ngrams for every N words in the original query
     */
        
    public List<List<Ngram>> SentenceCandidates (List<List<suggestItem>> list, HashMap<Ngram,Double> dict_ngram){   
        List<List<Ngram>> possibilities = new ArrayList();
        Ngram possiblegram;
        Ngram bigram;
        Ngram buggram = null;
        suggestItem best = new suggestItem();
        List<Ngram> lastgrams = null;                   
        
        System.out.println("");
             
        for(int i = 0; i < list.size()-1; i++){           
            List<Ngram> gramlist = new ArrayList();                        
            for(suggestItem sug1 : list.get(i)){
                for(suggestItem sug2 : list.get(i+1)){                    
                    ArrayList<String> merge = 
                            new ArrayList<>(Arrays.asList(sug1.term, sug2.term));                                                                       
                    bigram = new Ngram(0, 2, merge);     
                    
                    if(sug2.count > best.count){
                        buggram = bigram;
                        best = sug2;
                    }

                    if(dict_ngram.get(bigram) != null){                               
                        if(dict_ngram.get(bigram) > 0){
                            possiblegram = bigram;  
                            possiblegram.setFreq(dict_ngram.get(bigram));                                                         

                            if(lastgrams != null){                                 
                                for(Ngram n : lastgrams){
                                    if(Coherent(possiblegram, n)){                                          
                                        gramlist.add(possiblegram); 
                                        break;                                  
                                    }
                                }                                
                            }
                            else gramlist.add(possiblegram);                                                            
                        }
                    }                                               
                }
            }            
            possibilities.add(gramlist);
            lastgrams = possibilities.get(i);              
        }          
        return possibilities;
    }   

    /**
     * This method finds the list of the most probable Ngrams in relation to the current query
     * @param candidates list of lists of Ngrams for every N words in the original query
     * @param words original query
     * @return list of the most probable Ngrams
     */   
    public List<Ngram> FindOriginalSentence (List<List<Ngram>> candidates, String[] words){       
        for(List<Ngram> list : candidates){
            for(Ngram n : list){                       
                if(candidates.indexOf(list) == 0){
                    ReadaptFreq(n, words[0], 0);
                    ReadaptFreq(n, words[1], 1);
                }                              
                else ReadaptFreq(n, words[candidates.indexOf(list)+1], 1);              
            }
        }
        
//        System.out.println("Readaptados:");
//        ShowListofListsNgrams(candidates);
        
        List<Ngram> finalList = new ArrayList();
        Ngram lastadded = null;
        
        for(List<Ngram> list : candidates){
            if(candidates.indexOf(list) == 0) lastadded = FindBestFreq(list, null);                
            
            else if (candidates.indexOf(list) < candidates.size() - 1){
                while( FindBestFreq (candidates.get(candidates.indexOf(list) + 1), FindBestFreq(list, lastadded)) == null ){
                    if(!list.isEmpty() && list.indexOf(FindBestFreq(list, lastadded)) != -1) list.remove(list.indexOf(FindBestFreq(list, lastadded)));                
                    else break;
                }
                lastadded = FindBestFreq(list, lastadded);
            }
           
            else lastadded = FindBestFreq(list, lastadded);                
                       
            finalList.add(lastadded);
        }         
        
        System.out.println("Lista Final");
        ShowListNgrams(finalList);
        
        return finalList;
    }
    
    
    /**
     * This method returns the most probable Ngram from a list of Ngrams, evaluated by his frequency and the previous Ngram
     * @param list Ngram list to be evaluated
     * @param key previous Ngram
     * @return best Ngram in the list
     */   
        
    public Ngram FindBestFreq(List<Ngram> list, Ngram key){
        double freq = 0.0;
        Ngram best = null;
        for(Ngram n : list)
            if(n.getFreq() > freq && Coherent(n, key)){
                best = n;
                freq = best.getFreq();
            }       
        return best;
    }
    
    /**
     * This method changes the frequency of a Ngram according to its Levenshtein distance from the original word
     * @param n Ngram to have its frequency changed
     * @param a original string
     * @param i position in the Ngram
     */    
    public void ReadaptFreq (Ngram n, String a, int i){
        n.setFreq(n.getFreq()*CRITERION[DamerauLevenshteinDistance(n.getWords().get(i), a)]);      
    }
    
    /**
     * This method returns a new string with the most probable corrected query
     * @param query original query from user
     * @return string with final corrected query
     */    
    public String CorrectedQuery (String query){
        String finalcorrection = "";
        String[] words = query.split(" ");
        List<List<suggestItem>> sentence_sug = Correct(words, "");        
        if(words.length != 1){            
            List<List<Ngram>> sentence_bigram = SentenceCandidates(sentence_sug, ngramDict);                        
            List<Ngram> list = FindOriginalSentence(sentence_bigram, words);
            int cont = 0;

            for(Ngram n : list){
                if(n == null) {
                    cont++;
                    finalcorrection += " " + words[list.indexOf(n)+cont];
                }                                               

                else{
                    if(list.indexOf(n) == 0) finalcorrection += n.getWords().get(0) + " " + n.getWords().get(1);
                    else finalcorrection += " " + n.getWords().get(1);
                }
            }
			
			if (cont == list.size()) finalcorrection = query;
        } else {
            suggestItem best = new suggestItem();
            for(suggestItem sug : sentence_sug.get(0))
                if(sug.count > best.count) best = sug;       
            finalcorrection += best.term;
            if (finalcorrection.equals("")) finalcorrection = query;
        }
        
        System.out.println("String final: ");
        System.out.println(finalcorrection);
        
        return finalcorrection;            
    }
    
    /**
     * This method verifies whether a Ngram is coherent to its previous Ngram (if one's last word is equal to last's first word)
     * @param newgram new Ngram to be added
     * @param lastgram previous Ngram in the sentence
     * @return 
     */   
    public boolean Coherent (Ngram newgram, Ngram lastgram){
        if(lastgram == null) return true;                            
        return newgram.getWords().get(0).compareTo(lastgram.getWords().get(1))==0;
    }
    
    /**
     * This method displays a list of lists of sugggested items
     * @param sentence_sug
     */    
    public void ShowSuggestions(List<List<suggestItem>> sentence_sug){
        for(List<suggestItem> list : sentence_sug)
            for(suggestItem s : list)
                System.out.println(s.term + " " + s.distance);              
    }
    
    /**
     * This method displays a list of lists of Ngrams
     * @param biglist
     */    
    public void ShowListofListsNgrams(List<List<Ngram>> biglist){
        for(List<Ngram> list : biglist){
            for(Ngram n : list)
                System.out.println(n.getWords() + " " + n.getFreq());
            System.out.println();
        }        
    }   
    
    /**
     * This method displays a list of Ngrams
     * @param list
     */
    public void ShowListNgrams(List<Ngram> list){        
        for(Ngram n : list)
            if(n != null)
                System.out.println(n.getWords() + " " + n.getFreq());
        System.out.println();            
    }   
    
    /**
     * This method calculates the Levenshtein distance between two strings
     * @param a first string
     * @param b second string
     * @return 
     */    

    public static int DamerauLevenshteinDistance(String a, String b) {
    final int inf = a.length() + b.length() + 1;
        int[][] H = new int[a.length() + 2][b.length() + 2];
        for (int i = 0; i <= a.length(); i++) {
            H[i + 1][1] = i;
            H[i + 1][0] = inf;
        }
        for (int j = 0; j <= b.length(); j++) {
            H[1][j + 1] = j;
            H[0][j + 1] = inf;
        }
        HashMap<Character, Integer> DA = new HashMap<>();
        for (int d = 0; d < a.length(); d++) 
            if (!DA.containsKey(a.charAt(d)))
                DA.put(a.charAt(d), 0);


        for (int d = 0; d < b.length(); d++) 
            if (!DA.containsKey(b.charAt(d)))
            DA.put(b.charAt(d), 0);

        for (int i = 1; i <= a.length(); i++) {
            int DB = 0;
            for (int j = 1; j <= b.length(); j++) {
            final int i1 = DA.get(b.charAt(j - 1));
            final int j1 = DB;
            int d = 1;
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                d = 0;
                DB = j;
            } 
            H[i + 1][j + 1] = min(
            H[i][j] + d, 
            H[i + 1][j] + 1,
            H[i][j + 1] + 1, 
            H[i1][j1] + ((i - i1 - 1)) 
            + 1 + ((j - j1 - 1)));
        }
            DA.put(a.charAt(i - 1), i);
        }
        return H[a.length() + 1][b.length() + 1];
    }
    
    /**
     * This method returns the min value between four given values
     * @param a
     * @param b
     * @param c
     * @param d
     * @return 
     */    


    public static int min(int a, int b, int c, int d) {
        return Math.min(a, Math.min(b, Math.min(c, d)));
    }    
}
