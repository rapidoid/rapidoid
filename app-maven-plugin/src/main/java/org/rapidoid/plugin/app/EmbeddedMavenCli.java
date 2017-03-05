package org.rapidoid.plugin.app;

/*
 * #%L
 * Rapidoid App Plugin
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

import org.apache.maven.Maven;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.*;
import org.codehaus.plexus.PlexusContainer;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class EmbeddedMavenCli extends MavenCli {

	private final MavenSession session;

	public EmbeddedMavenCli(MavenSession session) {
		this.session = session;
	}

	public void execute(List<String> goals, String baseDirectory, boolean updateSnapshots, Map<String, String> properties) {
		try {
			executeReq(goals, baseDirectory, updateSnapshots, properties);
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	private void executeReq(List<String> goals, String baseDirectory, boolean updateSnapshots, Map<String, String> properties) throws Exception {

		PlexusContainer container = session.getContainer();
		Maven maven = container.lookup(Maven.class);
		MavenExecutionRequestPopulator executionRequestPopulator = container.lookup(MavenExecutionRequestPopulator.class);

		MavenExecutionRequest request = new DefaultMavenExecutionRequest();
		request = executionRequestPopulator.populateDefaults(request);

		request.setPom(session.getRequest().getPom());
		request.setGoals(goals);
		request.setUpdateSnapshots(updateSnapshots);
		request.setBaseDirectory(new File(baseDirectory));

		Properties props = new Properties();
		props.putAll(properties);
		request.setUserProperties(props);

		String localRepo = System.getProperty("maven.repo.local");
		if (localRepo != null) {
			request.setLocalRepositoryPath(localRepo);
			ArtifactRepositoryLayout layout = new DefaultRepositoryLayout();
			ArtifactRepository repo = new DefaultArtifactRepository("repo", "file://" + localRepo, layout);

			request.setLocalRepository(repo);
			props.put("maven.repo.local", localRepo);
		}

		MavenExecutionResult result = maven.execute(request);

		if (result.hasExceptions()) {
			for (Throwable e : result.getExceptions()) {
				throw U.rte(e); // TODO is throwing the first exception enough?
			}
		}
	}

}
