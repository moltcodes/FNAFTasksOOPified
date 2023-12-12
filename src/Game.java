import javax.swing.*;
public abstract class Game extends JFrame{
    Sound sound = new Sound();
    public abstract void gameInitialize();
    public abstract void gameOver();
    public abstract void win();
    public abstract void gameRestart();

    public void playMusic(int i){
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic(){
        sound.stop();
    }

    public void playSE(int i){
        sound.setFile(i);
        sound.play();
    }
}
