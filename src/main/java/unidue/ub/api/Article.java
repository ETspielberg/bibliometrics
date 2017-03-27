/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unidue.ub.api;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Martin Grunwald
 */
public class Article implements Comparable<Article>{

    private String title = "";
    private List<String> authors = new LinkedList<String>();
    private int positionInFile;
    private final int LEVENTHRESHOLD = 100;
    private int levDistance = 100;

    public int getPositionInFile() {
        return positionInFile;
    }

    public void setPositionInFile(int positionInFile) {
        this.positionInFile = positionInFile;
    }

    public int getLevDistance() {
        return levDistance;
    }
    
    public void calcLevDist(Article a) {
        // Autoren, Titel + ? vergleichen
        List<String> compAuthors = a.getAuthors();
        int numberOfAuthors = authors.size();
        if(numberOfAuthors != compAuthors.size()) {
            levDistance = LEVENTHRESHOLD;
            return;
        }
        int tempAuthorLeven = 0;
        for(int i = 0; i < numberOfAuthors; i++) {
            String author = authors.get(i);
            String compAuthor = compAuthors.get(i);
            tempAuthorLeven += levenshteinDistance(author, compAuthor);
        }
        double authorLeven = tempAuthorLeven / numberOfAuthors;
        int titleLeven = levenshteinDistance(this.title, a.title);
        
        // levDistance auf den Mittelwert setzen
        double totalLeven = authorLeven + titleLeven;
        levDistance = (int) Math.round(totalLeven);
        
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
        Collections.sort(authors);
    }
    
    public void addAuthor(String author) {
        authors.add(author);
        Collections.sort(authors);
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String Title) {
        this.title = Title;
    }
    
    private int levenshteinDistance(String s1, String s2) {
        return StringUtils.getLevenshteinDistance(s1, s2);
    }

    @Override
    public int compareTo(Article a) {
        return levDistance - a.getLevDistance();
    }

}
