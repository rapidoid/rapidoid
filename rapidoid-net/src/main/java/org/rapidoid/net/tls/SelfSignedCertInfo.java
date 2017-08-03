package org.rapidoid.net.tls;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class SelfSignedCertInfo extends RapidoidThing {

	private volatile String name = "";
	private volatile String organization = "";
	private volatile String unit = "";
	private volatile String locality = "";
	private volatile String state = "";
	private volatile String country = "";

	private volatile String alias;
	private volatile char[] password;

	private volatile int keysize = 1024; // bits
	private volatile long validity = 365; // days

	public String name() {
		return name;
	}

	public SelfSignedCertInfo name(String name) {
		this.name = name;
		return this;
	}

	public String organization() {
		return organization;
	}

	public SelfSignedCertInfo organization(String organization) {
		this.organization = organization;
		return this;
	}

	public String unit() {
		return unit;
	}

	public SelfSignedCertInfo unit(String unit) {
		this.unit = unit;
		return this;
	}

	public String locality() {
		return locality;
	}

	public SelfSignedCertInfo locality(String locality) {
		this.locality = locality;
		return this;
	}

	public String state() {
		return state;
	}

	public SelfSignedCertInfo state(String state) {
		this.state = state;
		return this;
	}

	public String country() {
		return country;
	}

	public SelfSignedCertInfo country(String country) {
		this.country = country;
		return this;
	}

	public String alias() {
		return alias;
	}

	public SelfSignedCertInfo alias(String alias) {
		this.alias = alias;
		return this;
	}

	public int keysize() {
		return keysize;
	}

	public SelfSignedCertInfo keysize(int keysize) {
		this.keysize = keysize;
		return this;
	}

	public long validity() {
		return validity;
	}

	public SelfSignedCertInfo validity(long validity) {
		this.validity = validity;
		return this;
	}

	public char[] password() {
		return password;
	}

	public SelfSignedCertInfo password(char[] password) {
		this.password = password;
		return this;
	}
}
