package se.liu.simjolucul.dopeslope.highscore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighscoreList {

	private List<Highscore> highscores;
	private final String filename;
	private final boolean lowerIsBetter;

	public HighscoreList(String filename, boolean lowerIsBetter) {
		this.filename = filename;
		this.lowerIsBetter = lowerIsBetter;
		this.highscores = new ArrayList<>();
	}

	public void addScore(Highscore score) throws IOException {
		highscores.add(score);

		if (lowerIsBetter) {
			highscores.sort(Comparator.comparingInt(Highscore::getPoints));
		} else {
			highscores.sort((o1, o2) -> Integer.compare(o2.getPoints(), o1.getPoints()));
		}

		if (highscores.size() > 10) {
			highscores = new ArrayList<>(highscores.subList(0, 10));
		}

		save();
	}

	public List<Highscore> getHighscores() {
		return highscores;
	}

	public int getLength() {
		return highscores.size();
	}

	public void save() throws IOException {
		saveToFile(getFullPath(filename));
	}

	public void saveToFile(String fullPath) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File originalFile = new File(fullPath);
		File tempFile = new File(fullPath + ".tmp");

		File parentDir = originalFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException("Kunde inte skapa katalog: " + parentDir);
			}
		}

		try (FileWriter writer = new FileWriter(tempFile)) {
			gson.toJson(this, writer);
		}

		Files.move(tempFile.toPath(),
				originalFile.toPath(),
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.ATOMIC_MOVE);
	}

	public static HighscoreList load(String filename, boolean lowerIsBetter) {
		try {
			return loadFromFile(getFullPath(filename), filename, lowerIsBetter);
		} catch (FileNotFoundException e) {
			return new HighscoreList(filename, lowerIsBetter);
		}
	}

	public static HighscoreList loadFromFile(String fullPath, String filename, boolean lowerIsBetter) throws FileNotFoundException {
		Gson gson = new Gson();

		try (FileReader reader = new FileReader(fullPath)) {
			HighscoreList loaded = gson.fromJson(reader, HighscoreList.class);

			if (loaded == null || loaded.highscores == null) {
				return new HighscoreList(filename, lowerIsBetter);
			}

			HighscoreList result = new HighscoreList(filename, lowerIsBetter);
			result.highscores.addAll(loaded.highscores);

			if (lowerIsBetter) {
				result.highscores.sort(Comparator.comparingInt(Highscore::getPoints));
			} else {
				result.highscores.sort((o1, o2) -> Integer.compare(o2.getPoints(), o1.getPoints()));
			}

			if (result.highscores.size() > 10) {
				result.highscores = new ArrayList<>(result.highscores.subList(0, 10));
			}

			return result;
		} catch (IOException e) {
			FileNotFoundException ex = new FileNotFoundException(e.getMessage());
			ex.initCause(e);
			throw ex;
		}
	}

	private static String getFullPath(String filename) {
		return System.getProperty("user.home") + "/DopeSlopeData/" + filename;
	}
}