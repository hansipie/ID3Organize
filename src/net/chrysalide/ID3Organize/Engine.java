package net.chrysalide.ID3Organize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

public class Engine {

	private int _count;
	private int _error;
	private File _sourceDir;
	private File _destinationDir;
	private List<String> _strError;
	private int _totalCount;
	private int _prevPerCent;

	public Engine(File srcDir, File destDir) {
		_sourceDir = srcDir;
		_destinationDir = destDir;
		_count = 0;
		_error = 0;
		_strError = new ArrayList<String>();
		_totalCount = 0;
		_prevPerCent = 0;
	}

	public boolean directoryCrawler() {

		_totalCount = directoryCount(_sourceDir, _totalCount);
		System.out.println(String
				.format("Total Items count: %1$d", _totalCount));
		boolean ret = directoryCrawler(_sourceDir);
		System.out.println("-- Rapport --");
		for (String line : _strError) {
			System.out.println("error: " + line);
		}
		System.out.println(String.format("\ntotal: %1$d - erreurs: %2$d",
				_count, _error));
		return ret;
	}

	private boolean directoryCrawler(File input) {
		if (!input.isDirectory())
			return false;

		for (File file : input.listFiles()) {
			if (file.isDirectory()) {
				directoryCrawler(file);
			} else {
				if (!copyMp3(file)) {
					continue;
				}
			}
		}
		return true;
	}

	private int directoryCount(File input, int count) {
		if (!input.isDirectory())
			return count++;

		for (File file : input.listFiles()) {
			if (file.isDirectory()) {
				count = directoryCount(file, count);
			} else {
				count++;
			}
		}
		return count;
	}

	public boolean copyMp3(File inputFile) {

		String album;
		String artist;
		String title;

		_count++;
		progressCounter();
		try {
			MP3File mp3File = new MP3File(inputFile);

			ID3v1 id3v1 = mp3File.getID3v1Tag();
			AbstractID3v2 id3v2 = mp3File.getID3v2Tag();

			if (id3v2 != null) {
				album = id3v2.getAlbumTitle();
				artist = id3v2.getLeadArtist();
				title = id3v2.getSongTitle();
			} else if (id3v1 != null) {
				album = id3v1.getAlbum();
				artist = id3v1.getArtist();
				title = id3v1.getTitle();
			} else {
				String path = _destinationDir + File.separator + "_error"
						+ File.separator + "id3 not found" + File.separator
						+ inputFile.getName();
				File dest = new File(path);
				writeInPath(inputFile, dest);

				_strError.add(inputFile.getPath() + " - id3 not found");
				return false;
			}

		} catch (Exception e) {
			String path = _destinationDir + File.separator + "_error"
					+ File.separator
					+ validateStringForFilename(e.getMessage())
					+ File.separator + inputFile.getName();
			File dest = new File(path);
			writeInPath(inputFile, dest);

			_strError.add(inputFile.getPath() + " - Reason: " + e.getMessage());
			_error += 1;
			return false;
		}

		if (album.isEmpty()) {
			album = "_empty";
		} else {
			album = validateStringForFilename(album);
		}
		if (artist.isEmpty()) {
			artist = "_empty";
		} else {
			artist = validateStringForFilename(artist);
		}
		if (title.isEmpty()) {
			title = "_empty";
		} else {
			title = validateStringForFilename(title);
		}

		String path = _destinationDir + File.separator + artist
				+ File.separator + album + File.separator + inputFile.getName();

		File dest = new File(path);
		writeInPath(inputFile, dest);
		return true;
	}

	private String validateStringForFilename(String input) {
		if (input != null) {
		input = input.replaceAll("[^\\w\\s\\[\\]\\(\\),\\.]", "_");
		} else{
			input = "null";
		}
		return input;
	}

	private boolean writeInPath(File file, File dest) {
		try {
			Files.createDirectories(dest.getParentFile().toPath());
			Files.copy(file.toPath(), dest.toPath());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void progressCounter() {
		int perCent = (_count * 100) / _totalCount;
		if (perCent != _prevPerCent) {
			System.out.println(String.format("Progress: %1$d", perCent) + "%");
		}
		_prevPerCent = perCent;
	}
}
