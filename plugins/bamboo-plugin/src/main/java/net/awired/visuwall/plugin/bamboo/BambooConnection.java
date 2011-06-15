/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooBuildNumberNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooStateNotFoundException;
import net.awired.visuwall.plugin.bamboo.builder.BuildBuilder;

import com.google.common.base.Preconditions;

public class BambooConnection implements Connection, BuildCapability {

	private static final String BAMBOO_ID = "BAMBOO_ID";

	private Bamboo bamboo;

	private BuildBuilder buildBuilder = new BuildBuilder();

	public BambooConnection(String url, String login, String password) {
		this(url);
	}

	public BambooConnection(String url) {
		Preconditions.checkNotNull(url, "Use setUrl() before calling init method");
		bamboo = new Bamboo(url);
	}

	@Override
	public List<ProjectId> findAllProjects() {
		List<ProjectId> projects = new ArrayList<ProjectId>();
		for (BambooProject bambooProject : bamboo.findAllProjects()) {
			ProjectId projectId = new ProjectId();
			projectId.setName(bambooProject.getName());
			projectId.addId(BAMBOO_ID, bambooProject.getKey());
			projects.add(projectId);
		}
		return projects;
	}

	@Override
	public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		try {
			String projectKey = getProjectKey(projectId);
			BambooProject bambooProject = bamboo.findProject(projectKey);
			Project project = new Project(projectId);
			project.setName(bambooProject.getName());
			return project;
		} catch (BambooProjectNotFoundException e) {
			throw new ProjectNotFoundException("Can't find project with ProjectId:" + projectId, e);
		}
	}

	@Override
	public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
	        ProjectNotFoundException {
		checkProjectId(projectId);

		String projectName = getProjectKey(projectId);
		try {
			BambooBuild bambooBuild = bamboo.findBuild(projectName, buildNumber);
			return buildBuilder.createFrom(bambooBuild);
		} catch (BambooBuildNotFoundException e) {
			throw new BuildNotFoundException(e);
		}
	}

	@Override
	public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
		String projectName = getProjectKey(projectId);
		try {
			return bamboo.getEstimatedFinishTime(projectName);
		} catch (BambooProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		try {
			String projectName = getProjectKey(projectId);
			BambooProject bambooProject = bamboo.findProject(projectName);
			return bambooProject.isBuilding();
		} catch (BambooProjectNotFoundException e) {
			throw new ProjectNotFoundException("Can't find project with ProjectId:" + projectId, e);
		}
	}

	@Override
	public State getState(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		try {
			String projectName = getProjectKey(projectId);
			String bambooState = bamboo.getState(projectName);
			return States.asVisuwallState(bambooState);
		} catch (BambooStateNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
		checkProjectId(projectId);
		String id = getProjectKey(projectId);
		Preconditions.checkNotNull(id, BAMBOO_ID);
		try {
			return bamboo.getLastBuildNumber(id);
		} catch (BambooBuildNumberNotFoundException e) {
			throw new BuildNotFoundException(e);
		}
	}

	@Override
	public List<String> findProjectNames() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public boolean contains(ProjectId projectId) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public List<ProjectId> findProjectsByNames(List<String> names) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void close() {
	}

	private String getProjectKey(ProjectId projectId) {
		return projectId.getId(BAMBOO_ID);
	}

	private void checkProjectId(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
	}

}