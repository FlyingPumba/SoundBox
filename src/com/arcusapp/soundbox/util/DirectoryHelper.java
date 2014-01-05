/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013  Iván Arcuschin Moreno
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

package com.arcusapp.soundbox.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DirectoryHelper {
    public static List<String> getNamesFromFiles(List<File> fileList) {
        List<String> names = new ArrayList<String>();
        for (File f : fileList) {
            names.add(f.getName());
        }
        return names;
    }

    /**
     * Similar to android.os.Environment.getExternalStorageDirectory(), except that
     * here, we return all possible storage directories. The Environment class only
     * returns one storage directory. If you have an extended SD card, it does not
     * return the directory path. Here we are trying to return all of them.
     * 
     * @return
     */
    public static String[] getStorageDirectories() {
        String[] dirs = null;
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            List<String> list = new ArrayList<String>();
            String line;
            while ((line = bufReader.readLine()) != null) {
                if (line.contains("vfat") || line.contains("/mnt")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (s.equals(Environment.getExternalStorageDirectory().getPath())) {
                        list.add(s);
                    }
                    else if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb") && !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
                            list.add(s);
                        }
                    }
                }
            }

            dirs = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                dirs[i] = list.get(i);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                }
            }
        }

        return dirs;
    }

    public static String[] getOtherStorageDirectories() {
        File tempFile;
        String[] directories = null;
        String[] splits;
        ArrayList<String> arrayList = new ArrayList<String>();
        BufferedReader bufferedReader = null;
        String lineRead;

        try {
            arrayList.clear(); // redundant, but what the hey
            bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));

            while ((lineRead = bufferedReader.readLine()) != null) {
                splits = lineRead.split(" ");

                // System external storage
                if (splits[1].equals(Environment.getExternalStorageDirectory()
                        .getPath())) {
                    arrayList.add(splits[1]);
                    continue;
                }

                // skip if not external storage device
                if (!splits[0].contains("/dev/block/")) {
                    continue;
                }

                // skip if mtdblock device

                if (splits[0].contains("/dev/block/mtdblock")) {
                    continue;
                }

                // skip if not in /mnt node

                if (!splits[1].contains("/mnt")) {
                    continue;
                }

                // skip these names

                if (splits[1].contains("/secure")) {
                    continue;
                }

                if (splits[1].contains("/mnt/asec")) {
                    continue;
                }

                // Eliminate if not a directory or fully accessible
                tempFile = new File(splits[1]);
                if (!tempFile.exists()) {
                    continue;
                }
                if (!tempFile.isDirectory()) {
                    continue;
                }
                if (!tempFile.canRead()) {
                    continue;
                }
                if (!tempFile.canWrite()) {
                    continue;
                }

                // Met all the criteria, assume sdcard
                arrayList.add(splits[1]);
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }

        // Send list back to caller

        if (arrayList.size() == 0) {
            arrayList.add("sdcard not found");
        }
        directories = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            directories[i] = arrayList.get(i);
        }
        return directories;
    }
}