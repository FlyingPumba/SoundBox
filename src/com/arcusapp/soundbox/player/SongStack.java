/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013 Iván Arcuschin Moreno
 *
 * This file is part of SoundBox.
 *
 * SoundBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * SoundBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arcusapp.soundbox.player;

import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.data.SoundBoxPreferences;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        this.songsID = new ArrayList<String>();
        this.songsID.addAll(songsID);

        this.currentSong = mediaProvider.getSongFromID(songsID.get(firstPosition));

        randomGenerator = new Random();
        currentSongsIDList = new ArrayList<String>();
        setRandomState(randomState);
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
                currentPosition = songsID.size() - 1;
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

    public void moveStackForward() {
        if (randomState != RandomState.Random) {
            if (currentPosition == songsID.size() - 1) {
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
        return currentSongsIDList.get(currentPosition);
    }

    private String previousId() {
        if (randomState != RandomState.Random) {
            if (currentPosition == 0) {
                return currentSongsIDList.get(currentSongsIDList.size() - 1);
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
            if (currentPosition == currentSongsIDList.size() - 1) {
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
        this.randomState = state;
        String currentID = currentSong.getID();
        switch (state) {
            case Off:
                currentSongsIDList.clear();
                currentSongsIDList.addAll(songsID);
                currentPosition = currentSongsIDList.indexOf(currentID);
                updateStack();
                break;
            case Shuffled:
                currentSongsIDList.clear();
                currentSongsIDList.addAll(songsID);
                Collections.shuffle(currentSongsIDList);
                currentPosition = currentSongsIDList.indexOf(currentID);
                updateStack();
                break;
            case Random:
                break;
        }
        
        // store the songs on preferences
        SoundBoxPreferences.LastSongs.setLastSongs(currentSongsIDList);
    }

    public RandomState getCurrentRandomState() {
        return randomState;
    }
}