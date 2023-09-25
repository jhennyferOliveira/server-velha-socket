package view;

import controller.MainViewController;
import javafx.util.Pair;
import view.extensions.BoardButton;
import view.extensions.CustomColor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static view.extensions.CustomColor.*;

public class MainView {

    public Pair<String, String> occupied = new Pair<>("occupied", "-1"); // value -1 means that the place is not occupied

    public static MainView mainView;

    public static MainViewController controller = MainViewController.getInstance();

    public static synchronized MainView getInstance() {
        if (mainView == null)
            mainView = new MainView();
        return mainView;
    }

    public JFrame frame = new JFrame("#VELHA#");
    public JPanel boardPanel = new JPanel();
    public JPanel chatPanel = new JPanel();

    public JTextPane chatPane = new JTextPane();
    public JPanel placeholderPanel = new JPanel();

    public final Integer rowLength = 3;
    public final Integer colLength = 3;
    public JButton[][] bottomButtons = new JButton[rowLength][colLength];
    public JButton[][] middleButtons = new JButton[rowLength][colLength];
    public JButton[][] topButtons = new JButton[rowLength][colLength];
    public JButton randomNumberButton = new JButton();
    public JButton giveUpButton = new JButton();
    public JButton passTurnButton = new JButton();
    public JLabel randomNumberLabel = new JLabel();
    public JPanel initPanel = new JPanel();

    public JPanel endGamePanel = new JPanel();
    public JLabel endGameMessageTitle = new JLabel();

    public JLabel endGameMessageSubtitle = new JLabel();

    public JPanel blockPassTurnPanel = new JPanel();

    public JTextField inputField = new JTextField();

    public JLabel playerLabel = new JLabel();

    public JPanel playerLabelSquare = new JPanel();

    // SET UP PANELS
    public void setUpFrame() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        int frameWidth = 1100;
        int frameHeight = 900;
        frame.setSize(frameWidth, frameHeight);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainViewController.getInstance().closeWindow();
            }
        });

        setUpInitPanel();

        placeholderPanel.setBackground(Color.WHITE);
        placeholderPanel.setBounds(0, 0, 1100, 900);
        frame.add(placeholderPanel);

        setUpWinnerPanel();
        setUpBoardPanel();
        setUpChatPanel();
        setUpPlayerLabel();
        setUpMenuButtons();
        setUpPassTurnBlockPanel();

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setBounds(0, 0, 1100, 900);
        frame.add(backgroundPanel);
    }

    public void setUpInitPanel() {
        initPanel.setBackground(lightGrayCustom);
        initPanel.setLayout(null);
        initPanel.setOpaque(true);
        initPanel.setAlignmentX(0.5f); // Center horizontally
        initPanel.setAlignmentY(0.5f); // Center vertically
        initPanel.setBounds(250, 300, 600, 200);
        frame.add(initPanel);

        JLabel title = new JLabel();
        title.setText("Escolha do jogador");
        title.setVisible(true);
        Font largerFont = title.getFont().deriveFont(Font.BOLD, 17f);
        title.setFont(largerFont);
        title.setBounds(230, 20, 180, 30);
        initPanel.add(title);

        JLabel subtitle = new JLabel();
        subtitle.setText("O jogador que gerar o maior número aleatório entre 1-1000 irá iniciar o jogo.");
        subtitle.setVisible(true);
        subtitle.setBounds(60, 60, 500, 40);
        initPanel.add(subtitle);

        JLabel subtitle2 = new JLabel();
        subtitle2.setText("Para números iguais o sistema irá gerar um novo número para você.");
        subtitle2.setVisible(true);
        subtitle2.setBounds(75, 80, 500, 40);
        initPanel.add(subtitle2);

        randomNumberButton.setAlignmentX(0.5f); // Center horizontally
        randomNumberButton.setAlignmentY(0.5f); // Center vertically
        randomNumberButton.setBounds(210, 130, 150, 40);
        randomNumberButton.setFocusPainted(false);
        randomNumberButton.setContentAreaFilled(false);
        randomNumberButton.setText("gerar número");
        randomNumberButton.setOpaque(true);
        randomNumberButton.setBackground(Color.WHITE);


        randomNumberLabel.setBounds(160, 110, 300, 40);
        randomNumberLabel.setText("Aguardando seu adversário. Seu número é");
        randomNumberLabel.setVisible(false);
        initPanel.add(randomNumberLabel);

        controller.setUpRandomNumberButtonAction();

        randomNumberButton.setEnabled(true);
        initPanel.add(randomNumberButton);

    }

    public void setUpChatPanel() {
        var sender = "Você";
        chatPanel.setOpaque(false);
        chatPanel.setBounds(600, 100, 460, 600);
        chatPanel.setLayout(new BorderLayout());
        chatPane.setEditable(true);
        chatPane.setBackground(lightGrayCustom);

        JScrollPane scrollPane = new JScrollPane(chatPane);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        chatPanel.add(inputField, BorderLayout.SOUTH);
        inputField.setVisible(false);
        chatPanel.setVisible(false);

        inputField.addActionListener(e -> {
            String message = inputField.getText();
            addMessageToPane(sender, message, chatRed);
            controller.sendChatMessage(message);
            inputField.setText(""); // clear the input box
        });
        frame.add(chatPanel);
    }

    public void addMessageToPane(String sender, String message, Color color) {
        StyledDocument doc = chatPane.getStyledDocument();

        SimpleAttributeSet senderStyle = new SimpleAttributeSet();
        StyleConstants.setBold(senderStyle, true);
        StyleConstants.setForeground(senderStyle, color);

        SimpleAttributeSet messageStyle = new SimpleAttributeSet();

        try {
            doc.insertString(doc.getLength(), "  " + sender + ": ", senderStyle);
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
            System.out.println(message);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        chatPane.setCaretPosition(doc.getLength());
    }

    public void setUpBoardPanel() {
        boardPanel.setBackground(lightGrayCustom);
        boardPanel.setLayout(new GridLayout(3, 3, 30, 30));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        boardPanel.setBounds(40, 100, 520, 520);
        boardPanel.setOpaque(true);
        frame.add(boardPanel);

        for(int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                BoardButton bottomButton = new BoardButton();
                bottomButton.setAlignmentX(0.5f); // Center horizontally
                bottomButton.setAlignmentY(0.5f); // Center vertically
                bottomButton.setFocusPainted(false);
                bottomButton.setOpaque(true);
                bottomButton.setBackground(Color.WHITE);
                bottomButton.putClientProperty("move", "B" + "," + i + "," + j);
                bottomButton.putClientProperty(occupied.getKey(), occupied.getValue());
                bottomButton.setEnabled(true);
                bottomButton.setVisible(false);
                bottomButtons[i][j] = bottomButton;
                boardPanel.add(bottomButton);

                BoardButton middleButton = new BoardButton();
                middleButton.setBackground(Color.white);
                middleButton.setFocusPainted(false);
                middleButton.setAlignmentX(0.5f); // Center horizontally
                middleButton.setAlignmentY(0.5f); // Center vertically
                middleButton.setMaximumSize(new Dimension(85, 85));
                middleButton.setVisible(false);
                middleButton.putClientProperty("move", "M" + "," +  i + "," + j);
                middleButton.putClientProperty(occupied.getKey(), occupied.getValue());
                middleButtons[i][j] = middleButton;
                bottomButtons[i][j].add(middleButton);


                BoardButton topButton = new BoardButton();
                topButton.setOpaque(true);
                topButton.setBackground(Color.white);
                topButton.setFocusPainted(false);
                topButton.setAlignmentX(0.5f); // Center horizontally
                topButton.setAlignmentY(0.5f); // Center vertically
                topButton.setVisible(false);
                topButton.setMaximumSize(new Dimension(40, 40));
                topButton.putClientProperty("move", "T" + "," + i + "," + j);
                topButton.putClientProperty(occupied.getKey(), occupied.getValue());
                topButtons[i][j] = topButton;
                middleButtons[i][j].add(topButton);

            }
        }
    }


    public void setUpWinnerPanel() {
        endGamePanel.setBackground(blockGray);
        endGamePanel.setLayout(null);
        endGamePanel.setOpaque(true);
        endGamePanel.setBounds(0, 0, 1100, 900);
        frame.add(endGamePanel);

        endGameMessageTitle.setText("Parabéns você venceu!");
        endGameMessageTitle.setVisible(true);
        Font largerFont = endGameMessageTitle.getFont().deriveFont(Font.BOLD, 17f);
        endGameMessageTitle.setFont(largerFont);
        endGameMessageTitle.setBounds(450, 20, 210, 30);
        endGamePanel.add(endGameMessageTitle);

        endGameMessageSubtitle.setText("");
        endGameMessageSubtitle.setVisible(true);
        endGameMessageSubtitle.setBounds(477, 55, 200, 30);
        endGamePanel.add(endGameMessageSubtitle);
        endGamePanel.setVisible(false);
    }

    public void setUpPlayerLabel() {
        playerLabel.setVisible(true);
        playerLabel.setBounds(240, boardPanel.getHeight() + boardPanel.getY() + 16, 150, 30);
        frame.add(playerLabel);

        playerLabelSquare.setBounds(363, boardPanel.getHeight() + boardPanel.getY() + 27, 10, 10);
        playerLabelSquare.setBackground(boardRed);
        playerLabelSquare.setOpaque(true);
        frame.add(playerLabelSquare);
    }

    public void setUpLabelAndButtonVisibility() {
        randomNumberButton.setVisible(false);
        randomNumberLabel.setVisible(true);
    }

    public void removeInitialCardAndShowGameBoard() {
        frame.remove(initPanel);
        frame.repaint();
        placeholderPanel.setVisible(false);
        boardPanel.setVisible(true);
        chatPanel.setVisible(true);
        chatPanel.setEnabled(true);
        chatPane.setEnabled(true);
        chatPane.setVisible(true);
        setAllButtonsEnable(true, true);
    }

    public void setUpMenuButtons() {
        passTurnButton.setBounds(236, 700, 130, 40);
        passTurnButton.setFocusPainted(false);
        passTurnButton.setContentAreaFilled(false);
        passTurnButton.setText("Passar turno");
        passTurnButton.setVisible(false);
        passTurnButton.setFont(giveUpButton.getFont().deriveFont(Font.PLAIN, 15f));
        passTurnButton.setOpaque(true);
        passTurnButton.setBackground(CustomColor.passTurnGreen);
        passTurnButton.addActionListener(e -> {
            MainViewController.getInstance().setUpPassTurnButtonAction();
        });

        giveUpButton.setBounds(248, 760, 100, 40);
        giveUpButton.setFocusPainted(false);
        giveUpButton.setContentAreaFilled(false);
        giveUpButton.setVisible(false);
        giveUpButton.setText("Desistir");
        giveUpButton.setForeground(Color.white);
        giveUpButton.setFont(giveUpButton.getFont().deriveFont(Font.BOLD, 15f));
        giveUpButton.setOpaque(true);
        giveUpButton.setBackground(CustomColor.giveUpRed);
        giveUpButton.addActionListener(e -> {
            MainViewController.getInstance().giveUpAction();
        });
        frame.add(passTurnButton);
        frame.add(giveUpButton);
    }

    public void setUpPassTurnBlockPanel() {
        blockPassTurnPanel.setBounds(236, 700, 130, 40);
        blockPassTurnPanel.setOpaque(true);
        blockPassTurnPanel.setBackground(lightGrayCustom);
        blockPassTurnPanel.setVisible(false);
        frame.add(blockPassTurnPanel);
    }

    public void blockPassTurnButton() {
        blockPassTurnPanel.setVisible(true);
        passTurnButton.setEnabled(false);
    }

    public void releasePassTurnButton() {
        blockPassTurnPanel.setVisible(false);
        passTurnButton.setEnabled(true);
    }

    public void showEndGamePanel(String message) {
        endGamePanel.setVisible(true);
        endGameMessageSubtitle.setText(message);
    }

    public void setBoardButtonsEnable(Boolean enable, Boolean visible) {
        for(int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                bottomButtons[i][j].setEnabled(enable);
                bottomButtons[i][j].setVisible(visible);
                middleButtons[i][j].setEnabled(enable);
                middleButtons[i][j].setVisible(visible);
                topButtons[i][j].setEnabled(enable);
                topButtons[i][j].setVisible(visible);
            }
        }
    }

    public void setAllButtonsEnable(Boolean enable, Boolean visible) {
        setBoardButtonsEnable(enable, visible);
        giveUpButton.setEnabled(enable);
        passTurnButton.setEnabled(enable);
        inputField.setEnabled(enable);

        giveUpButton.setVisible(visible);
        passTurnButton.setVisible(visible);
        inputField.setVisible(visible);
    }
}





