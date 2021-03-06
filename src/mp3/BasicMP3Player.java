package mp3;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class BasicMP3Player extends BasicPlayer {

	private BasicPlayer player = new BasicPlayer();
	private String currentFileName;
	private double currentVolumeValue;

	
	public BasicMP3Player(BasicPlayerListener listener) {
        player.addBasicPlayerListener(listener);
    }
	 
	 
	public void play(String fileName) {
		try {
			if (currentFileName != null && currentFileName.equals(fileName)
					&& player.getStatus() == BasicPlayer.PAUSED) {
				player.resume();
				return;
			}
			currentFileName = fileName;
			player.open(new File(fileName));
			player.play();
			player.setGain(currentVolumeValue);
		} catch (BasicPlayerException ex) {
			Logger.getLogger(BasicMP3Player.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void pause() {
		try {
			player.pause();
		} catch (BasicPlayerException ex) {
			Logger.getLogger(BasicMP3Player.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void stop() {
		try {
			player.stop();
		} catch (BasicPlayerException ex) {
			Logger.getLogger(BasicMP3Player.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	public void setVolume(int currentValue, int maximumValue) {
		try {
			this.currentVolumeValue = currentValue;

			if (currentValue == 0) {	
				player.setGain(0);
			} else {
				player.setGain(calcVolume(currentValue, maximumValue));
			}
			
		} catch (BasicPlayerException ex) {
			Logger.getLogger(BasicMP3Player.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private double calcVolume(int currentValue, int maximumValue) {
		currentVolumeValue = (double) currentValue / (double) maximumValue;
		return currentVolumeValue;
	}

	public void jump(long bytes) {
        try {
            player.seek(bytes);
            player.setGain(currentVolumeValue);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(BasicMP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
