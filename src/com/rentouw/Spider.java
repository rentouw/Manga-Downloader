package com.rentouw;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider {
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<List<String>> imagesPerUrl = new LinkedList<>();

    /**
     * Our main launching point for the Spider's functionality. Internally it creates spider legs
     * that make an HTTP request and parse the response (the web page).
     *
     * @param url - The starting point of the spider
     * @return
     */
    public Set<String> search(String url) {
        String currentUrl = "null";
        SpiderLeg leg = null;
        while (!currentUrl.equals("http://google.com")) {
            leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
            this.pagesToVisit.addAll(leg.getLinks());
            this.imagesPerUrl.add(leg.getImgs());
        }
        return pagesVisited;
    }


    /**
     * Returns the next URL to visit (in the order that they were found). We also do a check to make
     * sure this method doesn't return a URL that has already been visited.
     *
     * @return
     */
    private String nextUrl() {
        String nextUrl;
        if (this.pagesToVisit.size() > 1) {
            do {
                nextUrl = this.pagesToVisit.remove(0);
            } while (this.pagesVisited.contains(nextUrl));
            this.pagesVisited.add(nextUrl);
        } else {
            nextUrl = "http://google.com";
        }
        return nextUrl;
    }

    public List<List<String>> getImagesPerUrl() {
        return imagesPerUrl;
    }

}
