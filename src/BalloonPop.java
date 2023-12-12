import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BalloonPop extends Game implements Counter {
    private static final int NUM_BALLOONS = 10;
    private static final int MAX_ALLOWED_POPS = 5;
    private static final int BALLOON_SIZE = 50;
    private static final int SPACING = 20;
    private static final int TIMER_DELAY = 25; // milliseconds
    private int popsCount = 0;
    private Timer timer;
    private List<JButton> balloons;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BalloonPop());
    }

    public BalloonPop() {
        playMusic(9);
        gameInitialize();
    }

    public void gameInitialize() {
        setTitle("Balloon Pop Game");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        //--------------->CHANGE BACKGROUND
        String backgroundImagePath = "Resources/Pictures/placeholderBG.gif";

        try {
            ImageIcon backgroundImageIcon = new ImageIcon(getClass().getResource(backgroundImagePath));
            JLabel backgroundLabel = new JLabel(backgroundImageIcon);
            setContentPane(backgroundLabel);
            setLayout(null);

            balloons = new ArrayList<>();

            for (int i = 0; i < NUM_BALLOONS; i++) {
                JButton balloon = createBalloonButton();
                balloons.add(balloon);
                add(balloon);
            }

            arrangeBalloons();

            timer = new Timer(TIMER_DELAY, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveBalloons();
                }
            });
            timer.start();
        } catch (Exception e) {
            System.out.println("Image error!");
        }
    }

    @Override
    public void gameTimer() {
        //
    }

    @Override
    public void gameOver() {
        JOptionPane.showMessageDialog(this, "Game over! You hit Balloon Boy.");
    }

    private JButton createBalloonButton() {
        JButton balloon = new JButton();
        balloon.setSize(BALLOON_SIZE, BALLOON_SIZE);
        balloon.setContentAreaFilled(false);
        balloon.setBorderPainted(false);
        ImageIcon balloonIcon = getRandomBalloonImage();
        balloon.setIcon(balloonIcon);

        balloon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color balloonColor = balloon.getBackground();
                playSE(10);
                popsCount++;
                if (popsCount == MAX_ALLOWED_POPS) {
                    win();
                }
                balloon.setVisible(false);
            }
        });

        //-------------->CHANGE TO THE ABSOLUTE PATH OF THE PICTURE THAT SHOULD NOT BE CLICKED
        if (balloonIcon.getDescription().equals("D:\\School\\CIT\\Second Year\\CS227\\Codes\\FNAFTasks1\\src\\Resources\\Pictures\\balloonImages\\balloonBoy.gif")) {
            balloon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameRestart();
                }
            });
        }
        return balloon;
    }

    private ImageIcon getRandomBalloonImage() {
        //------------->CHANGE TO THE ABSOLUTE PATH OF THE FOLDER CONTAINING THE FILES (PICTURES HERE MUST HAVE ITS OWN FILE)
        String imagePath = "D:\\School\\CIT\\Second Year\\CS227\\Codes\\FNAFTasks1\\src\\Resources\\Pictures\\balloonImages";
        File folder = new File(imagePath);
        File[] files = folder.listFiles();

        if (files != null && files.length > 0) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(files.length);
            return new ImageIcon(files[randomIndex].getAbsolutePath());
        } else {
            return new ImageIcon();
        }
    }

    private void arrangeBalloons() {
        int x = SPACING;
        int y = getHeight() / 2 - BALLOON_SIZE / 2;

        for (JButton balloon : balloons) {
            balloon.setLocation(x, y);
            x += BALLOON_SIZE + SPACING;
        }
    }

    private void moveBalloons() {
        for (JButton balloon : balloons) {
            Point location = balloon.getLocation();
            if (location.x < getWidth()) {
                balloon.setLocation(location.x + 8, location.y);
            } else {
                balloon.setLocation(10 - BALLOON_SIZE, location.y);
            }
        }
    }

    public void gameRestart() {
        gameOver();
        timer.stop();
        stopMusic();
        for (JButton balloon : balloons) {
            remove(balloon);
        }
        getContentPane().removeAll();
        popsCount = 0;
        gameInitialize();
        revalidate();
        repaint();
    }

    public void win(){
        JOptionPane.showMessageDialog(BalloonPop.this, "Congratulations! You win!");
        timer.stop();
        stopMusic();
        dispose();
    }
}
