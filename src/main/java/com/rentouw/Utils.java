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

  /** Make a Press enter dialog */
  public static void enter() {
    Scanner input = new Scanner(System.in);
    System.out.println("Press enter key to continue...");
    input.nextLine();
  }

  /** Add manga to manga.json file. */
  public static void add() {
    Scanner input = new Scanner(System.in);
    System.out.print("\turl of the new manga :");
    String url = input.nextLine();
    if (!FileHandler.checkByUrl(url)) {
      String[] urlList = url.split("/");
      if (urlList[2].equals("manganelo.com") || urlList[2].equals("mangakakalot.com")) {
        String name = urlList[3];
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
          FileHandler.writeManga(name, url, downloadBool);
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

  /** Remove manga form manga.json file. */
  public static void remove() {
    Scanner input = new Scanner(System.in);
    show();
    System.out.print("\tnumber of the manga to remove :");
    int number = input.nextInt();
    ArrayList<String[]> mangaList = FileHandler.getFile(FileHandler.getMangaList());
    ArrayList<String[]> newMangaList = new ArrayList<>();
    if (number <= mangaList.size()) {
      int i = 0;
      for (String[] manga : mangaList) {
        if (i != number) newMangaList.add(manga);
        i++;
      }
      FileHandler.writeJSON(newMangaList);
    }
  }

  /** Show all the manga's. */
  public static void show() {
    System.out.println("name(chapter)[install]");
    ArrayList<String[]> allMangas = FileHandler.getFile(FileHandler.getMangaList());
    int i = 0;
    for (String[] manga : allMangas) {
      System.out.println("\t-" + i + " " + manga[0] + " (" + manga[3] + ")" + "[" + manga[2] + "]");
      i++;
    }
  }

  /**
   * Copy files to root/newFiles/ folder.
   *
   * @param files Files that need to be moved.
   */
  public static void moveNewFiles(ArrayList<String> files) {
    if (!FileHandler.checkFile(FileHandler.getRootFolder() + "newFiles/"))
      FileHandler.makePath(FileHandler.getRootFolder() + "newFiles/");
    for (String filePath : files) {
      File f = new File(filePath);
      try {
        Files.copy(
            Paths.get(f.getAbsolutePath()),
            Paths.get(FileHandler.getRootFolder() + "newFiles/" + f.getName()));
      } catch (IOException e) {
        System.out.println(f.getName() + " is already in the newFiles folder.");
      }
    }
  }

  /**
   * Check which files are newly downloaded.
   *
   * @return array with all the new files.
   */
  public static ArrayList<String> checkNewFiles() {
    String root = FileHandler.getRootFolder();
    ArrayList<String[]> mangasArray = FileHandler.getFile(FileHandler.getMangaList());
    ArrayList<String> files = new ArrayList<>();
    String folder;
    try {
      for (String[] mangaList : mangasArray) {
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
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return files;
  }

  /**
   * Remove all files in a folder.
   *
   * @param file Folder as File object.
   */
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
