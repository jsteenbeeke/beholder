package com.jeroensteenbeeke.topiroll.beholder.util.compendium;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CompendiumIndexEntry implements Comparable<CompendiumIndexEntry> {
	private static final Comparator<CompendiumIndexEntry> comparator = Comparator.comparing(CompendiumIndexEntry::getPath);

	private final String path;

	private final String title;

	public CompendiumIndexEntry(String path, String title) {
		this.path = path;
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int compareTo(CompendiumIndexEntry other) {
		return comparator.compare(this, other);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CompendiumIndexEntry that = (CompendiumIndexEntry) o;
		return Objects.equals(path, that.path) &&
				Objects.equals(title, that.title);
	}

	@Override
	public int hashCode() {

		return Objects.hash(path, title);
	}
}
