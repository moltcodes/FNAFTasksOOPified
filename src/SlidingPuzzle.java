import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;


public class SlidingPuzzle extends Game implements Counter {
    private JButton[][] puzzleButtons;
    private JLabel counterLabel;
    private int emptyRow, emptyCol;
    private Timer timer;
    int second, minute, minuteFlag;
    public Font pixelFont;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SlidingPuzzle());
    }
    public SlidingPuzzle() {
        setResizable(false);
        setTitle("Fix Bonnie");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 750);
        setLocationRelativeTo(null);

        try{
            InputStream is = getClass().getResourceAsStream("Resources/Fonts/Minecraft.ttf");
           assert is != null;
           pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
        }catch (Exception e) {
            System.out.println("Font error!");
        }

        puzzleButtons = new JButton[3][3];
        counterLabel = new JLabel();
        second = 15;
        minute = 1;
        minuteFlag = 0;
        gameTimer();
        timer.start();

        JPanel puzzlePanel = new JPanel(new GridLayout(3, 3));

        gameInitialize();
        shufflePuzzle();

        for (int i = 0; i < puzzleButtons.length; i++) {
            for (int j = 0; j < puzzleButtons[i].length; j++) {
                puzzlePanel.add(puzzleButtons[i][j]);
                puzzleButtons[i][j].addActionListener(new PuzzleButtonListener());
                puzzleButtons[i][j].setBorder(new MatteBorder(0,0,0,0,Color.BLACK));
                puzzleButtons[i][j].setBackground(Color.WHITE);
            }
        }

        add(counterLabel, BorderLayout.NORTH);
        counterLabel.setFont(pixelFont);
        counterLabel.setBackground(Color.decode("#0D0D0D"));
        counterLabel.setOpaque(true);
        counterLabel.setForeground(Color.decode("#5F5085"));
        counterLabel.setHorizontalAlignment(JLabel.CENTER);
        //Uses Factory Pattern?? JAJAJA
        counterLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        counterLabel.setBounds(300, 320, 200, 200);
        add(puzzlePanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void gameInitialize() {
        playMusic(2);
        int count = 1;
        String imgName;
        for (int i = 0; i < puzzleButtons.length; i++) {
            for (int j = 0; j < puzzleButtons[i].length; j++) {
                int number = i * puzzleButtons.length + j + 1;
                puzzleButtons[i][j] = new JButton(String.valueOf(number));

                try{
                    imgName = "Resources/Pictures/bonnieCut"+count+".jpg";
                    Image img = ImageIO.read(Objects.requireNonNull(getClass().getResource(imgName)));
                    Image newImg = img.getScaledInstance(260, 260, java.awt.Image.SCALE_SMOOTH);
                    puzzleButtons[i][j].setIcon(new ImageIcon(newImg));
                    puzzleButtons[i][j].setBorderPainted(true);
                    if(count != 8){
                        count++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Picture error!");
                }
            }
        }
        emptyRow = puzzleButtons.length - 1;
        emptyCol = puzzleButtons[0].length - 1;
        puzzleButtons[emptyRow][emptyCol].setText("");
    }

    private void shufflePuzzle() {
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            int direction = rand.nextInt(4);
            moveTile(direction);
        }
    }

    private void moveTile(int direction) {
        int newRow = emptyRow;
        int newCol = emptyCol;

        switch (direction) {
            case 0: //up
                newRow = Math.min(emptyRow + 1, puzzleButtons.length - 1);
                break;
            case 1: //down
                newRow = Math.max(emptyRow - 1, 0);
                break;
            case 2: //left
                newCol = Math.min(emptyCol + 1, puzzleButtons[0].length - 1);
                break;
            case 3: //right
                newCol = Math.max(emptyCol - 1, 0);
                break;
        }

        puzzleButtons[emptyRow][emptyCol].setIcon(puzzleButtons[newRow][newCol].getIcon());
        puzzleButtons[newRow][newCol].setIcon(null);
        puzzleButtons[emptyRow][emptyCol].setText(puzzleButtons[newRow][newCol].getText());
        puzzleButtons[newRow][newCol].setText("");
        emptyRow = newRow;
        emptyCol = newCol;
    }


    private class PuzzleButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int row = -1, col = -1;

            //PANGITAA position
            for (int i = 0; i < puzzleButtons.length; i++) {
                for (int j = 0; j < puzzleButtons[i].length; j++) {
                    if (puzzleButtons[i][j] == button) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            // checks if empty button is beside empty space
            if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) || (row == emptyRow && Math.abs(col - emptyCol) == 1)) {
                puzzleButtons[emptyRow][emptyCol].setIcon(button.getIcon());
                puzzleButtons[row][col].setIcon(null);
                puzzleButtons[emptyRow][emptyCol].setText(button.getText());
                puzzleButtons[row][col].setText("");
                emptyRow = row;
                emptyCol = col;

                playSE(1);
                
                if (isPuzzleSolved()) {
                    win();
                }
            }
        }

        private boolean isPuzzleSolved() {
            int expectedValue = 1;

            for (int i = 0; i < puzzleButtons.length; i++) {
                for (int j = 0; j < puzzleButtons[i].length; j++) {
                    String buttonText = puzzleButtons[i][j].getText();

                    if (buttonText.isEmpty()) {
                        if (!(i == puzzleButtons.length - 1 && j == puzzleButtons[i].length - 1)) {
                            return false;
                        }
                    } else {
                        int value = Integer.parseInt(buttonText);

                        if (value != expectedValue) {
                            return false;
                        }
                        expectedValue = (expectedValue % (puzzleButtons.length * puzzleButtons[0].length)) + 1;
                    }
                }
            }

            return true;
        }
    }

    @Override
    public void gameOver(){
        counterLabel.setText("You ran out of time!");
        JOptionPane.showMessageDialog(null, "You failed. Try again?");
        second = 15;
        minute = 1;
        minuteFlag = 0;
        gameRestart();
    }

    @Override
    public void win() {
        JOptionPane.showMessageDialog(SlidingPuzzle.this, "Congratulations! Puzzle solved!");
        stopMusic();
        dispose();
    }

    @Override
    public void gameRestart() {
        shufflePuzzle();
    }

    @Override
    public void gameTimer(){
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                second--;
                if(second < 0 && minuteFlag == 0){
                    minuteFlag = 1;
                    second = 59;
                    minute = 0;
                }
                if(minuteFlag == 0){
                    if(second<10){
                        counterLabel.setText(minute+":0"+second);
                    }else{
                        counterLabel.setText(minute+":"+second);
                    }
                }else{
                    if(second<10){
                        counterLabel.setText("0"+second);
                    }else{
                        counterLabel.setText(""+second);
                    }
                }

                if(second < 0 && minute == 0){
                    gameOver();
                }
            }
        });
    }
}
