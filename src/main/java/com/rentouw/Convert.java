package com.rentouw;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class Convert {
  public static void convert(String nameManga) {
    FileHandler handler = new FileHandler();
    String map_prefix = "00";
    Download.makePath(FileHandler.getRootFolder() + nameManga + "_cbz/");
    for (int i = 0; i < handler.readChapter(nameManga); i++) {
      if (i >= 10 && i < 100) {
        map_prefix = "0";
      } else if (i >= 100) {
        map_prefix = "";
      }
      if (!FileHandler.checkFile(
          FileHandler.getRootFolder()
              + nameManga
              + "_cbz/"
              + nameManga
              + "_"
              + map_prefix
              + i
              + ".cbz")) {
        System.out.println(
            "Making "
                + FileHandler.getRootFolder()
                + nameManga
                + "_cbz/"
                + map_prefix
                + i
                + ".cbz");
        try {
          zipFolder(
              Paths.get(FileHandler.getRootFolder() + nameManga + "/" + map_prefix + i),
              Paths.get(
                  FileHandler.getRootFolder() + nameManga + "_cbz/" + map_prefix + i + ".cbz"));
        } catch (Exception e) {
          e.printStackTrace();
        }

        new File(FileHandler.getRootFolder() + nameManga + "_cbz/" + map_prefix + i + ".cbz")
            .renameTo(
                new File(
                    FileHandler.getRootFolder()
                        + nameManga
                        + "_cbz/"
                        + nameManga
                        + "_"
                        + map_prefix
                        + i
                        + ".cbz"));

      } else {
        //        System.out.println(FileHandler.getRootFolder() + nameManga + "_cbz/" + nameManga +
        // "_" + map_prefix + i + ".cbz exists");
      }
    }
    File f = new File(FileHandler.getRootFolder() + nameManga);
    Utils.recursiveDelete(f);
  }

  // Uses java.util.zip to create zip file
  private static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
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
  }

  /**
   * Check if a file is JPG or PNG and change the suffix accordingly.
   *
   * @param file The path to the file.
   */
  public static void checkJPG(String file) {
    File f;
    if (FileHandler.checkFile(file + ".jpg")) {
      f = new File(file + ".jpg");
    } else if (FileHandler.checkFile(file + ".png")) {
      f = new File(file + ".png");
    } else {
      f = new File(file);
    }
    try {
      Process p = Runtime.getRuntime().exec("file " + f.getAbsolutePath());
      String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
      if (output.contains("PNG")) {
        f.renameTo(new File(f.getPath().replace(".jpg", "") + ".png"));
      } else if (output.contains("JPEG")) {
        f.renameTo(new File(f.getPath().replace(".jpg", "") + ".jpg"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}