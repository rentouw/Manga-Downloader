package com.rentouw;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int value;
    readConfig();
    while (true) {
      System.out.print(
              "        MANGA DOWNLOADER\n"
                      + "------------------------------------\n"
                      + "\t1. Download\n"
                      + "\t2. List\n"
                      + "\t3. Add\n"
                      + "\t4. Remove\n"
                      + "\t5. Options\n"
                      + "\t6. Exit\n"
                      + "select :");
      value = input.nextInt();
      switch (value) {
        case 1:
          System.out.println("        DOWNLOAD\n" + "------------------------------------");
          FileHandler handler = new FileHandler();
          String[] bigList = handler.getFile(FileHandler.getMangaList());
          String[] oldChapterList = handler.getFile(FileHandler.getChapterList());
          Download download = new Download("test", "init");
          int i = 1;
          ExecutorService executor = Executors.newFixedThreadPool(bigList.length);
          for (String list : bigList) {
            if (list != null) {
              download = new Download(list, Integer.toString(i));
              executor.execute(download);
              i += 1;
            }
          }
          executor.shutdown();
          System.out.println("Downloading output:");
          while (!executor.isTerminated()) {
          }

          for (String list : bigList) {
            if (list != null) {
              try {
                if (Boolean.parseBoolean(list.split("([$])")[2])) {
                  String[] listSplit = list.split("([$])");
                  Convert.convert(listSplit[0]);
                }
              } catch (Exception e) {
                System.out.println("error converter=" + e.getMessage());
              }
            }
          }

          String[] newChapterList = handler.getFile(FileHandler.getChapterList());
          Utils.show();
          System.out.println("\n");
          ArrayList<String> files = Utils.checkNewFiles();
          Utils.moveNewFiles(files);
          if (files.size() != 0) {
            System.out.println("These files new:");
            for (String out : files) {
              System.out.println("\t" + out);
            }
            System.out.println("Also available in the newFiles folder.");
          }
          Utils.enter();
          break;
        case 2:
          System.out.println("        LIST\n" + "------------------------------------");
          Utils.show();
          Utils.enter();
          break;
        case 3:
          System.out.println("        ADD\n" + "------------------------------------");
          Utils.add();
          Utils.enter();
          break;
        case 4:
          System.out.println("        REMOVE\n" + "------------------------------------");
          Utils.remove();
          Utils.enter();
          break;
        case 5:
          // WIP
          Options.show();
          break;
        case 6:
          System.out.println(
                  "\nBye\n"
                          + "                   _____\n"
                          + "                  /     \\\n"
                          + "                  vvvvvvv  /|__/|\n"
                          + "                     I   /O,O   |\n"
                          + "                     I /_____   |      /|/|\n"
                          + "                    J|/^ ^ ^ \\  |    /00  |    _//| \n"
                          + "                     |^ ^ ^ ^ |W|   |/^^\\ |   /oo | \n"
                          + "                      \\m___m__|_|    \\m_m_|   \\mm_| \n"
                          + "            \n\n");
          System.exit(0);
          break;
        default:
          System.out.println("Pls select 1-5.");
      }
    }
  }

  private static void readConfig() {
    String mangaLocation = FileHandler.getRootFolder() + "manga.conf";
    FileHandler handler = new FileHandler();
    if (FileHandler.checkFile(mangaLocation)) {
      String[] config = handler.getFile(mangaLocation);
      String location = "ERROR";
      for (String data : config) {
        if (data != null) {
          if (data.split("([$])")[0].equals("location")) {
            location = data.split("([$])")[1];
          }
        }
      }
      Options.changeLocation(location);
    } else {
      handler.writeConfig(FileHandler.getRootFolder(), "location");
    }
  }
}
