package net.chrysalide.ID3Organize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public class Engine {

	final String regex = "[:\\\\/*?|<>]";

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
				if (!moveMp3(file)) {
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

	public boolean moveMp3(File inputFile) {

		String album;
		String artist;
		String title;

		_count++;
		progressCounter();
		try {
			Mp3File mp3File = new Mp3File(inputFile.getPath());

			if (mp3File.hasId3v1Tag()) {
				ID3v1 id3v1 =  mp3File.getId3v1Tag();
				album = id3v1.getAlbum();
				artist = id3v1.getArtist();
				title = id3v1.getTitle();
			} else if (mp3File.hasId3v2Tag()) {
				ID3v2 id3v2 =  mp3File.getId3v2Tag();
				album = id3v2.getAlbum();
				artist = id3v2.getArtist();
				if ((artist == null) || artist.isEmpty()){
					artist = id3v2.getOriginalArtist();
				}
				if ((artist == null) || artist.isEmpty()){
					artist = id3v2.getAlbumArtist();
				}
				title = id3v2.getTitle();
			} else {
				String path = _destinationDir + File.separator + "_error"
						+ File.separator + "id3 not found" + File.separator
						+ inputFile.getName();
				File dest = new File(path);
				moveFile(inputFile, dest);

				_strError.add(inputFile.getPath() + " - id3 not found");
				return false;
			}

		} catch (Exception e) {
			String path = _destinationDir + File.separator + "_error"
					+ File.separator + validateString(e.getMessage()) + File.separator
					+ inputFile.getName();
			File dest = new File(path);
			moveFile(inputFile, dest);

			_strError.add(inputFile.getPath() + " - Reason: " + e.getMessage());
			_error += 1;
			return false;
		}

		if ((album == null) || album.isEmpty()) {
			album = "_empty";
		} else {
			album = validateString(album);
		}
		if ((artist == null) || artist.isEmpty()) {
			artist = "_empty";
		} else {
			artist = validateString(artist);
		}
		if ((title == null) || title.isEmpty()) {
			title = "_empty";
		} else {
			title = validateString(title);
		}

		String path = _destinationDir + File.separator + artist
				+ File.separator + album + File.separator + inputFile.getName();

		path = path.replace("...", "");
		path = path.replace("\"", "");
		File dest = new File(path);
		moveFile(inputFile, dest);
		return true;
	}

	private String validateString(String input){
		//System.out.println("<-- " + input);
		input = input.replaceAll(regex, "_");
		//System.out.println("--> " + input);
		return input;
	}

	private boolean moveFile(File file, File dest) {
		try {
			System.out.println("Move " + file.toString() + " to " + dest.toString());
			createDirectory(dest.getParentFile());
			Files.move(file.toPath(), dest.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (NoSuchFileException e2) {
			e2.printStackTrace();
			return false;						
		} catch (InvalidPathException e1){
			e1.printStackTrace();
			return false;				
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void createDirectory(File path){
		if (path.exists())
			return;
		if (path.getParentFile().exists()){
			try {
				System.out.println("Create Directory " + path.toString());
				Files.createDirectories(path.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			createDirectory(path.getParentFile());
		}
	}
	
	private void progressCounter() {
		int perCent = (_count * 100) / _totalCount;
		if (perCent != _prevPerCent) {
			System.out.println(String.format("Progress: %1$d", perCent) + "%");
		}
		_prevPerCent = perCent;
	}
}
