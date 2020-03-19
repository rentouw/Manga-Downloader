package com.rentouw;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class Download extends Thread {
  private final String threadName;
  private final String[] mangaDetails;
  private static ArrayList<String[]> JSONArrayParsed = new ArrayList<>();
  private Thread t;

  /**
   * @param mangaDetails List of the manga's details. (chapters,download,url)
   * @param name Name of the thread.
   * @param jsonArrayParsed Array met al de JSONArray info.
   */
  Download(String[] mangaDetails, String name, ArrayList<String[]> jsonArrayParsed) {
    this.threadName = "downloader-" + name;
    this.mangaDetails = mangaDetails;
    JSONArrayParsed = jsonArrayParsed;
  }

  /**
   * Test if a url works. <br>
   * Not if its is a valid manga url.
   *
   * @param url String url to test.
   * @return boolean for if url works.
   */
  public static boolean testUrl(String url) {
    try {
      Spider spider = new Spider();
      spider.search(url);
      return true;
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return false;
    }
  }

  /**
   * Download the full manga.
   *
   * @param imgs A list with lists of all the links to the images.
   * @param name name of the manga.
   */
  private static void DownloadManga(List<List<String>> imgs, String name) {
    int i = 0;
    String imgurl;
    String map_prefix = "00";
    for (List<String> imgLinks : imgs) {
      if (i >= 10 && i < 100) map_prefix = "0";
      else if (i >= 100) map_prefix = "";
      FileHandler.makePath(FileHandler.getRootFolder() + name + "/" + map_prefix + i);
      String prefix = "00";
      for (int j = 0; j < imgLinks.size(); j++) {
        if (j >= 10 && j < 100) prefix = "0";
        else if (j >= 100) prefix = "";
        imgurl = imgLinks.get(j);
        // added root folder to check
        String file = FileHandler.getRootFolder() + name + "/" + map_prefix + i + "/" + prefix + j;
        String oldFile = FileHandler.getRootFolder() + name + "/" + map_prefix + i + "/" + j;
        String cbzFolder =
            FileHandler.getRootFolder() + name + "_cbz/" + name + "_" + map_prefix + i + ".cbz";
        if (!FileHandler.checkFile(cbzFolder)) {
          if (fileCheckFull(file)) {
            try {
              if (fileCheckFull(file, oldFile)) {
                DownloadImage(imgurl, file);
                Convert.checkJPG(file);
              } else {
                new File(oldFile + ".jpg").renameTo(new File(file + ".jpg"));
                new File(oldFile + ".png").renameTo(new File(file + ".png"));
              }
            } catch (Exception ex) {
              System.out.println("Could not download " + file + " on url " + imgurl);
              ex.getCause();
            }
          }
        }
      }
      i++;
    }
    Convert.convert(name, JSONArrayParsed);
    Utils.recursiveDelete(new File(FileHandler.getRootFolder() + name));
  }

  /**
   * Check if file exits.
   *
   * @param file Location of the file.
   * @return boolean if file exists.
   */
  private static boolean fileCheckFull(String file) {
    return fileCheckFull(file, "null");
  }

  /**
   * Check if file exits.
   *
   * @param file Location of the file.
   * @param oldFile Location of the old file.
   * @return boolean if file is same as old file.
   */
  private static boolean fileCheckFull(String file, String oldFile) {
    if (oldFile != null)
      return !FileHandler.checkFile(file)
          && !FileHandler.checkFile(oldFile + ".jpg")
          && !FileHandler.checkFile(oldFile + ".png");
    else
      return !FileHandler.checkFile(file)
          && !FileHandler.checkFile(file + ".jpg")
          && !FileHandler.checkFile(file + ".png");
  }

  /**
   * Download a image.
   *
   * @param urlImg url of the image.
   * @param path path where to save the image.
   */
  private static void DownloadImage(String urlImg, String path) {
    // This will get input data from the server
    InputStream inputStream = null;

    // This will read the data from the server;
    OutputStream outputStream = null;

    try {
      // This will open a socket from client to server
      URL url = new URL(urlImg);

      // This user agent is for if the server wants real humans to visit
      String USER_AGENT =
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

      boolean notDone = true;
      while (notDone) {
        // This socket type will allow to set user_agent
        URLConnection con = url.openConnection();

        // Setting the user agent
        con.setRequestProperty("User-Agent", USER_AGENT);

        // Requesting input data from server
        inputStream = con.getInputStream();

        System.out.println("Write to " + path);
        // Open local file writer
        outputStream = new FileOutputStream(path);

        // Limiting byte written to file per loop
        byte[] buffer = new byte[2048];

        // Increments file size
        int length;

        // Looping until server finishes
        // and write to the file.
        while ((length = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, length);

        if (Utils.testPath(path)) notDone = false;
      }

    } catch (Exception ex) {
      System.out.println(
          "Error downloading img on url "
              + urlImg
              + " to path "
              + path
              + "\nError = "
              + ex.getMessage());
    }

    // closing used resources
    // The computer will not be able to use the image
    // This is a must

    try {
      Objects.requireNonNull(outputStream).close();
      Objects.requireNonNull(inputStream).close();
    } catch (IOException e) {
      System.out.println("Error=" + e.getMessage());
    }
  }

  /** Run function for thread. */
  public void run() {
    if (mangaDetails != null) {
      String mangaName = mangaDetails[0];
      String url = mangaDetails[1];
      List<String> chapterUrls = this.allChaptersUrls(url);
      Collections.reverse(chapterUrls);
      boolean downloadBool = Boolean.parseBoolean(mangaDetails[2]);
      int oldChapter = FileHandler.readChapter(mangaName, JSONArrayParsed);
      int newChapter = chapterUrls.size();
      if (chapterUrls.size() == 0) System.out.println("url=" + url);
      if (oldChapter != newChapter) {
        FileHandler.writeChapter(mangaName, newChapter, JSONArrayParsed);
        if (downloadBool) {
          boolean download = false;
          String map_prefix = "00";
          for (int i = 0; i < newChapter; i++) {
            if (i >= 10 && i < 100) map_prefix = "0";
            else if (i >= 100) map_prefix = "";
            if (!FileHandler.checkFile(
                FileHandler.getRootFolder()
                    + mangaName
                    + "_cbz/"
                    + mangaName
                    + "_"
                    + map_prefix
                    + i
                    + ".cbz")) {
              download = true;
              break;
            }
          }
          if (download) {
            List<List<String>> imgs = new ArrayList<>();
            Spider spider = new Spider();
            if (chapterUrls.size() == 0) System.out.println("url=" + url);
            for (String chapterUrl : chapterUrls) {
              spider.search(chapterUrl);
              imgs.add(spider.getImagesPerUrl());
            }
            DownloadManga(imgs, mangaName);
          }
        }
      }
      System.out.println("\tDone downloading " + mangaName);
    }
  }

  /** Start function for thread. */
  public void start() {
    if (t == null) {
      t = new Thread(this, threadName);
      t.start();
    }
  }

  /**
   * Give the amount of chapters back.
   *
   * @param url String url for the manga.
   * @return amount of chapters.
   */
  private List<String> allChaptersUrls(String url) {
    Spider spider = new Spider();
    spider.search(url);
    return spider.getChapterLinks();
  }
}
