package org.rapidoid.io;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Arrays;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class ResKey {

	final String filename;
	final String[] possibleLocations;

	public ResKey(String filename, String[] possibleLocations) {
		this.filename = filename;
		this.possibleLocations = possibleLocations;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResKey resKey = (ResKey) o;

		if (!filename.equals(resKey.filename)) return false;

		return Arrays.equals(possibleLocations, resKey.possibleLocations);
	}

	@Override
	public int hashCode() {
		int result = filename.hashCode();
		result = 31 * result + Arrays.hashCode(possibleLocations);
		return result;
	}

}
