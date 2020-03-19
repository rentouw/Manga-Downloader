package com.rentouw;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class FileHandler {
  private static String rootFolder = "./";
  private static String mangaList = "manga.json";

  public FileHandler() {
    // Check if settings files are made.
    if (!checkFile(getMangaList())) {
      File f = new File(getMangaList());
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

  public static void setRootFolder(String rootFolder) {
    FileHandler.rootFolder = rootFolder;
  }

  public static String getMangaList() {
    return rootFolder + mangaList;
  }

  //  public static void setMangaList(String mangaList) {
  //    FileHandler.mangaList = mangaList;
  //  }

  /**
   * Make a folder.
   *
   * @param path Location for your new map.
   * @return boolean for folder made.
   */
  public static boolean makePath(String path) {
    return new File(path).mkdirs();
  }

  /**
   * Check of a file exists.
   *
   * @param path location of the path
   * @return Boolean true if file exists
   */
  public static boolean checkFile(String path) {
    return new File(path).exists();
  }

  /**
   * Read json file. And return content as JSONArray
   *
   * @return JSONArray with full content of manga.json
   */
  private static JSONArray readJsonFile(String file) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
    JSONArray mangaListJson = new JSONArray();

    try (FileReader reader = new FileReader(file)) {
      // Read JSON file
      mangaListJson = (JSONArray) jsonParser.parse(reader);
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
    return mangaListJson;
  }

  /**
   * Take the JSONArray and take all input manga and return as array.
   *
   * @param mangaList JSONArray with all the files.
   * @return Nested array with every manga and info about the manga organized as [manga, url,
   *     download, chapter]
   */
  private static ArrayList<String[]> parseMangaObject(JSONArray mangaList) {
    // Get json keys within list
    ArrayList<String[]> array = new ArrayList<>();
    Object[] keyset = ((JSONObject) mangaList.get(0)).keySet().toArray();

    for (Object key : keyset) {
      JSONObject mangaObject = (JSONObject) ((JSONObject) mangaList.get(0)).get(key);
      // Make temp array
      String[] tmp =
          new String[] {
            String.valueOf(key),
            (String) mangaObject.get("url"),
            (String) mangaObject.get("download"),
            (String) mangaObject.get("chapter")
          };
      array.add(tmp);
    }
    return array;
  }

  /**
   * Return content of json-file.
   *
   * @param file name of json-file.
   * @return array with content of the json-file.
   */
  public static ArrayList<String[]> getFile(String file) {
    return parseMangaObject(readJsonFile(file));
  }

  /**
   * Change 1 entry from a manga.
   *
   * @param array array to change
   * @param manga manga which the entry is going to change.
   * @param data the data to change.
   * @param type the entry you want to change. (chapter, download, url)
   * @return changed array.
   */
  private static ArrayList<String[]> editJson(
      ArrayList<String[]> array, String manga, String data, String type) {
    switch (type) {
      case "chapter":
        return editJson(array, manga, null, null, data);
      case "download":
        return editJson(array, manga, null, data, null);
      case "url":
        return editJson(array, manga, data, null, null);
      default:
        return editJson(array, manga, null, null, null);
    }
  }

  /**
   * Change 1 entry from a array.
   *
   * @param array array to change
   * @param manga manga which the entry is going to change.
   * @param url changed url
   * @param download changed download state
   * @param chapter changed chapter number
   * @return changed array
   */
  private static ArrayList<String[]> editJson(
      ArrayList<String[]> array, String manga, String url, String download, String chapter) {
    ArrayList<String[]> newArray = new ArrayList<>();
    for (String[] i : array) {
      if (i[0].equals(manga)) {
        if (url != null) i[1] = url;
        if (download != null) i[2] = download;
        if (chapter != null) i[3] = chapter;
      }
      newArray.add(i);
    }
    return newArray;
  }

  /**
   * Write JSONArray back to the file.
   *
   * @param array content that is going to be write to the file.
   */
  public static void writeJSON(ArrayList<String[]> array) {
    // Write JSON file
    try (FileWriter file = new FileWriter(getMangaList())) {
      JSONArray jsonArray = new JSONArray();
      JSONObject bigTmp = new JSONObject();
      for (String[] i : array) {
        JSONObject tmp = new JSONObject();
        tmp.put("url", i[1]);
        tmp.put("download", i[2]);
        tmp.put("chapter", i[3]);
        bigTmp.put(i[0], tmp);
      }
      jsonArray.add(bigTmp);
      file.write(jsonArray.toJSONString());
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check if a url is already in the manga.list file.
   *
   * @param url Url that is going to be checked.
   * @return true if url is in the file.
   */
  public static boolean checkByUrl(String url) {
    boolean output = false;
    ArrayList<String[]> list = getFile(mangaList);
    for (String[] array : list) {
      if (array[1].equals(url)) {
        output = true;
        break;
      }
    }
    return output;
  }

  /**
   * Return amount of chapters.
   *
   * @param nameManga Name of the manga where you want to know the amount of chapters from.
   * @param array Array with the full JSONArray.
   * @return number of chapters.
   */
  public static int readChapter(String nameManga, ArrayList<String[]> array) {
    int chapter = 0;
    if (array == null) array = parseMangaObject(readJsonFile(getMangaList()));
    for (String[] i : array) {
      if (i[0].equals(nameManga)) {
        chapter = Integer.parseInt(i[3]);
      }
    }
    return chapter;
  }

  /**
   * Write new chapter number to file.
   *
   * @param mangaName Name of manga were the chapter number changes.
   * @param newChapter The number to change for the chapter.
   * @param array Array with all the JSON data.
   */
  public static void writeChapter(String mangaName, int newChapter, ArrayList<String[]> array) {
    array = editJson(array, mangaName, String.valueOf(newChapter), "chapter");
    writeJSON(array);
  }

  /**
   * Write new manga to file.
   *
   * @param name name of manga.
   * @param url url of manga.
   * @param downloadBool boolean state for manga.
   */
  public static void writeManga(String name, String url, boolean downloadBool) {
    ArrayList<String[]> array = getFile(getMangaList());
    array.add(
        new String[] {
          name, url, String.valueOf(downloadBool), "0"
        }); // [manga, url, download, chapter]
    writeJSON(array);
  }
}
