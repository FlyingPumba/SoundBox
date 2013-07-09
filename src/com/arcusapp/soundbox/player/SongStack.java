package com.arcusapp.soundbox.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.Song;

public class SongStack {
	private MediaProvider mediaProvider;
	private int currentPosition;
	private List<String> songsID;
	private List<String> currentSongsIDList;

	private Song currentSong;
	private Song previousSong;
	private Song nextSong;

	private RandomState randomState = RandomState.Off;

	private Random randomGenerator;

	public SongStack(int firstPosition, List<String> songsID, RandomState randomState) {
		mediaProvider = new MediaProvider();
		this.currentPosition = firstPosition;
		this.songsID = songsID;

		randomGenerator = new Random();
		currentSongsIDList = new ArrayList<String>();
		this.setRandomState(randomState);

		updateStack();
	}

	public Song getCurrentSong() {
		return currentSong;
	}

	public List<String> getCurrentSongsIDList() {
		return currentSongsIDList;
	}

	public void moveStackBackward() {
		if (randomState != RandomState.Random) {
			if (currentPosition == 0) {
				currentPosition = songsID.size();
			} else {
				currentPosition--;
			}
			updateStack();
		} else {
			nextSong = currentSong;
			currentSong = previousSong;
			previousSong = mediaProvider.getSongFromID(previousId());
		}
	}

	public void moveStackFoward() {
		if (randomState != RandomState.Random) {
			if (currentPosition == songsID.size()) {
				currentPosition = 0;
			} else {
				currentPosition++;
			}
			updateStack();
		} else {
			previousSong = currentSong;
			currentSong = nextSong;
			nextSong = mediaProvider.getSongFromID(nextId());
		}
	}

	private void updateStack() {
		currentSong = mediaProvider.getSongFromID(currentId());
		previousSong = mediaProvider.getSongFromID(previousId());
		nextSong = mediaProvider.getSongFromID(nextId());
	}

	private String currentId() {
		return songsID.get(currentPosition);
	}

	private String previousId() {
		if (randomState != RandomState.Random) {
			if (currentPosition == 0) {
				return currentSongsIDList.get(currentSongsIDList.size());
			} else {
				return currentSongsIDList.get(currentPosition - 1);
			}
		} else {
			int index = randomGenerator.nextInt(currentSongsIDList.size());
			return currentSongsIDList.get(index);
		}
	}

	private String nextId() {
		if (randomState != RandomState.Random) {
			if (currentPosition == currentSongsIDList.size()) {
				return currentSongsIDList.get(0);
			} else {
				return currentSongsIDList.get(currentPosition + 1);
			}
		} else {
			int index = randomGenerator.nextInt(currentSongsIDList.size());
			return currentSongsIDList.get(index);
		}
	}

	public void setRandomState(RandomState state) {
		if (state != randomState) {
			this.randomState = state;

			switch (state) {
				case Off:
					currentSongsIDList.clear();
					currentSongsIDList = songsID;
					break;
				case Shuffled:
					currentSongsIDList.clear();
					currentSongsIDList = songsID;
					Collections.shuffle(currentSongsIDList);
					break;
				case Random:
					break;
			}
		}
	}

	public RandomState getCurrentRandomState() {
		return randomState;
	}
}
