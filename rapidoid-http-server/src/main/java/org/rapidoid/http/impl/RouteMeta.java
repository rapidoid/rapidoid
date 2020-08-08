/*-
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.http.DataSchema;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class RouteMeta extends RapidoidThing {

	private volatile String id;

	private volatile String summary;

	private volatile String description;

	private volatile Set<String> tags;

	private volatile DataSchema inputSchema;

	private volatile DataSchema outputSchema;

	private volatile Map<String, Object> responses;

	private volatile boolean publish = true;

	public String id() {
		return id;
	}

	public RouteMeta id(String id) {
		this.id = id;
		return this;
	}

	public String summary() {
		return summary;
	}

	public RouteMeta summary(String summary) {
		this.summary = summary;
		return this;
	}

	public String description() {
		return description;
	}

	public RouteMeta description(String description) {
		this.description = description;
		return this;
	}

	public Set<String> tags() {
		return tags;
	}

	public RouteMeta tags(Set<String> tags) {
		this.tags = tags;
		return this;
	}

	public DataSchema inputSchema() {
		return inputSchema;
	}

	public RouteMeta inputSchema(DataSchema inputSchema) {
		this.inputSchema = inputSchema;
		return this;
	}

	public DataSchema outputSchema() {
		return outputSchema;
	}

	public RouteMeta outputSchema(DataSchema outputSchema) {
		this.outputSchema = outputSchema;
		return this;
	}

	public Map<String, Object> responses() {
		return responses;
	}

	public RouteMeta responses(Map<String, Object> responses) {
		this.responses = responses;
		return this;
	}

	public boolean publish() {
		return publish;
	}

	public RouteMeta publish(boolean publish) {
		this.publish = publish;
		return this;
	}

	public RouteMeta copy() {
		RouteMeta copy = new RouteMeta();

		copy.id = this.id;
		copy.summary = this.summary;
		copy.description = this.description;
		copy.publish = this.publish;
		copy.tags = Coll.copyOf(U.safe(this.tags));
		copy.inputSchema = this.inputSchema;
		copy.outputSchema = this.outputSchema;
		copy.responses = Coll.deepCopyOf(U.safe(this.responses));

		return copy;
	}

	@Override
	public String toString() {
		return "RouteMeta{" +
			"id='" + id + '\'' +
			", summary='" + summary + '\'' +
			", description='" + description + '\'' +
			", tags=" + tags +
			", inputSchema=" + inputSchema +
			", outputSchema=" + outputSchema +
			", responses=" + responses +
			", publish=" + publish +
			'}';
	}
}
