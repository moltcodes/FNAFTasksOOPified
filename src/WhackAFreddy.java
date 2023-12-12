import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class WhackAFreddy extends Game implements Counter {
    private static final int GRID_SIZE = 6;
    private static final int MOLE_COUNT = 10;
    private JButton[][] buttons;
    private JLabel scoreLabel;
    private Sound sound = new Sound();
    private Timer timer;
    private Font pixelFont;
    private Random random;
    private int score = 0;
    private Cursor hammerCursor;
    private Cursor hammerCursorWhack;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhackAFreddy());
    }

    public WhackAFreddy() {
        setTitle("Whack-a-Freddy");
        setSize(450, 460);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buttons = new JButton[GRID_SIZE][GRID_SIZE];
        scoreLabel = new JLabel("Score: 0");
        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(Color.ORANGE);
        scorePanel.add(scoreLabel);
        FlowLayout layout = (FlowLayout)scorePanel.getLayout();
        layout.setVgap(0);
        layout.setHgap(0);
        random = new Random();
        add(scorePanel, BorderLayout.NORTH);
        gameInitialize();
        gameTimer();
        setVisible(true);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);

        playMusic(4);

        try {
            InputStream is = getClass().getResourceAsStream("Resources/Fonts/Minecraft.ttf");
            assert is != null;
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
        } catch (Exception e) {
            System.out.println("Font error!");
        }

        scoreLabel.setFont(pixelFont);

        try {
            Image hammerImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("Resources/Pictures/hammer.png"));
            Image hammerWhack = Toolkit.getDefaultToolkit().getImage(getClass().getResource("Resources/Pictures/hammerWhack.png"));
            hammerCursor = Toolkit.getDefaultToolkit().createCustomCursor(hammerImage, new Point(0, 0), "hammerCursor");
            hammerCursorWhack = Toolkit.getDefaultToolkit().createCustomCursor(hammerWhack, new Point(0, 0), "hammerCursorWhack");
        } catch (Exception e) {
            System.out.println("Error loading hammer cursor image");
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(hammerCursor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                setCursor(hammerCursorWhack);
                Timer timer = new Timer(400, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setCursor(hammerCursor);
                        ((Timer) e.getSource()).stop();
                    }
                });
                timer.start();
            }
        });
    }

    public void gameInitialize() {
        JPanel grid = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        grid.setBackground(Color.ORANGE);
        add(grid, BorderLayout.CENTER);
        ImageIcon icon = emptyWhack(70,70);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = new JButton();
                grid.add(buttons[i][j]);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setIcon(icon);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].addActionListener(new MoleListener(i, j));
                buttons[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setCursor(hammerCursor);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        setCursor(Cursor.getDefaultCursor());
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        setCursor(hammerCursorWhack);
                        Timer timer = new Timer(400, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setCursor(hammerCursor);
                                ((Timer) e.getSource()).stop();
                            }
                        });
                        timer.start();
                    }

                });
            }
        }
    }
    private ImageIcon emptyWhack(int width, int height) {
        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getResource("Resources/Pictures/emptyWhack.png")));
            Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } catch (IOException e) {
            System.out.println("Error loading emptyWhack.png");
            return null;
        }
    }

    public void gameTimer() {
        timer = new Timer(900, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideMoles();
                showRandomMole();
            }
        });
        timer.start();
    }

    private void hideMoles() {
        ImageIcon icon = emptyWhack(70,70);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j].setIcon(icon);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].setText("");
            }
        }
    }

    private void showRandomMole() {
        int row = random.nextInt(GRID_SIZE);
        int col = random.nextInt(GRID_SIZE);

        try{
            Image img;
            if(row%2 == 0){
                img = ImageIO.read(Objects.requireNonNull(getClass().getResource("Resources/Pictures/freddy.png")));
                buttons[row][col].setText("1");
            }else{
                img = ImageIO.read(Objects.requireNonNull(getClass().getResource("Resources/Pictures/purpleGuy.png")));
                buttons[row][col].setText("0");
            }
            Image newImg = img.getScaledInstance(70, 70, java.awt.Image.SCALE_SMOOTH);
            buttons[row][col].setIcon(new ImageIcon(newImg));
        }catch (ImagingOpException | IOException e){
            System.out.println("Freddy error!");
        }

        if(score == 10){
            win();
        }

    }

    public void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(null, "You whacked Purple Man! Try again?");
        gameRestart();
    }

    public void gameRestart(){
        timer.start();
        hideMoles();
        gameInitialize();
        score = 0;
        scoreLabel.setText("Score: "+score);
    }

    public void win(){
        JOptionPane.showMessageDialog(WhackAFreddy.this, "Congratulations, great whacker!");
        timer.stop();
        stopMusic();
        dispose();
    }

    private class MoleListener implements ActionListener {
        private int row;
        private int col;

        public MoleListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("1")){
                playSE(5);
                ++score;
                scoreLabel.setText("Score: "+score);
            }else if(buttons[row][col].getText().equals("0")){
                gameOver();
            }else{
                playSE(6);
            }
        }
    }
}
