package com.rentouw;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Convert {
  /**
   * Create a .cbz from a folder.
   *
   * @param nameManga Name of the manga you want to convert.
   */
  public static void convert(String nameManga, ArrayList<String[]> array) {
    String map_prefix = "00";
    String cbzFolder = FileHandler.getRootFolder() + nameManga + "_cbz/";
    FileHandler.makePath(cbzFolder);
    for (int i = 0; i < FileHandler.readChapter(nameManga, array); i++) {
      if (i >= 10 && i < 100) map_prefix = "0";
      else if (i >= 100) map_prefix = "";
      if (!FileHandler.checkFile(cbzFolder + nameManga + "_" + map_prefix + i + ".cbz")
              && (FileHandler.checkFile(
                  FileHandler.getRootFolder() + nameManga + "/" + map_prefix + i + "/000.png"))
          || FileHandler.checkFile(
              FileHandler.getRootFolder() + nameManga + "/" + map_prefix + i + "/000.jpg")) {
        System.out.println(
            "Making "
                + FileHandler.getRootFolder()
                + nameManga
                + "_cbz/"
                + map_prefix
                + i
                + ".cbz");
        zipFolder(
            Paths.get(FileHandler.getRootFolder() + nameManga + "/" + map_prefix + i),
            Paths.get(cbzFolder + map_prefix + i + ".cbz"));
        new File(cbzFolder + map_prefix + i + ".cbz")
            .renameTo(new File(cbzFolder + nameManga + "_" + map_prefix + i + ".cbz"));
      }
    }
    File f = new File(FileHandler.getRootFolder() + nameManga);
    Utils.recursiveDelete(f);
  }

  /**
   * Put all files in a folder into a zip file.
   *
   * @param sourceFolderPath Location of the folder.
   * @param zipPath Location for the made zip file.
   */
  // Uses java.util.zip to create zip file
  private static void zipFolder(Path sourceFolderPath, Path zipPath) {
    try {
      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
      Files.walkFileTree(
          sourceFolderPath,
          new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
              Files.copy(file, zos);
              zos.closeEntry();
              return FileVisitResult.CONTINUE;
            }
          });
      zos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check if a file is JPG or PNG and change the suffix accordingly.
   *
   * @param file The path to the file.
   */
  public static void checkJPG(String file) {
    File f;
    String jpgFile = file + ".jpg";
    String pngFile = file + ".png";
    if (FileHandler.checkFile(jpgFile)) f = new File(jpgFile);
    else if (FileHandler.checkFile(pngFile)) f = new File(pngFile);
    else f = new File(file);
    try {
      Process p = Runtime.getRuntime().exec("file " + f.getAbsolutePath());
      String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
      if (output.contains("PNG")) {
        Runtime.getRuntime().exec("optipng " + f.getAbsolutePath());
        f.renameTo(new File(f.getPath().replace(".jpg", "") + ".png"));
      } else if (output.contains("JPEG"))
        Runtime.getRuntime().exec("jpegoptim " + f.getAbsolutePath());
      f.renameTo(new File(f.getPath().replace(".jpg", "") + ".jpg"));
    } catch (IOException e) {
      System.out.println(Arrays.toString(e.getStackTrace()));
    }
  }
}
