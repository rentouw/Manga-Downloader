package com.rentouw;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    /**
     * Same as read() from python.
     *
     * @param text Text to be asked.
     * @return String from users input
     */
    public static String read(String text) {
        Scanner input = new Scanner(System.in);
        System.out.print(text);
        return input.nextLine();
    }

    /**
     * Check if a path exists.
     *
     * @param path True = path exists
     * @return
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
        ArrayList<String> newMangaList = new ArrayList<String>();
        ArrayList<String> newChapterList = new ArrayList<String>();
        String[] mangaList = handler.getFile(true);
        if (number <= mangaList.length) {
            for (int i = 0; i < mangaList.length; i++) {
                String manga = mangaList[i];
                if (manga != null) {
                    if (!manga.equals(mangaList[number])) {
                        newMangaList.add(manga);
                    }
                }
            }

            String[] chapterList = handler.getFile(false);
            for (int i = 0; i < chapterList.length; i++) {
                String chapter = chapterList[i];
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
        String[] allMangas = handler.getFile(true);
        String[] allChapters = handler.getFile(false);
        for (int i = 0; i < allMangas.length; i++) {
            String manga = allMangas[i];
            String chapter = allChapters[i];
            if (manga != null) {
                String[] mangaList = manga.split("([$])");
                if (chapter != null) {
                    String[] chapterList = chapter.split("([$])");
                    System.out.println("\t-" + i + " " + mangaList[0] + " (" + chapterList[1] + ")" + "[" + mangaList[2] + "]");
                } else {
                    System.out.println("\t-" + i + " " + mangaList[0] + "[" + mangaList[2] + "]");
                }
            }
        }
    }

    public static ArrayList<String> checkNewFiles() {
        String root = FileHandler.getRootFolder();
        FileHandler fileHandler = new FileHandler();
        String[] mangasArray = fileHandler.getFile(true);
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
                    for (File file : listOfFiles) {
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
        //to end the recursive loop
        if (!file.exists())
            return;

        //if directory, go inside and call recursively
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                //call recursively
                recursiveDelete(f);
            }
        }
        //call delete to delete files and empty directory
        file.delete();
    }
}
