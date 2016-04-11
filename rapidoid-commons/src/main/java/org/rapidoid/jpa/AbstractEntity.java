package org.rapidoid.jpa;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public abstract class AbstractEntity {

	@Id
	@GeneratedValue
	public Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractEntity that = (AbstractEntity) o;
		return id != null ? id.equals(that.id) : that.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "id=" + id + '}';
	}
}
