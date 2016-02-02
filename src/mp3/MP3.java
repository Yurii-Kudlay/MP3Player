package mp3;

import java.io.Serializable;

import utils.FileUtils;

public class MP3 implements Serializable {

	private String name;
	private String path;
	
	public MP3(String name, String path) {
		super();
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return FileUtils.getFileNameWithoutExtension(name);
	}
	
}
