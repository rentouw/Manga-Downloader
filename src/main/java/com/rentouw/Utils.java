package com.rentouw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class Utils {

  /**
   * Same as read() from python.
   *
   * @param text Text to be asked.
   * @return String from users input
   */
  private static String read(String text) {
    Scanner input = new Scanner(System.in);
    System.out.print(text);
    return input.nextLine();
  }

  /**
   * Check if a path exists.
   *
   * @param path The path to the folder/file
   * @return boolean True = path exists
   */
  public static boolean testPath(String path) {
    return new File(path).exists();
  }

  public static void enter() {
    Scanner input = new Scanner(System.in);
    System.out.println("Press enter key to continue...");
    input.nextLine();
  }

  public static void add() {
    Scanner input = new Scanner(System.in);
    FileHandler handler = new FileHandler();
    System.out.print("\turl of the new manga :");
    String url = input.nextLine();
    if (!handler.checkByUrl(url)) {
      String[] urlList = url.split("/");
      if (urlList[2].equals("manganelo.com") || urlList[2].equals("mangakakalot.com")) {
        String name = urlList[4];
        String correct = Utils.read("\tIs the name of the manga " + name + " ? (Y/n): ");
        if (correct.equals("n")) {
          name = Utils.read("\tThan what is the name ?:");
        }
        correct = Utils.read("\tWould you like to download " + name + " ? (Y/n): ");
        boolean downloadBool = true;
        if (correct.equals("n")) {
          downloadBool = false;
        }
        System.out.println("Checking getting urls for " + name);
        if (Download.testUrl(url)) {
          handler.writeManga(name, url, downloadBool);
        } else {
          System.out.println("\tSorry seems like there are no images on this site.");
        }
      } else {
        System.out.println("\tSorry only manganelo or mangakakalot are supported for now.");
      }
    } else {
      System.out.println("\tThis url is already in the list.");
    }
  }

  public static void remove() {
    Scanner input = new Scanner(System.in);
    FileHandler handler = new FileHandler();
    show();
    System.out.print("\tnumber of the manga to remove :");
    int number = input.nextInt();
    ArrayList<String> newMangaList = new ArrayList<>();
    ArrayList<String> newChapterList = new ArrayList<>();
    String[] mangaList = handler.getFile(FileHandler.getMangaList());
    if (number <= mangaList.length) {
      for (String manga : mangaList) {
        if (manga != null) {
          if (!manga.equals(mangaList[number])) {
            newMangaList.add(manga);
          }
        }
      }

      String[] chapterList = handler.getFile(FileHandler.getChapterList());
      for (String chapter : chapterList) {
        if (chapter != null) {
          if (!chapter.equals(chapterList[number])) {
            newChapterList.add(chapter);
          }
        }
      }

      String file = FileHandler.getMangaList();
      File f = new File(file);
      f.delete();
      try {
        f.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      for (String manga : newMangaList) {
        handler.writeManga(manga);
      }

      file = FileHandler.getChapterList();
      f = new File(file);
      f.delete();
      try {
        f.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      for (String chapter : newChapterList) {
        handler.writeChapter(chapter);
      }
    }
  }

  public static void show() {
    FileHandler handler = new FileHandler();
    System.out.println("name(chapter)[install]");
    String[] allMangas = handler.getFile(FileHandler.getMangaList());
    String[] allChapters = handler.getFile(FileHandler.getChapterList());
    for (int i = 0; i < allMangas.length; i++) {
      String manga = allMangas[i];
      String chapter = allChapters[i];
      if (manga != null) {
        String[] mangaList = manga.split("([$])");
        if (chapter != null) {
          String[] chapterList = chapter.split("([$])");
          System.out.println(
                  "\t-"
                          + i
                          + " "
                          + mangaList[0]
                          + " ("
                          + chapterList[1]
                          + ")"
                          + "["
                          + mangaList[2]
                          + "]");
        } else {
          System.out.println("\t-" + i + " " + mangaList[0] + "[" + mangaList[2] + "]");
        }
      }
    }
  }

  public static void moveNewFiles(ArrayList<String> files) {
    if (!FileHandler.checkFile(FileHandler.getRootFolder() + "/newFiles/")) {
      Download.makePath(FileHandler.getRootFolder() + "/newFiles/");
    }
    for (String filePath : files) {
      File f = new File(filePath);
      try {
        Files.copy(
                Paths.get(f.getAbsolutePath()),
                Paths.get(FileHandler.getRootFolder() + "/newFiles/" + f.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static ArrayList<String> checkNewFiles() {
    String root = FileHandler.getRootFolder();
    FileHandler fileHandler = new FileHandler();
    String[] mangasArray = fileHandler.getFile(FileHandler.getMangaList());
    ArrayList<String> files = new ArrayList<>();
    String folder;
    try {
      for (String mangaArray : mangasArray) {
        String[] mangaList = mangaArray.split("([$])");
        String manga = mangaList[0];
        folder = root + "/" + manga + "_cbz/";
        if (testPath(folder)) {
          File map = new File(folder);
          File[] listOfFiles = map.listFiles();
          for (File file : Objects.requireNonNull(listOfFiles)) {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM");
            Date date = Calendar.getInstance().getTime();
            String fileDate = dateFormat.format(file.lastModified());
            String now = dateFormat.format(date);
            if (fileDate.equals(now)) {
              files.add(file.getAbsolutePath());
            }
          }
        }
      }
    } catch (NullPointerException ignored) {
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return files;
  }

  public static void recursiveDelete(File file) {
    // to end the recursive loop
    if (!file.exists()) return;

    // if directory, go inside and call recursively
    if (file.isDirectory()) {
      for (File f : Objects.requireNonNull(file.listFiles())) {
        // call recursively
        recursiveDelete(f);
      }
    }
    // call delete to delete files and empty directory
    file.delete();
  }
}
