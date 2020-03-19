package com.rentouw;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class SpiderLeg {
  // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
  private static final String USER_AGENT =
      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
  private final List<String> links = new LinkedList<>();
  private final List<String> imgs = new LinkedList<>();

  /**
   * This performs all the work. It makes an HTTP request, checks the response, and then gathers up
   * all the links on the page. Perform a searchForWord after the successful crawl
   *
   * @param url - The URL to visit
   */
  public void crawl(String url) {
    try {
      Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
      Document htmlDocument = connection.get();
      if (!connection.response().contentType().contains("text/html")) {
        return;
      }
      Elements linksOnPage = htmlDocument.select("a[href]");
      Elements imgsOnPage = htmlDocument.getElementsByAttribute("title").select("img");
      for (Element img : imgsOnPage) {
        if (img.parent().hasClass("container-chapter-reader")
            || img.parent().hasClass("vung-doc")) {
          if (img.attr("src") != null) {
            imgs.add(img.attr("src"));
          } else {
            imgs.add(img.attr("data-src"));
          }
        }
      }
      for (Element link : linksOnPage) {
        if (link.parents().hasClass("panel-story-chapter-list") || link.parents().hasClass("chapter-list")) {
          this.links.add(link.absUrl("href"));
        }
      }
    } catch (IOException ioe) {
      // We were not successful in our HTTP request
    }
  }

  public List<String> getLinks() {
    return this.links;
  }

  public List<String> getImgs() {
    return this.imgs;
  }
}
