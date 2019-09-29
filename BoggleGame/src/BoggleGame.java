
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.SET;

public class BoggleGame extends JFrame {
    private static final int GAME_TIME = 180;                 // 游戏时间终点
    private static final int SECONDS_PER_MINUTE = 60;         // 一分钟60秒
    private static final int FOUND_WORDS_DISPLAY_COUNT = 17;  // how many rows to display for the two side columns
    private static final int ALL_WORDS_DISPLAY_COUNT   = 7;   // how many rows to display for the middle all-words list

    // 窗口尺寸
    private static final int DEF_HEIGHT = 550;
    private static final int DEF_WIDTH = 700;
    private static final int WORD_PANEL_WIDTH  = 205;
    private static final int WORD_PANEL_HEIGHT = 325;

    // 部件显示颜色
    private static final Color PLAYER_POINT_WORD = new Color(0xBFBFBF);
    private static final Color OPP_POINT_WORD    = new Color(0xBFBFBF);
    private static final Color NONPOINT_WORD     = new Color(0xBFBFBF);

    // 游戏等级
    // keep these in sync - should be a text description for each level!
    // if making adjustments to levels, endGame (~line 400) contains hard-coded elements
    // menu items will be adjusted automatically
    private static final int NUMBER_OF_LEVELS = 5;
    private static final String[] LEVEL_DESCRIPTION = {
            "Nursery",
            "Shakespeare",
            "Algorithms 4/e",
            "Hard",
            "Impossible"
    };
    private static final int NURSERY     = 0;
    private static final int SHAKESPEARE = 1;
    private static final int ALGORITHMS  = 2;
    private static final int HARD        = 3;
    private static final int IMPOSSIBLE  = 4;

    // keep these two values in sync!
    // used to force the JTextfield and the JList to be the same length
    // 输入框的尺寸大小
    private static final int DEF_COLUMNS = 10;
    private static final String MAX_WORD_SIZE = "INCONSEQUENTIALLY";


    // keeps track of the level
    private int gameDifficulty = 0;

    // 棋盘大小
    private int BOARD_ROWS;
    private int BOARD_COLS;

    // game values
    private boolean inGame = true;
    private int elapsedTime = 0;      // 游戏的运行时间
    private int points = 0;           // current number of points
    private Timer timer = new Timer();// 计时器

    private String[] emptyList = new String[0];

    //数据显示
    private LinkedHashSet<String> foundWords;      // to keep words in same order as entered
    private TreeSet<String> validWords;
    private TreeSet<String> opponentFoundWords;
    private JList foundWordsList;
    private JList validWordsList;
    private JList opponentFoundWordsList;

    private int oppCurScore;
    private BoggleBoard board;

    // dictionaries
    // (words that appear in Shakespeare, nursery rhymes, common words, and Algorithms 4/e)
    private SET<String> shakespeareDictionary;
    private SET<String> nurseryDictionary;
    private SET<String> commonDictionary;
    private SET<String> algs4Dictionary;

    // GUI elements 
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JRadioButtonMenuItem[] difficultySelection;
    private BoggleSolver solver;
    private JLabel clock;
    private BoardPanel bp;
    private final JTextField entryField;
    private JLabel scoreLabel;
    private JLabel possiblePointsLabel;
    private JLabel oppScoreLabel;

    /**
     * Construct the GUI for the Boggle game.
     */
    public BoggleGame(int rows, int cols) {
        // 构造棋盘的大小
        BOARD_ROWS = rows;
        BOARD_COLS = cols;

        // this.setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
        // 常规操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Boggle");
        setLocationRelativeTo(null);    // 指定窗口出现在某些相对位置 这里是出现在屏幕正中央
        this.makeMenuBar();             // 后面有相应联系的地方

        // 时间面板及时间标签
        JPanel timerPanel = new JPanel();
        JLabel timerLabel = new JLabel("Timer:");
        String seconds = String.format("%02d", (GAME_TIME - elapsedTime) % SECONDS_PER_MINUTE);
        String minutes = String.format("%02d", (GAME_TIME - elapsedTime) / SECONDS_PER_MINUTE);
        String time = minutes + ":" + seconds;

        //时间组件加到面板里面去
        clock = new JLabel(time);
        timerPanel.add(timerLabel);
        timerPanel.add(clock);

        // 文本输入框
        entryField = new JTextField(DEF_COLUMNS);
        //这里的意思就是 设置一个最大尺寸，并且这个最大尺寸会随着布局管理器变化而变化
        entryField.setMaximumSize(new Dimension(entryField.getPreferredSize().width,
                entryField.getPreferredSize().height));

        // 给文本输入框加入键盘事件 一般这里直接用匿名内部类给解决了
        // 之前这里有一个疑问，为啥下面有各种相应的同时，就必须要在这里加上ActionListener，如果没有这个的话，那么上面有一些总相应，就不能进行调用
        entryField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkWord();
            }
        });

        //三种键盘事件的选择
        entryField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) { }
            @Override
            public void keyReleased(KeyEvent e) {
                // 这里之所以再建立一个文本输入框的原因就是让，上面的显式不要快速呈现在界面上面的选择框。
                JTextField txtSrc = (JTextField) e.getSource();
                String text = txtSrc.getText().toUpperCase();
                bp.matchWord(text);
            }
            @Override
            public void keyTyped(KeyEvent e) { }
        });

        // list of typed words
        foundWordsList = new JList();
        foundWordsList.setPrototypeCellValue(MAX_WORD_SIZE);
        foundWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foundWordsList.setListData(emptyList);
        foundWordsList.setVisibleRowCount(FOUND_WORDS_DISPLAY_COUNT);
        foundWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);
        foundWordsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, false, false);
                JComponent jc = (JComponent) comp;
                String word = (String) value;
                if (!inGame && inGame) {
                    if (foundWords.contains(word) && !opponentFoundWords.contains(word)) {
                        comp.setBackground(PLAYER_POINT_WORD);
                    }
                    else if (foundWords.contains(word) && opponentFoundWords.contains(word)) {
                        comp.setBackground(NONPOINT_WORD);
                    }
                }
                comp.setForeground(Color.black);
                return comp;
            }
        });
        JScrollPane foundWordsScrollPane = new JScrollPane(foundWordsList);
        foundWordsScrollPane.setPreferredSize(new Dimension(WORD_PANEL_WIDTH, WORD_PANEL_HEIGHT));
        foundWordsScrollPane.setMinimumSize(foundWordsScrollPane.getPreferredSize());
        foundWordsScrollPane.setMaximumSize(foundWordsScrollPane.getPreferredSize());
        JPanel scoreLabelPanel = new JPanel();
        scoreLabel = new JLabel("My Points:");
        scoreLabelPanel.add(scoreLabel);
        JPanel controlPanel = new JPanel();

        // layout for the left panel, with controls and found word display
        GroupLayout controlLayout = new GroupLayout(controlPanel);
        controlPanel.setLayout(controlLayout);
        controlLayout.setAutoCreateGaps(true);
        controlLayout.setAutoCreateContainerGaps(true);
        controlLayout.setHorizontalGroup(
                controlLayout.createSequentialGroup()
                        .addGroup(controlLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(timerPanel)
                                .addComponent(entryField)
                                .addComponent(foundWordsScrollPane)
                                .addComponent(scoreLabelPanel))
        );
        controlLayout.setVerticalGroup(
                controlLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(timerPanel,           GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,      GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(entryField,           GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(foundWordsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,      GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(scoreLabelPanel,      GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // creates the board and the list that will show all the available words at the end of a game
        bp = new BoardPanel();
        validWordsList = new JList();
        validWordsList.setVisible(true);
        validWordsList.setVisibleRowCount(ALL_WORDS_DISPLAY_COUNT);
        validWordsList.setPrototypeCellValue(MAX_WORD_SIZE);
        validWordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        validWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);
        validWordsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, false, false);
                String word = (String) value;
                if (!inGame) {
                    if (foundWords.contains(word)) {
                        comp.setBackground(OPP_POINT_WORD);
                    }
                }
                comp.setForeground(Color.black);
                return comp;
            }
        });
        JScrollPane validWordsScrollPane = new JScrollPane(validWordsList);
        validWordsScrollPane.setPreferredSize(new Dimension(300, 145));
        validWordsScrollPane.setMinimumSize(validWordsScrollPane.getPreferredSize());
        validWordsScrollPane.setMaximumSize(validWordsScrollPane.getPreferredSize());
        JPanel possiblePointsPanel = new JPanel();
        possiblePointsLabel = new JLabel();
        possiblePointsPanel.add(possiblePointsLabel);
        JPanel gamePanel = new JPanel();

        // layout for that panel
        GroupLayout gameLayout = new GroupLayout(gamePanel);
        gamePanel.setLayout(gameLayout);
        gameLayout.setAutoCreateGaps(true);
        gameLayout.setAutoCreateContainerGaps(true);
        gameLayout.setHorizontalGroup(
                gameLayout.createSequentialGroup()
                        .addGroup(gameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(bp)
                                .addComponent(validWordsScrollPane)
                                .addComponent(possiblePointsPanel))
        );
        gameLayout.setVerticalGroup(
                gameLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
                        .addComponent(bp,                   GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,      GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(validWordsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,      GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(possiblePointsPanel,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,        GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
        );

        // Opponent game panel
        JLabel opponentLabel = new JLabel("Opponent's Words:");
        JPanel opponentLabelPanel = new JPanel();
        opponentLabelPanel.add(opponentLabel);
        oppScoreLabel = new JLabel("Opponent's Points: ");
        JPanel oppScoreLabelPanel = new JPanel();
        oppScoreLabelPanel.add(oppScoreLabel);
        opponentFoundWordsList = new JList();
        opponentFoundWordsList.setPrototypeCellValue(MAX_WORD_SIZE);
        opponentFoundWordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        opponentFoundWordsList.setListData(emptyList);
        opponentFoundWordsList.setVisibleRowCount(FOUND_WORDS_DISPLAY_COUNT);
        opponentFoundWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);
        opponentFoundWordsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, false, false);
                String word = (String) value;
                if (!inGame && inGame) {
                    if (!foundWords.contains(word) && opponentFoundWords.contains(word)) {
                        comp.setBackground(OPP_POINT_WORD);
                    }
                    else if (foundWords.contains(word) && opponentFoundWords.contains(word)) {
                        comp.setBackground(NONPOINT_WORD);
                    }
                }
                comp.setForeground(Color.black);
                return comp;
            }
        });

        JScrollPane opponentWordsScrollPane = new JScrollPane(opponentFoundWordsList);
        opponentWordsScrollPane.setPreferredSize(new Dimension(WORD_PANEL_WIDTH, WORD_PANEL_HEIGHT));
        opponentWordsScrollPane.setMinimumSize(opponentWordsScrollPane.getPreferredSize());
        opponentWordsScrollPane.setMaximumSize(opponentWordsScrollPane.getPreferredSize());
        JPanel spacingPanel = new JPanel();
        spacingPanel.setPreferredSize(new Dimension(WORD_PANEL_WIDTH, 22));
        JPanel opponentPanel = new JPanel();
        GroupLayout buttonsLayout = new GroupLayout(opponentPanel);
        opponentPanel.setLayout(buttonsLayout);
        buttonsLayout.setAutoCreateContainerGaps(true);
        buttonsLayout.setAutoCreateGaps(true);
        buttonsLayout.setHorizontalGroup(
                buttonsLayout.createSequentialGroup()
                        .addGroup(buttonsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(spacingPanel)
                                .addComponent(opponentLabelPanel)
                                .addComponent(opponentWordsScrollPane)
                                .addComponent(oppScoreLabelPanel))
                //.addComponent(winnerLabel))
        );
        buttonsLayout.setVerticalGroup(
                buttonsLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,           GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
                        .addComponent(spacingPanel,            GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,         GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(opponentLabelPanel,      GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,         GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(opponentWordsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,         GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addComponent(oppScoreLabelPanel,      GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        //.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,         GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        //.addComponent(winnerLabel,             GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,           GroupLayout.DEFAULT_SIZE,   Short.MAX_VALUE)
        );

        // layout for the left and right panels 
        Container content = getContentPane();
        GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(controlPanel,    GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(gamePanel,       GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addComponent(opponentPanel,   GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                                .addComponent(controlPanel)
                                .addComponent(gamePanel)
                                .addComponent(opponentPanel))
        );

        // all words in shakespeare
        In in1 = new In(new File("dictionary-shakespeare.txt"));
        shakespeareDictionary = new SET<String>();
        for (String s : in1.readAllStrings())
            shakespeareDictionary.add(s);

        // all words in shakespeare
        In in2 = new In(new File("dictionary-nursery.txt"));
        nurseryDictionary = new SET<String>();
        for (String s : in2.readAllStrings())
            nurseryDictionary.add(s);

        // about 20K common words
        In in3 = new In(new File("dictionary-common.txt"));
        commonDictionary = new SET<String>();
        for (String s : in3.readAllStrings())
            commonDictionary.add(s);

        // all words in Algorithms 4/e
        In in4 = new In(new File("dictionary-algs4.txt"));
        algs4Dictionary = new SET<String>();
        for (String s : in4.readAllStrings())
            algs4Dictionary.add(s);

        // dictionary
        In in = new In(new File("dictionary-yawl.txt"));
        String[] dictionary = in.readAllStrings();

        // create the Boggle solver with the given dictionary
        solver = new BoggleSolver(dictionary);

        newGame();
        this.pack();
    }

    /**
     * Start a new game, can be called via the menu selection, the button, or CMD+N (CRTL+N).
     */
    private void newGame() {
        if (BOARD_ROWS == 4 && BOARD_COLS == 4) {
            board = new BoggleBoard();
        }
        else {
            board = new BoggleBoard(BOARD_ROWS, BOARD_COLS);
        }
        clock.setForeground(Color.BLACK);
        entryField.requestFocus();
        inGame = true;
        points = 0;
        scoreLabel.setText("Current Points:" + points);
        entryField.setEnabled(true);

        foundWords = new LinkedHashSet<String>();

        // set display of word lists to be empty
        foundWordsList.setListData(emptyList);
        validWordsList.setListData(emptyList);
        opponentFoundWordsList.setListData(emptyList);

        bp.setBoard();
        bp.unhighlightCubes();

        // all valid words
        Iterable<String> words = solver.getAllValidWords(board);
        validWords = new TreeSet<String>();
        int possiblePoints = 0;
        for (String s : words) {
            validWords.add(s);
            possiblePoints += scoreWord(s);
        }
        possiblePointsLabel.setText("Possible Points: " + possiblePoints);

        // opponent's words
        opponentFoundWords = new TreeSet<String>();
        if (gameDifficulty == NURSERY) {
            for (String word : validWords)
                if (nurseryDictionary.contains(word))
                    opponentFoundWords.add(word);
        }

        else if (gameDifficulty == SHAKESPEARE) {
            for (String word : validWords)
                if (shakespeareDictionary.contains(word) && StdRandom.uniform(3) != 0)
                    opponentFoundWords.add(word);
        }

        else if (gameDifficulty == ALGORITHMS) {
            for (String word : validWords)
                if (algs4Dictionary.contains(word))
                    opponentFoundWords.add(word);
        }

        else if (gameDifficulty == HARD) {
            for (String word : validWords)
                if (commonDictionary.contains(word) && StdRandom.bernoulli())
                    opponentFoundWords.add(word);
        }

        else if (gameDifficulty == IMPOSSIBLE) {
            for (String word : validWords)
                if (StdRandom.uniform(4) != 0)
                    opponentFoundWords.add(word);
        }

        // opponent's score
        oppCurScore = 0;
        for (String word : opponentFoundWords)
            oppCurScore += scoreWord(word);

        oppScoreLabel.setText("Opponent's Points: " + oppCurScore);
        timer.cancel();
        elapsedTime = -1;
        timer = new Timer();
        timer.schedule(new Countdown(), 0, 1000);

    }

    /**
     * End the current game, can be called via the menu selection, the button, or CMD+E (CRTL+E).
     */
    private void endGame() {

        clock.setText("00:00");
        clock.setForeground(Color.RED);
        timer.cancel();
        entryField.setText("");
        entryField.setEnabled(false);

        // display list of all valid words
        validWordsList.setListData(validWords.toArray());

        // highlight found words by specifying indices of found words
        int[] indices = new int[foundWords.size()];
        int i = 0;
        int n = 0;
        for (String s : validWords) {
            if (foundWords.contains(s))
                indices[i++] = n;
            n++;
        }
        validWordsList.setSelectedIndices(indices);
        //validWordsList.setEnabled(false);

        inGame = false;

        // compute score, discounting words that both players found
        int playerScore = points;
        int opponentScore = oppCurScore;
        for (String s : foundWords) {
            if (opponentFoundWords.contains(s)) {
                playerScore   -= scoreWord(s);
                opponentScore -= scoreWord(s);
            }
        }

        // strike out words in user's list that opponent found
        Object[] list1 = foundWords.toArray();
        for (int j = 0; j < list1.length; j++) {
            if (opponentFoundWords.contains(list1[j])) {
                list1[j] = "<html><strike>" + list1[j] + "</strike></html>";
            }
        }
        foundWordsList.setListData(list1);

        // strike out words in opponent's list that user found
        Object[] list2 = opponentFoundWords.toArray();
        for (int j = 0; j < list2.length; j++) {
            if (foundWords.contains(list2[j])) {
                list2[j] = "<html><strike>" + list2[j] + "</strike></html>";
            }
        }
        opponentFoundWordsList.setListData(list2);

        // display dialog indicating which player won
        String winnerMessage = "";
        if      (playerScore > opponentScore) winnerMessage = "                   You win!\n\n";
        else if (playerScore < opponentScore) winnerMessage = "            The computer wins!\n\n";
        else                                  winnerMessage = "                     Tie!\n\n";
        String scoreMessage  = "                  Final score:\n          You: " +  playerScore + " - Computer: " + opponentScore;
        JOptionPane.showMessageDialog(this, winnerMessage + scoreMessage, "Game finished", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Timer that runs to keep track of the game time.
     */
    private class Countdown extends TimerTask {
        @Override
        public void run() {
            if (elapsedTime < GAME_TIME - 1) {
                elapsedTime++;
                String seconds = String.format("%02d", (GAME_TIME - elapsedTime) % SECONDS_PER_MINUTE);
                String minutes = String.format("%02d", (GAME_TIME - elapsedTime) / SECONDS_PER_MINUTE);
                String time = minutes + ":" + seconds;
                clock.setText(time);
            }
            else {
                endGame();
            }
        }
    }

    /**
     * Check the word entered in the text field or selected by clicks on the board
     * Pressing ENTER or clicking the Check Word button will call this.
     */
    private void checkWord() {
        String s;
        // 这里就是哪一个长就选哪一个
        if (entryField.getText().length() >= bp.getCurrentPath().length())
            s = entryField.getText().toUpperCase();
        else
            s = bp.getCurrentPath().toUpperCase();
        s = s.trim();
        if (s.equals("")) return;

        // 匹配上了这个单词之后，将所有的单词显示在list上面，并且清空文本输入框
        if (validWords.contains(s) && !foundWords.contains(s)) {
            foundWords.add(s);
            foundWordsList.setListData(foundWords.toArray());
            points += scoreWord(s);
            scoreLabel.setText("Current Points: " + points);
            entryField.setText("");
        }

        // 这里的测试点竟然是使用了递归，觉得很神奇。不过由于其的递归层数应该是只有一层，因为出现在后面的单词不可能是godmode
        // 所以这个地方也是一个注意点，不能出现godmode这样的一个单词。
        else if (s.equals("GODMODE")) {
            for (String str : solver.getAllValidWords(board)) {
                entryField.setText(str);
                checkWord();
            }
        }

        // 25%的正确答案
        else if (s.equals("GODMODE4")) {
            for (String str : solver.getAllValidWords(board)) {
                if (StdRandom.uniform(4) == 0) {
                    entryField.setText(str);
                    checkWord();
                }
            }
        }

        /*
            这里花了点时间去整理了一下 synchronized 线程锁相关的知识
            java 线程同步里面的相应的知识
        */
        // 这里的toolkit是一个系统工具包，相当于最底层的工具包，比如这里就直接用可以直接beep
        else {
            Toolkit.getDefaultToolkit().beep();
            entryField.setText("");
        }
    }

    /**
     * Score a word based off typical Boggle scoring.
     * @param s Word to score
     * @return Score of the word passed in 
     */
    private int scoreWord(String s) {
        int pointValue;
        int length = s.length();
        if      (length < 5)  pointValue = 1;
        else if (length == 5) pointValue = 2;
        else if (length == 6) pointValue = 3;
        else if (length == 7) pointValue = 5;
        else                  pointValue = 11;
        return pointValue;
    }

    /**
     * Class that displays the board for the user to interact with.
     * @author mdrabick
     */
    // 中间的选择框的面板
    private class BoardPanel extends JPanel {
        private int NUM_OF_CUBES = BOARD_ROWS * BOARD_COLS;
        private JLabel[] cubes = new JLabel[NUM_OF_CUBES];
        private int CUBE_DIM = 60;
        private int[] path;
        private boolean foundWord;

        /**
         * Constructor for the board which the user interacts with in order to play Boggle.
         */
        public BoardPanel() {
            //类似于qt里面的直接用代码控制格局，这里就是方格控制
            GridLayout cubeLayout = new GridLayout(BOARD_ROWS, BOARD_COLS);
            //设置大小和格局控制
            this.setPreferredSize(new Dimension(CUBE_DIM*BOARD_COLS, CUBE_DIM*BOARD_ROWS));
            this.setMinimumSize(this.getPreferredSize());
            this.setMaximumSize(this.getPreferredSize());
            this.setLayout(cubeLayout);

            /*
                这里特别需要注意下面的相应模板的写法，如果是我个人以前的写法的话，我应该会先给每一个cube初始化，然后再去考虑事件相应操作
                但是这里很不一样的是，在初始化的时候去给每一个cube定一个事件系统，然后记下相应的鼠标监听器
                然后再鼠标监听器的时候，用path数组记下路径，并且考虑三个步骤
                (只会在鼠标有响应的时候去操作)

                第一 如果path数组还没有初始化，初始化path数组，并且全部置位-1，然后记录当前位置在起点，画图。
                第二 如果 遍历path数组，针对每一个位置进行判断，比如重复位置，最后位置，以及相连接的位置，相同位置的取消操作
                第三 上色

                这里需要注意的是 判断是否相连的操作，这里是按照行相连接分成三种情况。然后判断是否在列的周围附近 其实写的特别奇怪，我可是花了好久才看懂的。
            */
            for (int i = 0; i < NUM_OF_CUBES; i++) {
                final int cur = i;
                cubes[i] = new JLabel("", JLabel.CENTER);                         // 给JLavel赋值 并且参数表示的是呈现在正中间。
                cubes[i].setFont(new Font("SansSerif", Font.PLAIN, 28));    // 字体设置类
                cubes[i].setPreferredSize(new Dimension(CUBE_DIM, CUBE_DIM));         // 设置最适合的尺寸
                cubes[i].setMinimumSize(cubes[i].getPreferredSize());                 // 设置最大尺寸
                cubes[i].setMaximumSize(cubes[i].getPreferredSize());                 // 设置最小尺寸
                cubes[i].setBorder(BorderFactory.createRaisedBevelBorder());          // 设置相应样式的边框
                cubes[i].setOpaque(true);                                             // 设置控件是否透明
                cubes[i].setBackground(new Color(146, 183, 219));             // 设置背景颜色
                cubes[i].addMouseListener(new MouseListener() {                       // 设置鼠标动作
                    @Override
                    public void mouseClicked(MouseEvent arg0) {
                        if (inGame) {
                            // 如果当前路径为空的话 初始化整个路径，并且将当前位置记为起点
                            if (path == null) {
                                path = new int[NUM_OF_CUBES];
                                for (int n = 0; n < path.length; n++) {
                                    path[n] = -1;
                                }
                                path[0] = cur;
                                highlightCubes();
                                return;
                            }

                            for (int j = 0; j < path.length; j++) {
                                // if it is the first cube clicked
                                if (j == 0 && path[j] == -1) {
                                    path[j] = cur;
                                    break;
                                }
                                // if the cube clicked is in the path
                                else if (path[j] == cur) {
                                    // check if it is the last cube or the last one in the current path
                                    //if so un-highlight it
                                    if (j == path.length-1 || path[j+1] == -1) {
                                        cubes[cur].setBackground(new Color(146, 183, 219));
                                        path[j] = -1;
                                    }
                                    break;
                                }
                                // 检查相连的联通块，然后再这个地方上色。
                                else if (path[j] == -1) {
                                    // row above
                                    if (path[j-1] >= cur-BOARD_COLS-1 && path[j-1] <= cur-BOARD_COLS+1)
                                        path[j] = cur;
                                        // next to (same row)
                                    else if (path[j-1] == cur-1 || path[j-1] == cur+1)
                                        path[j] = cur;
                                        // row below
                                    else if (path[j-1] >= cur+BOARD_COLS-1 && path[j-1] <= cur+BOARD_COLS+1)
                                        path[j] = cur;

                                    break;
                                }
                            }
                            highlightCubes();
                        }
                    }
                    @Override
                    public void mouseEntered(MouseEvent arg0) { }
                    @Override
                    public void mouseExited(MouseEvent arg0) { }
                    @Override
                    public void mousePressed(MouseEvent arg0) { }
                    @Override
                    public void mouseReleased(MouseEvent arg0) { }
                });

                // 键盘相应事件的操作
                cubes[i].addKeyListener(new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent arg0) { }
                    @Override
                    public void keyReleased(KeyEvent arg0) { }
                    @Override
                    public void keyTyped(KeyEvent arg0) {
                        int keyCode = arg0.getKeyCode();
                        // 这个地方是按下回车键就会出现checkword操作
                        if (keyCode == KeyEvent.VK_ENTER) {
                            checkWord();
                        }
                    }
                });
                // 将每一个方块放到面板中，因为前面设置了layout，所以这里添加就是按照上面的顺序进行添加。
                this.add(cubes[i]);
            }
        }

        /**
         * Clear the selected blocks (change from highlighted to unhighlighted).
         */
        public void clearSelection() {
            for (int i = 0; i < path.length; i++) {
                path[i] = -1;
                cubes[i].setBackground(new Color(146, 183, 219));
            }
        }

        /**
         * Get the word spelled by the selected path.
         * @return the word spelled out
         */
        public String getCurrentPath() {
            StringBuilder selectedWord = new StringBuilder(8);
            for (int s : path) {
                if (s < 0) break;
                selectedWord.append(cubes[s].getText().charAt(0));
                if (cubes[s].getText().charAt(0) == 'Q') selectedWord.append('U');
            }
            return selectedWord.toString();
        }

        /**
         * Set the board with a String array.
         *
         */
        public void setBoard() {
            String[] letters = new String[BOARD_ROWS * BOARD_COLS];
            for (int i = 0; i < BOARD_ROWS; i++) {
                for (int j = 0; j < BOARD_COLS; j++) {
                    char letter = board.getLetter(i, j);
                    if (letter == 'Q')
                        cubes[i*BOARD_COLS + j].setText("Qu");
                    else
                        cubes[i*BOARD_COLS + j].setText("" + letter);
                }
            }
        }

        /**
         * Highlight all the cubes in the path array.
         */
        public void highlightCubes() {
            for (int i = 0; i < path.length; i++) {
                if (path[i] == -1) break;
                cubes[path[i]].setBackground(new Color(232, 237, 76));
            }
        }

        /**
         * Un-highlight all the cubes in the path array.
         */
        public void unhighlightCubes() {
            if (path == null) return;
            for (int i = 0; i < path.length; i++) {
                if (path[i] == -1) break;
                cubes[path[i]].setBackground(new Color(146, 183, 219));
            }
        }

        /**
         * Highlight the correct cubes when typing.
         * @param s String to match on the board
         */
        public void matchWord(String s) {
            if (path != null) unhighlightCubes();
            path = new int[NUM_OF_CUBES];
            for (int i = 0; i < path.length; i++) {
                path[i] = -1;
            }

            foundWord = false;
            s = s.toUpperCase();
            //记得在用输入框写字母，显示在界面上的时候涉及一个匹配过程，然后这个匹配过程涉及到很多更变与交替。
            for (int i = 0; i < cubes.length; i++) {
                if (s.startsWith(cubes[i].getText().toUpperCase())) {
                    dfs(s, 0, 0, i / BOARD_COLS, i % BOARD_COLS);
                }
                if (foundWord) break;
            }
            if (foundWord) {
                highlightCubes();
            }
        }

        /**
         * Recursive helper method to search for a particular string on the board.
         * @param s 输入框上面的字符串
         * @param curChar Current char that is being sought
         * @param pathIndex Current number of cubes searched (only differs from curChar if there is a q in string) 
         * @param i 行
         * @param j 列
         */
        private void dfs(String s, int curChar, int pathIndex, int i, int j) {
            // 超出相应的边界，因为要深搜每一个位置是否与当前的位置去匹配
            // 这个函数存在的意义就是有一些匹配了两个单词，但是有一个更好的位置匹配了四个还是五个的位置
            // 所以这里的dfs需要做的是重新匹配一个最适合的位置，看看其的长度到当前的那些位置
            // 这里有一个非常小的细节，正是因为有QU这种特殊情况的存在，所以将curChar,pathIndex分开来算
            if (i < 0 || j < 0 || i >= BOARD_ROWS || j >= BOARD_COLS) return;

            // 当递归层数相加的curChar长度递增的时候，超过原本string的长度，就说明这个字符串在这个位置上面已经匹配到了
            if (curChar >= s.length()) {
                foundWord = true;
                return;
            }
            // can't use a cell more than once
            for (int n = 0; n < path.length; n++) {
                if (path[n] == (i*BOARD_COLS)+j) return;
            }

            // 直接返回字符串里面前一个字母是Q，但是下面一个字母不是U的字符串。
            if (curChar != 0 && s.charAt(curChar-1) == 'Q' && s.charAt(curChar) != 'U')
                return;

            // 这里就直接递增相应的QU的特殊情况。
            if (curChar != 0 && s.charAt(curChar-1) == 'Q' && s.charAt(curChar) == 'U')
                curChar += 1;

            // 之前很不理解这里的代码，明明上面存在这个代码，这个地方很不科学的重复一遍，是针对某些特殊情况，就是上面的那个特殊情况，
            // 不过如果把这段代码加到上面当前位置递增应该会更清楚
            if (curChar >= s.length()) {
                foundWord = true;
                return;
            }
            // 匹配到了一个错误的字母，直接返回
            if (cubes[(i*BOARD_COLS)+j].getText().charAt(0) != s.charAt(curChar)) {
                return;
            }

            // 记录下当前的位置，方便后面的高光行动。
            path[pathIndex] = (i*BOARD_COLS)+j;
            //visited[i][j] = true;
            // consider all neighbors
            for (int ii = -1; ii <= 1; ii++)
                for (int jj = -1; jj <= 1; jj++)
                    if (!foundWord) dfs(s, curChar+1, pathIndex+1, i + ii, j + jj);

            if (!foundWord) path[curChar] = -1;
        }
    }

    /**
     * Create the menu bar.
     */
    private void makeMenuBar() {
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        gameMenu.getAccessibleContext().setAccessibleDescription("This menu contains game options");
        menuBar.add(gameMenu);
        JMenuItem newGameMenuItem = new JMenuItem("New...", KeyEvent.VK_N);
        newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newGameMenuItem.getAccessibleContext().setAccessibleDescription("Starts a new game");
        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                newGame();
            }
        });
        JMenuItem endGameMenuItem = new JMenuItem("End Game", KeyEvent.VK_E);
        endGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        endGameMenuItem.getAccessibleContext().setAccessibleDescription("Ends the current game");
        endGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                endGame();
            }
        });
        gameMenu.add(newGameMenuItem);
        gameMenu.add(endGameMenuItem);
        gameMenu.addSeparator();
        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultySelection = new JRadioButtonMenuItem[NUMBER_OF_LEVELS];
        for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
            difficultySelection[i]  = new JRadioButtonMenuItem(LEVEL_DESCRIPTION[i % LEVEL_DESCRIPTION.length]); // mod as a check against mismatched sizes
            if (i == 0) difficultySelection[i].setSelected(true);
            difficultySelection[i].setActionCommand(LEVEL_DESCRIPTION[i % LEVEL_DESCRIPTION.length]);
            difficultySelection[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    for (int i = 0; i < LEVEL_DESCRIPTION.length; i++) {
                        if (ae.getActionCommand().equals(LEVEL_DESCRIPTION[i])) {
                            gameDifficulty = i;
                            //endGame();
                            newGame();
                            break;
                        }
                    }
                }
            });
            difficultyGroup.add(difficultySelection[i]);
            gameMenu.add(difficultySelection[i]);
        }
        JMenuItem quitMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        quitMenuItem.getAccessibleContext().setAccessibleDescription("Quits the program");
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                timer.cancel();
                System.exit(0);
            }
        });
        gameMenu.addSeparator();
        gameMenu.add(quitMenuItem);
        setJMenuBar(menuBar);
    }


    /**
     * @param args the dimension of the Boggle game
     */
    public static void main(final String[] args) {

        //这里建立一个 runnable 对象，有效防止线程阻塞
        //必须建立一个Runnable 对象 去执行run里面的事情。
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                int rows = 0;
                int cols = 0;
                if (args.length == 0) {
                    rows = 4;
                    cols = 4;
                }
                else if (args.length == 1) {
                    try {
                        rows = Integer.parseInt(args[0]);
                        cols = rows;
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Usage: java BoggleGame " +
                                "\nor:    java BoggleGame [rows]" +
                                "\nor:    java BoggleGame [rows] [cols]");
                        System.exit(1);
                    }
                }
                else if (args.length == 2) {
                    try {
                        rows = Integer.parseInt(args[0]);
                        cols  = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Usage: java BoggleGame " +
                                "\nor:    java BoggleGame [rows]" +
                                "\nor:    java BoggleGame [rows] [cols]");
                        System.exit(1);
                    }
                }
                else {
                    System.err.println("Usage: java BoggleGame " +
                            "\nor:    java BoggleGame [rows]" +
                            "\nor:    java BoggleGame [rows] [cols]");
                    System.exit(1);
                }

                if (rows <= 0 || cols <= 0) {
                    throw new java.lang.IllegalArgumentException("Rows and columns must be positive" +
                            "\nUsage: java BoggleGame " +
                            "\nor:    java BoggleGame [rows]" +
                            "\nor:    java BoggleGame [rows] [cols]");
                }
                new BoggleGame(rows, cols).setVisible(true);
            }
        });
    }

}