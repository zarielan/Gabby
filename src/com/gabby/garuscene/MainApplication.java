package com.gabby.garuscene;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;

public class MainApplication {
    public static JTextPane console;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gabby");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(960, 540);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        //Text console=================
        console = new JTextPane();
        console.setEditable(false);
        console.setMargin(new Insets(10, 10, 10, 10));
        console.setPreferredSize(new Dimension(960, 520));

        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(set, 0.12f);
        StyleConstants.setFontFamily(set, "Consolas");
        console.setParagraphAttributes(set, true);

        frame.add(console);

        //Input========================
        JTextField input = new JTextField();
        input.setMargin(new Insets(5, 5, 5, 5));
        input.setFont(new Font("Consolas", Font.PLAIN, 12));
        input.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDictinonary(input.getText());
            }
        });

        frame.add(input);

        //End==========================
        frame.pack();
        frame.setVisible(true);

        input.requestFocus();

        addConsole("Maligayang Pagdating sa Gabby!");
        addConsole("Maglagay ng salita upang isalin sa Filipino!");
    }

    private static void searchDictinonary(String word) {
        addConsole("...");
        addConsole("Naghahanap ng mga kahulugan para sa \"" + word + "\"");
        //https://stackoverflow.com/questions/11497424/passing-data-to-an-html-post-form-using-java
        try {
            URL url = new URL("http://gabbydictionary.com/");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);

            PrintWriter wr = new PrintWriter(con.getOutputStream(), true);

            StringBuilder parameters = new StringBuilder();
            parameters.append("q=" + URLEncoder.encode(word, "UTF-8"));
            parameters.append("&");
            parameters.append("x=" + URLEncoder.encode("0", "UTF-8"));
            parameters.append("&");
            parameters.append("y=" + URLEncoder.encode("0", "UTF-8"));
            wr.println(parameters);
            wr.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = br.readLine()) != null) {
                if (line.contains("Results:<br>")) {
                    formatResults(line);
                    break;
                }
                if (line.contains("Search returned zero results..")) {
                    addConsole("Walang nahanap.");
                    break;
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addConsole(String t) {
        StyledDocument doc = console.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), t + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void formatResults(String result) {
        result = result.trim();
        result = result.substring(12, result.length() - 12);
        System.out.println(result);

        //DO REGEX

        //https://stackoverflow.com/questions/5034442/indexes-of-all-occurrences-of-character-in-a-string
        ArrayList<Integer> markers = new ArrayList<>();

        String noun = "n.  --";
        String verb = "v.  --";

        //Get all instances of n. --
        for (int i = result.indexOf(noun); i >= 0; i = result.indexOf(noun, i + noun.length())) {
            markers.add(i);
        }

        //Get all instances of v. --
        for (int i = result.indexOf(verb); i >= 0; i = result.indexOf(verb, i + verb.length())) {
            markers.add(i);
        }

        markers.stream().forEach(System.out::println);
    }
}
