package net.ionoff.player.handler;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MediaFileFilter {
	/**
     * From the vlc_interface.h include file.
     */
    private static final String[] EXTENSIONS_AUDIO = {
        "3ga",
        "669",
        "a52",
        "aac",
        "ac3",
        "adt",
        "adts",
        "aif",
        "aifc",
        "aiff",
        "amb",
        "amr",
        "aob",
        "ape",
        "au",
        "awb",
        "caf",
        "dts",
        "flac",
        "it",
        "kar",
        "m4a",
        "m4b",
        "m4p",
        "m5p",
        "mid",
        "mka",
        "mlp",
        "mod",
        "mpa",
        "mp1",
        "mp2",
        "mp3",
        "mpc",
        "mpga",
        "mus",
        "oga",
        "ogg",
        "oma",
        "opus",
        "qcp",
        "ra",
        "rmi",
        "s3m",
        "sid",
        "spx",
        "tak",
        "thd",
        "tta",
        "voc",
        "vqf",
        "w64",
        "wav",
        "wma",
        "wv",
        "xa",
        "xm"
    };

    /**
     * Single instance.
     */
    public static final MediaFileFilter INSTANCE = new MediaFileFilter();

    /**
     * Create a new file filter.
     */
    public MediaFileFilter() {
    	 this.extensions = Arrays.copyOf(EXTENSIONS_AUDIO, EXTENSIONS_AUDIO.length);
         Arrays.sort(this.extensions);
         // Make a hash-set for faster look-up
         for(String extension : extensions) {
             extensionsSet.add(extension);
         }
    }
    
    /**
     * The recognised file extensions.
     */
    private final String[] extensions;

    /**
     * Set of recognised file extensions.
     */
    private final Set<String> extensionsSet = new HashSet<String>();

    /**
     * Get the recognised file extensions.
     * <p>
     * A sorted copy of the array of file extensions is returned.
     *
     * @return file extensions accepted by the filter
     */
    public String[] getExtensions() {
        // The array is already sorted
        return Arrays.copyOf(extensions, extensions.length);
    }

    /**
     * Get the set of recognised file extensions.
     * <p>
     * A new (copy) sorted set of file extensions is returned.
     *
     * @return set of file extensions accepted by the filter
     */
    public Set<String> getExtensionSet() {
        return new TreeSet<String>(extensionsSet);
    }

    public boolean accept(File pathname) {
        if(pathname.isFile()) {
            String name = pathname.getName();
            int dot = name.lastIndexOf('.');
            if(dot != -1 && dot + 1 < name.length()) {
                String extension = name.substring(dot + 1).toLowerCase();
                return extensionsSet.contains(extension);
            }
        }
        return false;
    }
}
