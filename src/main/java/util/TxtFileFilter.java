package util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A filename filter for javax.swing to return files with extension 'txt' only.
 */
public class TxtFileFilter extends FileFilter {
    public TxtFileFilter() {
    }

    public boolean accept(File file) {
        return (file.getName().endsWith(".txt"));
    }


    public String getDescription() {
        return "*.txt";
    }
}
