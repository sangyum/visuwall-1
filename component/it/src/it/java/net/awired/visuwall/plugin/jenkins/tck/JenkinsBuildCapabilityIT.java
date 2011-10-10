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

package net.awired.visuwall.plugin.jenkins.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import org.junit.Before;
import org.junit.Test;

public class JenkinsBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability jenkins = new JenkinsConnection();

    @Before
    public void init() throws ConnectionException {
        jenkins.connect(Urls.AWIRED_JENKINS, null, null);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = struts();
        String number = jenkins.getLastBuildId(projectId);
        assertEquals("4", number);
    }

    @Override
    @Test
    public void should_get_build_state() throws Exception {
        assertEquals(State.SUCCESS, getLastBuildState("struts"));
        assertEquals(State.FAILURE, getLastBuildState("errorproject"));
    }

    private State getLastBuildState(String jobName) throws ProjectNotFoundException, BuildIdNotFoundException,
            BuildNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId(jobName);
        String buildNumber = jenkins.getLastBuildId(projectId);
        State state = jenkins.getBuildState(projectId, buildNumber);
        return state;
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        String buildNumber = jenkins.getLastBuildId(softwareProjectId);
        boolean isBuilding = jenkins.isBuilding(softwareProjectId, buildNumber);
        assertFalse(isBuilding);
    }

    @Override
    @Test
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        String buildNumber = jenkins.getLastBuildId(softwareProjectId);
        Date date = jenkins.getEstimatedFinishTime(softwareProjectId, buildNumber);
        assertNotNull(date);
    }

    @Override
    @Test
    public void should_get_build_numbers() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        List<String> buildNumbers = jenkins.getBuildIds(softwareProjectId);
        assertEquals("1", buildNumbers.get(0));
        assertEquals("2", buildNumbers.get(1));
        assertEquals("3", buildNumbers.get(2));
        assertEquals("4", buildNumbers.get(3));
    }

    @Override
    @Test
    public void should_get_build_time() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        String buildNumber = jenkins.getLastBuildId(softwareProjectId);
        BuildTime buildTime = jenkins.getBuildTime(softwareProjectId, buildNumber);
        assertNotNull(buildTime);
    }

    @Override
    @Test
    public void should_get_commiters() throws Exception {
        List<String> commiterNames = Arrays.asList("mcucchiara", "lukaszlenart", "jogep", "rgielen");

        SoftwareProjectId softwareProjectId = struts2();
        List<Commiter> commiters = jenkins.getBuildCommiters(softwareProjectId, "4");

        assertEquals(commiterNames.size(), commiters.size());

        for (Commiter commiter : commiters) {
            String commiterName = commiter.getName();
            assertTrue(commiterNames.contains(commiterName));
        }
    }

    private SoftwareProjectId struts2() {
        return new SoftwareProjectId("struts 2 instable");
    }

    private SoftwareProjectId struts() {
        return new SoftwareProjectId("struts");
    }
}
