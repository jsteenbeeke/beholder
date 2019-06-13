/**
 * This file is part of Beholder
 * (C) 2016-2019 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
