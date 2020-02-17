package com.rentouw;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class GUI {
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
    location.addActionListener(locationFunction(f));
    info.addActionListener(infoPopUp(f));

    JButton downloadButton =
            createButton(
                    "Download", new Rectangle(50, 500, 125, 40), actionEvent -> System.out.println("test"));

    JButton addButton =
            createButton("Add", new Rectangle(225, 500, 100, 40), actionEvent -> addMethode(f));

    JButton removeButton =
            createButton(
                    "Remove", new Rectangle(375, 500, 100, 40), actionEvent -> System.out.println("test3"));

    DefaultListModel<String> listManga = new DefaultListModel<>();
    for (String[] manga : file) listManga.addElement(manga[0] + "[" + manga[3] + "]");
    JScrollPane scrolllist = createScrollMangaPanel(listManga, new Rectangle(5, 10, 365, 400));

    JTextArea panel =
            createTextArea("Welcome to manga Downloader.", new Rectangle(375, 10, 345, 400));

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

  private static void addMethode(JFrame f) {
    String name = JOptionPane.showInputDialog(f, "What is your name?", null);
    System.out.println(name);
  }

  private static ActionListener infoPopUp(JFrame f) {
    return e -> {
      JOptionPane.showMessageDialog(
              f,
              "This is a program to download manga and convert it to .cbz files.\n" + "Made by rentouw",
              "Info",
              JOptionPane.PLAIN_MESSAGE);
    };
  }

  private static ActionListener locationFunction(JFrame f) {
    return e -> System.out.println("location");
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
