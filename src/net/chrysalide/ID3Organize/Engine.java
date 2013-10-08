package net.chrysalide.ID3Organize;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

public class Engine {

	private int _count;
	private int _error;
	private File _sourceDir;
	private File _destinationDir;
	private String _format;

	public Engine(File srcDir, File destDir, String format) {
		_sourceDir = srcDir;
		_destinationDir = destDir;
		_format = format;
		_count = 0;
		_error = 0;
	}

	public boolean directoryCrawler() {
		if (!_sourceDir.isDirectory())
			return false;

		for (File file : _sourceDir.listFiles()) {
			if (file.isDirectory()) {
				directoryCrawler();
			} else {
				if (!copyMp3(file)) {
					return false;
				}
			}
		}

		System.out.println(String.format("count:%1$d - error:%2$d", _count,
				_error));

		return true;
	}

	public boolean copyMp3(File inputFile) {

		MP3File mp3File = null;
		try {
			mp3File = new MP3File(inputFile);

			ID3v1 id3 = mp3File.getID3v1Tag();
			if (id3 != null) {
				System.out.println(String.format(
						"Path:%1$s - artist:%2$s - title:%3$s", inputFile,
						id3.getArtist(), id3.getTitle()));
			} else {
				System.out.println(String.format("Error:%1$s", inputFile));
				_error += 1;
			}
			_count += 1;

		} catch (IOException | TagException e) {
			System.err.println("MP3 read failed.  Reason: " + e.getMessage());
			return false;
		}

		return true;
	}

}
