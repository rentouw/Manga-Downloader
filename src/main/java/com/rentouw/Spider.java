package com.rentouw;

import java.util.LinkedList;
import java.util.List;

class Spider {
  private List<String> chapterLinks = new LinkedList<>();
  private List<String> imagesPerUrl = new LinkedList<>();

  public List<String> getChapterLinks() {
    return chapterLinks;
  }

  public List<String> getImagesPerUrl() {
    return imagesPerUrl;
  }

  /**
   * Our main launching point for the Spider's functionality. Internally it creates spider legs that
   * make an HTTP request and parse the response (the web page).
   *
   * @param url - The starting point of the spider
   */
  public void search(String url) {
    this.chapterLinks = new LinkedList<>();
    this.imagesPerUrl = new LinkedList<>();
    SpiderLeg leg;
    leg = new SpiderLeg();
    leg.crawl(url); // Lots of stuff happening here. Look at the crawl method in
    this.chapterLinks.addAll(leg.getLinks());
    this.imagesPerUrl.addAll(leg.getImgs());
  }
}
