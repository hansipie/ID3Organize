package net.chrysalide.ID3Organize;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class Engine {

	private File sourceDir;
	private File destinationDir;

	public Engine(File srcDir, File destDir) {
		sourceDir = srcDir;
		destinationDir = destDir;
	}

	public boolean directoryCrawler() {
		if (!sourceDir.isDirectory())
			return false;

		for (File file : sourceDir.listFiles()) {
			if (file.isDirectory()) {
				directoryCrawler();
			} else {
				if (!copyMp3(file)) {
				}
			}
		}

		return true;
	}

	public boolean copyMp3(File inputFile) {

		MP3File mp3File = null;
		try {
			mp3File = new MP3File(inputFile);
		} catch (IOException | TagException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
