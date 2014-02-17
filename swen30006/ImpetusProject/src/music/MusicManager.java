package music;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

/*
 * this class is for BGM, one channel only.
 * it's a singleton so it's outside the game states. just play music whenever
 */
public class MusicManager {
	private static MusicManager instance = null;
	
	//the list is only the filepath, so it doesn't load
	String[] musicList;
	int currentIndex;
	Music currentMusic;
	
	float currentPitch;
	float currentVolume;
	
	private MusicManager(){
		musicList = new String[3];
		musicList[0] = "assets/music/1.ogg";
		musicList[1] = "assets/music/2.ogg";
		musicList[2] = "assets/music/3.ogg";

		currentMusic = null;
		currentIndex = -1;
		
		//TODO make this static and changeable
		currentPitch = 1f;
		currentVolume = 0.3f;
	}
	
	public static MusicManager getInstance() throws SlickException{
		if (instance == null){
			instance = new MusicManager();
		}
		return instance;
	}
	
	private void loadBGM(int mus_index){
		try {
			currentMusic = new Music(musicList[mus_index]);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		currentIndex = mus_index;
	}
	
	public void playBGM(int mus_index){
		if (!(mus_index == currentIndex)){
			loadBGM(mus_index);
			currentMusic.loop(currentPitch, currentVolume);
		}else {
			if (!currentMusic.playing())
				currentMusic.resume();
		}
	}
	
	public void playBGM(){
		if(currentIndex==-1)
			return;
		if(!currentMusic.playing()){
			currentMusic.resume();
		}
	}
	
	public void fadeOutBGM(){
		//TODO not working, not sure why
		if (currentIndex == -1)
			return;
		if(currentMusic.playing()){
			currentMusic.fade(500, 0f, true);
		}
	}
	
	public void pauseBGM(){
		if(currentIndex==-1)
			return;
		if(currentMusic.playing()){
			currentMusic.pause();
		}
	}
}
