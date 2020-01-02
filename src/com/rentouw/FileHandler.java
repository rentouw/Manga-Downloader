package com.rentouw;

import java.io.*;

class FileHandler {
  private static final String rootFolder = "./";
  private static final String mangaList = rootFolder + "manga.list";
  private static final String chapterList = rootFolder + "log.txt";

  public FileHandler() {
    if (!checkFile(mangaList)) {
      File f = new File(mangaList);
      try {
        f.createNewFile();
      } catch (IOException e) {
        System.out.println("Error=" + e.getMessage());
      }
    }
    if (!checkFile(chapterList)) {
      File f = new File(chapterList);
      try {
        f.createNewFile();
      } catch (IOException e) {
        System.out.println("Error=" + e.getMessage());
      }
    }
  }

  public static String getRootFolder() {
    return rootFolder;
  }

  public static String getMangaList() {
    return mangaList;
  }

  public static String getChapterList() {
    return chapterList;
  }

  public static boolean checkFile(String path) {
    return new File(path).exists();
  }

  /**
   * Read the full file and return every line via a String array.
   *
   * @param fileSelecter True = mangaList False = chapterList
   * @return String array with every entry a line in the file
   */
  public String[] getFile(boolean fileSelecter) {
    // This will reference one line at a time
    String line;
    String[] output = new String[100];
    try {
      FileReader fileReader;
      // Assume default encoding.
      if (fileSelecter) {
        fileReader = new FileReader(mangaList);
      } else {
        fileReader = new FileReader(chapterList);
      }
      // Always wrap FileReader in BufferedReader.
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      int i = 0;
      while ((line = bufferedReader.readLine()) != null) {
        output[i] = line;
        i++;
      }

      // Always close files.
      bufferedReader.close();
    } catch (FileNotFoundException ex) {
      if (fileSelecter) {
        System.out.println("Unable to open file '" + mangaList + "'");
      } else {
        System.out.println("Unable to open file '" + chapterList + "'");
      }
    } catch (IOException ex) {
      if (fileSelecter) {
        System.out.println("Error reading file '" + mangaList + "'");
      } else {
        System.out.println("Error reading file '" + chapterList + "'");
      }
    }

    return output;
  }

  public void writeManga(String manga) {
    String[] listManga = manga.split("([$])");
    writeManga(listManga[0], listManga[1], Boolean.parseBoolean(listManga[2]));
  }

  public void writeManga(String name, String url, boolean bool) {
    try {

      String[] array = getFile(true);

      // Assume default encoding.
      FileWriter fileWriter = new FileWriter(mangaList);

      // Always wrap FileWriter in BufferedWriter.
      BufferedWriter out = new BufferedWriter(fileWriter);

      // Note that write() does not automatically
      // append a newline character.
      for (String line : array) {
        if (line != null) {
          out.write(line);
          out.newLine();
        }
      }
      out.append(name);
      out.append("$");
      out.append(url);
      out.append("$");
      out.append(String.valueOf(bool));
      out.newLine();

      // Always close files.
      out.close();
    } catch (IOException ex) {
      System.out.println("Error writing to file '" + mangaList + "'");
    }
  }

  public void writeChapter(String chapter) {
    String[] list = chapter.split("([$])");
    writeChapter(list[0], Integer.parseInt(list[1]));
  }

  public void writeChapter(String name, int chapter) {
    try {
      String[] array = getFile(false);

      // Assume default encoding.
      FileWriter fileWriter = new FileWriter(chapterList);

      // Always wrap FileWriter in BufferedWriter.
      BufferedWriter out = new BufferedWriter(fileWriter);

      for (String line : array) {
        if (line != null) {
          if (!line.split("([$])")[0].equals(name)) {
            out.write(line);
            out.newLine();
          }
        }
      }

      out.append(name);
      out.append("$");
      out.append(String.valueOf(chapter));
      out.newLine();

      // Always close files.
      out.close();
    } catch (IOException ex) {
      System.out.println("Error writing to file '" + chapterList + "'");
    }
  }

  public int readChapter(String name) {
    String[] logList = this.getFile(false);
    int chapter = 0;
    try {
      for (String log : logList) {
        if (log != null) {
          String[] logArray = log.split("([$])");
          if (logArray[0].equals(name)) {
            chapter = Integer.parseInt(logArray[1]);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("error=" + e.getMessage());
    }
    return chapter;
  }

  /**
   * Check if a url is already in the manga.list file.
   *
   * @param url Url that is going to be checked.
   * @return true if url is in the file.
   */
  public boolean checkByUrl(String url) {
    boolean output = false;
    String[] list = this.getFile(true);
    for (String smallList : list) {
      if (smallList != null) {
        String[] array = smallList.split("([$])");
        if (array[1].equals(url)) {
          output = true;
        }
      }
    }
    return output;
  }
}
