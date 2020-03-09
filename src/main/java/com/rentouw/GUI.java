package com.rentouw;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class GUI {

  private static JTextArea panel;
  private static String textPanelText = "";

  public static void startGUI(ArrayList<String[]> file) {
    JFrame f = new JFrame(); // creating instance of JFrame

    JMenu menu;
    JMenuItem location, info;
    JMenuBar mb = new JMenuBar();
    menu = new JMenu("Options");
    location = new JMenuItem("Location");
    info = new JMenuItem("Info");
    menu.add(location);
    menu.add(info);
    mb.add(menu);
    location.addActionListener(actionEvent -> locationFunction(f));
    info.addActionListener(infoPopUp(f));

    DefaultListModel<String> listManga = new DefaultListModel<>();
    for (String[] manga : file) listManga.addElement(manga[0] + "[" + manga[3] + "]");
    JScrollPane scrolllist = createScrollMangaPanel(listManga, new Rectangle(5, 10, 365, 400));

    panel = createTextArea("Welcome to manga Downloader.", new Rectangle(375, 10, 345, 400));

    JButton downloadButton =
        createButton("Download", new Rectangle(50, 500, 125, 40), actionEvent -> downloadMethode());

    JButton addButton =
        createButton("Add", new Rectangle(225, 500, 100, 40), actionEvent -> addMethode(f));

    JButton removeButton =
        createButton("Remove", new Rectangle(375, 500, 100, 40), actionEvent -> removeMethode(f));

    f.add(panel); // add panel
    f.add(scrolllist); // adding list with scroll panel in JFrame
    f.add(downloadButton); // adding button in JFrame
    f.add(addButton);
    f.add(removeButton);

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setJMenuBar(mb);
    f.setTitle("Manga downloader");
    f.setSize(730, 600); // 730 width and 600 height
    f.setLayout(null); // using no layout managers
    f.setVisible(true); // making the frame visible
  }

  private static void downloadMethode() {
    panel.setText("        DOWNLOAD\n" + "------------------------------------");
    ArrayList<String[]> bigList = FileHandler.getFile(FileHandler.getMangaList());
    Download download;
    int i = 1;
    ExecutorService executor = Executors.newFixedThreadPool(bigList.size());
    for (String[] mangaDetails : bigList) {
      if (mangaDetails != null) {
        download = new Download(mangaDetails, Integer.toString(i), panel, textPanelText);
        executor.execute(download);
        i += 1;
      }
    }
    executor.shutdown();
    panel.setText("Downloading output:");
    while (!executor.isTerminated()) {}
  }

  private static void removeMethode(JFrame f) {
    String name = JOptionPane.showInputDialog(f, "What is your name you want to remove?", null);
    System.out.println(name);
  }

  private static void addMethode(JFrame f) {
    String name = JOptionPane.showInputDialog(f, "What is your name?", null);
    System.out.println(name);
  }

  private static ActionListener infoPopUp(JFrame f) {
    return e ->
        JOptionPane.showMessageDialog(
            f,
            "This is a program to download manga and convert it to .cbz files.\n"
                + "Made by rentouw",
            "Info",
            JOptionPane.PLAIN_MESSAGE);
  }

  private static void locationFunction(JFrame f) {
    String name =
        JOptionPane.showInputDialog(
            f, "Want to change your root location?", FileHandler.getRootFolder());
    if (!name.equals(FileHandler.getRootFolder())) {
      Options.changeLocation(name);
      JOptionPane.showMessageDialog(
          f, "Location is updated to " + name, " Location", JOptionPane.PLAIN_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(
          f, "Location is still " + name, " Location", JOptionPane.PLAIN_MESSAGE);
    }
  }

  private static JTextArea createTextArea(String text, Rectangle dimension) {
    JTextArea panel = new JTextArea();
    panel.setFocusable(false);
    panel.setText(text);
    panel.setBounds(dimension);
    return panel;
  }

  private static JButton createButton(String text, Rectangle dimension, ActionListener function) {
    JButton addButton = new JButton(text); // creating instance of JButton
    addButton.setBounds(dimension); // x axis, y axis, width, height
    addButton.addActionListener(function);
    return addButton;
  }

  private static JScrollPane createScrollMangaPanel(
      DefaultListModel<String> List, Rectangle dimension) {
    JList<String> list = new JList<>(List);
    list.setCellRenderer(
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c =
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) c.setBackground(Color.getHSBColor(18, 4, 75));
            else {
              if (index % 2 == 0) c.setBackground(getBackground().darker());
              else c.setBackground(getBackground());
            }
            return c;
          }
        });
    JScrollPane scrolllist = new JScrollPane(list);
    scrolllist.setBounds(dimension);
    return scrolllist;
  }
}
