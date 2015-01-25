/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.plugin.continuum;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.BuildResult;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;
import org.apache.maven.continuum.xmlrpc.scm.ChangeSet;
import org.apache.maven.continuum.xmlrpc.scm.ScmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.ConnectionException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.exception.ViewNotFoundException;
import fr.norad.visuwall.plugin.capability.BuildCapability;
import fr.norad.visuwall.plugin.capability.ViewCapability;

public class ContinuumConnection implements BuildCapability, ViewCapability {

    private static final Logger LOG = LoggerFactory.getLogger(ContinuumConnection.class);

    private ContinuumXmlRpcClient client;

    private boolean connected;

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        try {
            ProjectSummary projectSummary = client.getProjectSummary(getId(softwareProjectId));
            return projectSummary.getGroupId() + ":" + projectSummary.getArtifactId();
        } catch (Exception e) {
            throw new MavenIdNotFoundException("Cannot find Maven Id for " + softwareProjectId, e);
        }
    }

    private int getId(SoftwareProjectId softwareProjectId) {
        return Integer.parseInt(softwareProjectId.getProjectId());
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            client = new ContinuumXmlRpcClient(new URL(url + "/xmlrpc"));
            connected = true;
        } catch (MalformedURLException e) {
            throw new ConnectionException("Cannot open a connection to " + url, e);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        try {
            ProjectSummary project = findProject(softwareProjectId);
            return project.getDescription();
        } catch (Exception e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        try {
            ProjectSummary project = findProject(softwareProjectId);
            return project.getName();
        } catch (Exception e) {
            throw new ProjectNotFoundException(e);
        }
    }

    private ProjectSummary findProject(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        try {
            return client.getProjectSummary(getId(softwareProjectId));
        } catch (Exception e) {
            throw new ProjectNotFoundException("Cannot find " + softwareProjectId, e);
        }
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        try {
            String projectMavenId = projectKey.getMavenId();
            List<ProjectSummary> projects = findAllProjects();
            for (ProjectSummary project : projects) {
                String groupId = project.getGroupId();
                String artifactId = project.getArtifactId();
                String mavenId = groupId + ":" + artifactId;
                if (mavenId.equals(projectMavenId)) {
                    return new SoftwareProjectId(Integer.toString(project.getId()));
                }
            }
        } catch (Exception e) {
            throw new ProjectNotFoundException("Cannot identify " + projectKey, e);
        }
        throw new ProjectNotFoundException("Cannot identify " + projectKey);
    }

    private List<ProjectSummary> findAllProjects() throws Exception {
        List<ProjectGroupSummary> pgs = client.getAllProjectGroups();
        List<ProjectSummary> ps = new ArrayList<ProjectSummary>();
        for (ProjectGroupSummary projectGroupSummary : pgs) {
            ps.addAll(client.getProjects(projectGroupSummary.getId()));
        }
        return ps;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> projectIds = new HashMap<SoftwareProjectId, String>();
        List<ProjectSummary> projects;
        try {
            projects = findAllProjects();
            for (ProjectSummary project : projects) {
                SoftwareProjectId projectId = new SoftwareProjectId(Integer.toString(project.getId()));
                projectIds.put(projectId, project.getName());
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return projectIds;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        Set<Commiter> commiters = new TreeSet<Commiter>();
        try {
            BuildResult buildResult = getBuildResult(softwareProjectId, buildId);
            ScmResult scmResult = buildResult.getScmResult();
            List<ChangeSet> changes = scmResult.getChanges();
            for (ChangeSet change : changes) {
                String author = change.getAuthor();
                Commiter commiter = new Commiter(author);
                commiter.setName(author);
                commiters.add(commiter);
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return new ArrayList<Commiter>(commiters);
    }

    private BuildResult getBuildResult(SoftwareProjectId softwareProjectId, String buildId) throws Exception {
        BuildResult buildResult = client.getBuildResult(getId(softwareProjectId), Integer.parseInt(buildId));
        return buildResult;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        try {
            BuildResult buildResult;
            buildResult = getBuildResult(softwareProjectId, buildId);
            long startTime = buildResult.getStartTime();
            long endTime = buildResult.getEndTime();
            BuildTime buildTime = new BuildTime();
            buildTime.setStartTime(new Date(startTime));
            buildTime.setDuration(endTime - startTime);
            return buildTime;
        } catch (Exception e) {
            throw new BuildNotFoundException("Cannot get build time for " + softwareProjectId + " and build id "
                    + buildId, e);
        }
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        List<String> buildIds = new ArrayList<String>();
        ProjectSummary project = findProject(softwareProjectId);
        Integer latestBuildId = project.getLatestBuildId();
        buildIds.add(latestBuildId.toString());
        return buildIds;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        int id = getId(projectId);
        BuildResult buildResult;
        try {
            buildResult = client.getBuildResult(id, Integer.parseInt(buildId));
            return States.asVisuwallState(buildResult.getState());
        } catch (NumberFormatException e) {
            throw new BuildNotFoundException(e);
        } catch (Exception e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return new Date();
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        ProjectSummary project = findProject(softwareProjectId);
        return Integer.toString(project.getLatestBuildId());
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        try {
            List<ProjectGroupSummary> pgs = client.getAllProjectGroups();
            for (String viewName : views) {
                for (ProjectGroupSummary projectGroupSummary : pgs) {
                    if (projectGroupSummary.getName().equals(viewName)) {
                        List<ProjectSummary> projects = client.getProjects(projectGroupSummary.getId());
                        for (ProjectSummary project : projects) {
                            SoftwareProjectId projectId = new SoftwareProjectId(Integer.toString(project.getId()));
                            softwareProjectIds.add(projectId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Cannot get all projects groups", e);
        }
        return softwareProjectIds;
    }

    @Override
    public List<String> findViews() {
        List<String> views = new ArrayList<String>();
        try {
            List<ProjectGroupSummary> pgs = client.getAllProjectGroups();
            for (ProjectGroupSummary projectGroupSummary : pgs) {
                String name = projectGroupSummary.getName();
                views.add(name);
            }
            Collections.sort(views);
        } catch (Exception e) {
            LOG.warn("Cannot get all projects groups", e);
        }
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        List<String> projectNames = new ArrayList<String>();
        try {
            List<ProjectGroupSummary> pgs = client.getAllProjectGroups();
            for (ProjectGroupSummary projectGroupSummary : pgs) {
                if (projectGroupSummary.getName().equals(viewName)) {
                    List<ProjectSummary> projects = client.getProjects(projectGroupSummary.getId());
                    for (ProjectSummary project : projects) {
                        projectNames.add(project.getName());
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Cannot get all projects groups", e);
        }
        return projectNames;
    }

}
