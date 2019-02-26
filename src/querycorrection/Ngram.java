/*
 * Project: The Search - IN204
 * ENSTA ParisTech - Mars 2018
 * Authors: Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * Version 1.0
 */
package querycorrection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Caio GARCIA CANCIAN and Thales LOIOLA RAVELI
 * @version 1.0
 */
public class Ngram {
    final static Charset ENCODING = StandardCharsets.UTF_8;
    
    private double freq; // observation frequency of the ngram 
    private int order; // order n of the ngram 
    private ArrayList<String> words; // list of n words that forms the ngram

    /**
     * Constructors 
     */
    public Ngram(int order) {
        this.freq = 0.0;
        this.order = order;
        this.words = null;
    }
    
    public Ngram(double freq, int order, ArrayList<String> words) {
        this.freq = freq;
        this.order = order;
        this.words = words;
    }

    /**
     * Getters and Setters for the private fields 
     */
    public double getFreq() {
        return freq;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }
    
    
   /**
     * Overrides parent class equals() to compare ngrams
     *
     * @param obj
     * @return true if the objects are the same.
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Ngram) || (obj == null))
            return false;
        if (obj == this)
            return true;
        
        boolean result = true;
        for (int k =0; k<order; k++) {
            result = this.words.get(k).equals(((Ngram) obj).getWords().get(k)) && result;
        }
        return result;
    }

    /**
     * Overrides parent class hashCode(), so now its based on the N words forming each ngram. We decided to
     * hash a compound set of strings simply as the sum of their individual hashes.
     * @return the object's hash code.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (int k =0; k<order; k++) {
            hash += this.words.get(k).hashCode();
        }
        return hash;
    } 
    
    /**
     * Creates a ngram formated .txt file from a normal English text file. Useful method for
     * future implementations of the software, in which the user may change the language model.
     * 
     * @param src
     * @param dst
     * @throws IOException 
     */
    public void learnFromFile(String src, String dst) throws IOException {
        String current = new java.io.File( "." ).getCanonicalPath();
        current = current.replace("\\", "/");        
        Path path = Paths.get(current, src);
        BufferedReader br = null;
        
        HashMap<Ngram, Double> FreqMap = new HashMap<>();
        int NgramCount = 0;
        String[] LineWords;
        Ngram N;
        
        try {
            br = Files.newBufferedReader(path, ENCODING);
            String Line;
            
            while ((Line = br.readLine()) != null) {
                LineWords = Line.split("[^\\p{L}0-9']+");
               
                for (int i = 0; i < LineWords.length - (order-1); ++i) {
                    ArrayList<String> wordList = new ArrayList();
                    for (int j = i; j < i+(order); ++j) {
                        wordList.add(LineWords[j].toLowerCase());
                    }
                   
                    N = new Ngram(0.0, this.order,wordList);
                    
                    if (FreqMap.containsKey(N))
                        FreqMap.put(N, FreqMap.get(N) + 1.0);
                    else {
                        NgramCount++;
                        FreqMap.put(N, 1.0);
                    }
                }

            }
            
        } catch (IOException e) {
            System.out.println("Failed to learn from Ngram file");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                System.out.println("Failed to learn from Ngram file");
            }
        }


        BufferedWriter bw = null;        
        path = Paths.get(" ", dst);
        try{
            bw = Files.newBufferedWriter(path, ENCODING);
            String Line;

            Iterator it = FreqMap.entrySet().iterator();
            bw.write(Double.toString(NgramCount) + "\n");
            while (it.hasNext()){
                Map.Entry<Ngram, Double> pair = (Map.Entry)it.next();
                Line = pair.getValue().toString() + " ";
                for (int k = 0; k < this.order - 1; k++) {
                    Line += pair.getKey().getWords().get(k) + " ";
                }
                Line +=  pair.getKey().getWords().get(this.order - 1) + "\n";
                bw.write(Line);
            }
            
        }catch(IOException e){
            System.out.println("Failed to open the file");
        }finally {
            try{
                if (bw != null)
                    bw.close();
            }catch(IOException e){
                System.out.println("Failed to close the file");
            }
        }
    }
    
    /**
     * Loads the ngram formated .txt file into a HashMap that contains the list of words and its 
     * frequency for each ngram.
     * 
     * @param src
     * @return HashMap<Ngram,Double> 
     * @throws IOException 
     */
    public HashMap<Ngram,Double> loadFromFile(String src) throws IOException {        
        String current = new java.io.File( "." ).getCanonicalPath();
        current = current.replace("\\", "/");
        System.out.println("src = " + src + " current = " + current);
        Path path = Paths.get(current, src);        
        BufferedReader br = null;        
        
        String[] LineWords;
        Ngram N;
       
        HashMap<Ngram, Double> map = new HashMap<>();
        
        try {            
            br = Files.newBufferedReader(path, ENCODING);            
            String Line = null;
            Line = br.readLine();
            //NgramCount = Double.parseDouble(Line);
            
            while ((Line = br.readLine()) != null) {
                LineWords = Line.split("\\s");
                ArrayList<String> wordList = new ArrayList();
                for (int k = 1; k <= this.order; k++) {
                    wordList.add(LineWords[k]);
                }
                N = new Ngram(Double.parseDouble(LineWords[0]), this.order, wordList);
                map.put(N, N.getFreq());
            }

        } catch (IOException e) {
            System.out.println("Failed to load Ngram file");
            System.out.println(e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                System.out.println("Failed to close Ngram file");
            }
        }
        return map;
    }
}
