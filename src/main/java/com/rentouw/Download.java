package com.rentouw;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

class Download extends Thread {
  private final String threadName;
  private final String[] mangaList;
  private Thread t;

  Download(String[] mangaList, String name) {
    threadName = "downloader-" + name;
    this.mangaList = mangaList;
  }

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

  /** @param name name of the manga. */
  private static void DownloadManga(Spider spider, String name) {
    List<List<String>> imgs = spider.getImagesPerUrl();
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
                //                System.out.println("Converted file: " + oldFile);
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
    Convert.convert(name);
    Utils.recursiveDelete(new File(FileHandler.getRootFolder() + name));
  }

  private static boolean fileCheckFull(String file) {
    return fileCheckFull(file, "null");
  }

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

  private static void DownloadImage(String urlImg, String path) {
    // Add root folder to path
    // path = FileHandler.getRootFolder() + path;

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
        while ((length = inputStream.read(buffer)) != -1) {
          // Writing data
          outputStream.write(buffer, 0, length);
        }

        if (Utils.testPath(path)) {
          notDone = false;
        }
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

  public void run() {
    Spider spider = new Spider();
    if (mangaList != null) {
      String mangaName = mangaList[0];
      String url = mangaList[1];
      boolean downloadBool = Boolean.parseBoolean(mangaList[2]);
      int oldChapter = FileHandler.readChapter(mangaName);
      int newChapter = this.allChapters(url, spider);
      if (oldChapter != newChapter) {
        FileHandler.writeChapter(mangaName, newChapter);
      }
      if (downloadBool) {
        DownloadManga(spider, mangaName);
      }
      System.out.println("\tDone downloading " + mangaName);
    }
  }

  public void start() {
    if (t == null) {
      t = new Thread(this, threadName);
      t.start();
    }
  }

  private int allChapters(String url, Spider spider) {
    return spider.search(url).size();
  }
}
