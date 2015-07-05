package org.rapidoidx.inmem;

/*
 * #%L
 * rapidoid-x-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Prop;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RelPair {

	final String name;
	final Class<?> src;
	final Class<?> dest;
	final Prop srcProp;
	final Prop destProp;

	public RelPair(String name, Class<?> src, Class<?> dest, Prop srcProp, Prop destProp) {
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.srcProp = srcProp;
		this.destProp = destProp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((src == null) ? 0 : src.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelPair other = (RelPair) obj;
		if (dest == null) {
			if (other.dest != null)
				return false;
		} else if (!dest.equals(other.dest))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (src == null) {
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RelPair [name=" + name + ", src=" + src.getSimpleName() + ", dest=" + dest.getSimpleName()
				+ ", srcProp=" + srcProp + ", destProp=" + destProp + "]";
	}

}
