package com.rentouw;

import java.util.Scanner;

public class Options {
  public static void show() {
    Scanner input = new Scanner(System.in);
    int value;
    boolean loop = true;
    while (loop) {
      System.out.print(
          "        OPTIONS\n"
              + "------------------------------------\n"
              + "\t1. Change Location\n"
              + "\t2. Exit\n"
              + "select :");
      value = input.nextInt();
      switch (value) {
        case 1:
          changeLocationAsk();
          Config properties = new Config();
          properties.writeConfig(FileHandler.getRootFolder(), "rootLocation");
          break;
        case 2:
          loop = false;
          break;
        default:
          System.out.println("Pls select 1-2.");
          break;
      }
    }
  }

  private static void changeLocationAsk() {
    Scanner input = new Scanner(System.in);
    System.out.println("Current location=" + FileHandler.getMangaList());
    System.out.print("What is going to be the new location : ");
    String location = input.nextLine();
    changeLocation(location);
    System.out.println("New location is =" + FileHandler.getMangaList());
    Utils.enter();
  }

  protected static void changeLocation(String location) {
    if (location != null) {
      if (!location.substring(location.length() - 1).equals("/")) location = location + "/";
      FileHandler.setRootFolder(location);
      FileHandler.setMangaList(location + "manga.list");
      FileHandler.setChapterList(location + "log.txt");
    }
  }
}
