package utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MP3PlayerFileFilter extends FileFilter{
	
	private String fileExtension;
	private String fileDescription;
	
	

	public MP3PlayerFileFilter(String fileExtension, String fileDescription) {
		super();
		this.fileExtension = fileExtension;
		this.fileDescription = fileDescription;
	}

	@Override
	public boolean accept(File f) {// razrewit' tol'ko papki, i fa'lu .mp3
		return f.isDirectory() || f.getAbsolutePath().endsWith(fileExtension);
	}

	@Override
	public String getDescription() {// description for mp3 - file in change in dialog window 
		return fileDescription + " (*." + fileExtension + ")";
	}

}
