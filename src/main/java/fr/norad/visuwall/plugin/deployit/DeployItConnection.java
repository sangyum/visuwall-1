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
package fr.norad.visuwall.plugin.deployit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.domain.TestResult;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.ConnectionException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.exception.ViewNotFoundException;
import fr.norad.visuwall.plugin.capability.BuildCapability;
import fr.norad.visuwall.plugin.capability.TestCapability;
import fr.norad.visuwall.plugin.capability.ViewCapability;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.providers.deployit.DeployIt;
import fr.norad.visuwall.providers.deployit.resource.ArchivedTasks;
import fr.norad.visuwall.providers.deployit.resource.Step;
import fr.norad.visuwall.providers.deployit.resource.Task;

public class DeployItConnection implements BuildCapability, TestCapability, ViewCapability {

    private DeployIt deployIt;
    private boolean connected;

    private final static Logger LOG = LoggerFactory.getLogger(DeployItConnection.class);

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        throw new MavenIdNotFoundException("Not implemented");
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        deployIt = new DeployIt(url, login, password);
        connected = true;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        try {
            Task task = getTask(projectId);
            String environment = task.getEnvironment();
            String application = task.getApplication();
            Integer version = task.getVersion();
            return environment + "<br/>" + application + " v" + version;
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find name for " + projectId, e);
        }
    }

    private Task getTask(SoftwareProjectId projectId) throws ResourceNotFoundException {
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            if (projectId.getProjectId().equals(environment + " - " + application)) {
                return task;
            }
        }
        return null;
    }

    private Task getTask(SoftwareProjectId projectId, String expectedVersion) throws ResourceNotFoundException {
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            Integer version = task.getVersion();
            if (expectedVersion.equals(version.toString())
                    && projectId.getProjectId().equals(environment + " - " + application)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("Not implemented");
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
        try {
            List<Task> tasks = findAllTasks();
            for (Task task : tasks) {
                String environment = task.getEnvironment();
                String application = task.getApplication();
                String projectId = environment + " - " + application;
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(projectId);
                softwareProjectIds.put(softwareProjectId, projectId);
            }
        } catch (ResourceNotFoundException e) {
            LOG.error("Cannot get archived tasks", e);
        }
        return softwareProjectIds;
    }

    private List<Task> findAllTasks() throws ResourceNotFoundException {
        ArchivedTasks archivedTasks = deployIt.getArchivedTasks();
        List<Task> tasks = archivedTasks.getTasks();
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t2.getCompletionDate().compareTo(t1.getCompletionDate());
            }
        });
        return tasks;
    }

    private List<Task> getTasks(SoftwareProjectId softwareProjectId) throws ResourceNotFoundException {
        List<Task> filteredTasks = new ArrayList<Task>();
        List<Task> tasks = findAllTasks();
        for (Task task : tasks) {
            String environment = task.getEnvironment();
            String application = task.getApplication();
            if (softwareProjectId.getProjectId().equals(environment + " - " + application)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = new ArrayList<Commiter>();
        try {
            Task task = getTask(softwareProjectId, buildId);
            if (task == null) {
                throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                        + buildId);
            }
            Commiter commiter = new Commiter(task.getUser());
            commiter.setName(task.getUser());
            commiters.add(commiter);
        } catch (ResourceNotFoundException e) {
            throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                    + buildId, e);
        }
        return commiters;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        try {
            Task task = getTask(softwareProjectId, buildId);
            if (task == null) {
                throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                        + buildId);
            }
            BuildTime buildTime = new BuildTime();
            buildTime.setStartTime(task.getStartDate());
            long duration = task.getCompletionDate().getTime() - task.getStartDate().getTime();
            if (duration < 1000) {
                duration = 1000;
            }
            buildTime.setDuration(duration);
            return buildTime;
        } catch (ResourceNotFoundException e) {
            throw new BuildNotFoundException("Cannot find deployment for " + softwareProjectId + " and version "
                    + buildId);
        }
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        List<String> buildIds = new ArrayList<String>();
        try {
            List<Task> tasks = getTasks(softwareProjectId);
            for (Task task : tasks) {
                String version = task.getVersion().toString();
                if (!buildIds.contains(version)) {
                    buildIds.add(0, version);
                }
            }
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find deployment for " + softwareProjectId);
        }
        return buildIds;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return BuildState.SUCCESS;
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return new Date();
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        Task task;
        try {
            task = getTask(softwareProjectId);
            if (task == null) {
                throw new ProjectNotFoundException("Cannot find deployment for " + softwareProjectId);
            }
            Integer version = task.getVersion();
            return version.toString();
        } catch (ResourceNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find deployment for " + softwareProjectId);
        }
    }

    @Override
    public String toString() {
        return "DeployIt Connection";
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = new TestResult();
        try {
            Task task = getTask(projectId);
            List<Step> steps = task.getSteps();
            for (Step step : steps) {
                switch (step.getState()) {
                case SKIPPED:
                    testResult.setSkipCount(testResult.getSkipCount() + 1);
                    break;
                case DONE:
                    if (step.getFailureCount() == 0) {
                        testResult.setPassCount(testResult.getPassCount() + 1);
                    } else {
                        testResult.setFailCount(testResult.getFailCount() + 1);
                    }
                    break;
                }
            }
        } catch (ResourceNotFoundException e) {
            LOG.warn("Cannot retrieve steps for " + projectId, e);
        }
        return testResult;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId projectId) {
        return new TestResult();
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        for (String environmentName : views) {
            try {
                List<String> deployedApplications = deployIt.getDeployedApplicationsByEnvironment(environmentName);
                for (String deployedApplication : deployedApplications) {
                    SoftwareProjectId softwareProjectId = createSoftwareProjectId(deployedApplication, environmentName);
                    softwareProjectIds.add(softwareProjectId);
                }
            } catch (ResourceNotFoundException e) {
                LOG.warn("Cannot retrieve deployed applications on environment " + environmentName, e);
            }
        }
        return softwareProjectIds;
    }

    private SoftwareProjectId createSoftwareProjectId(String deployedApplication, String environment) {
        String projectId = deployedApplication.replaceFirst(environment + "/", environment + " - ");
        SoftwareProjectId softwareProjectId = new SoftwareProjectId(projectId);
        return softwareProjectId;
    }

    @Override
    public List<String> findViews() {
        List<String> views = new ArrayList<String>();
        try {
            views = deployIt.getEnvironmentNames();
        } catch (ResourceNotFoundException e) {
            LOG.warn("Cannot retrieve views", e);
        }
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        try {
            return deployIt.getDeployedApplicationsByEnvironment(viewName);
        } catch (ResourceNotFoundException e) {
            throw new ViewNotFoundException("Cannot retrieve project names for view " + viewName, e);
        }
    }

}
