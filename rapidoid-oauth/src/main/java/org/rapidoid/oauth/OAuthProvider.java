package org.rapidoid.oauth;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-oauth
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuthProvider extends RapidoidThing {

	public static OAuthProvider GOOGLE = new OAuthProvider("Google", "https://accounts.google.com/o/oauth2/auth",
		"https://accounts.google.com/o/oauth2/token", "https://www.googleapis.com/oauth2/v1/userinfo", "profile",
		"email");

	public static OAuthProvider FACEBOOK = new OAuthProvider("Facebook", "https://graph.facebook.com/oauth/authorize",
		"https://graph.facebook.com/oauth/access_token", "https://graph.facebook.com/me", "public_profile", "email");

	public static OAuthProvider LINKEDIN = new OAuthProvider(
		"LinkedIn",
		"https://www.linkedin.com/uas/oauth2/authorization",
		"https://www.linkedin.com/uas/oauth2/accessToken",
		"https://api.linkedin.com/v1/people/~:(id,first-name,last-name,maiden-name,email-address)?format=json&oauth2_access_token={{token}}",
		"r_basicprofile", "r_emailaddress");

	public static OAuthProvider GITHUB = new OAuthProvider("GitHub", "https://github.com/login/oauth/authorize",
		"https://github.com/login/oauth/access_token", "https://api.github.com/user", "" /* no scope */,
		"user:email");

	public static final OAuthProvider[] PROVIDERS = {GOOGLE, FACEBOOK, LINKEDIN, GITHUB};

	private final String name;

	private final String authEndpoint;

	private final String tokenEndpoint;

	private final String profileEndpoint;

	private final String profileScope;

	private final String emailScope;

	public OAuthProvider(String name, String authEndpoint, String tokenEndpoint, String profileEndpoint,
	                     String profileScope, String emailScope) {
		this.name = name;
		this.authEndpoint = authEndpoint;
		this.tokenEndpoint = tokenEndpoint;
		this.profileEndpoint = profileEndpoint;
		this.profileScope = profileScope;
		this.emailScope = emailScope;
	}

	public String getName() {
		return name;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public String getProfileEndpoint() {
		return profileEndpoint;
	}

	public String getProfileScope() {
		return profileScope;
	}

	public String getEmailScope() {
		return emailScope;
	}

}
