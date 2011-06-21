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

package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.IntegrationTestData.JENKINS_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;

import org.junit.Test;

public class JenkinsPluginITest {

    @Test
    public void should_recognize_jenkins_instance_with_valid_url() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        SoftwareId softwareId = jenkinsPlugin.getSoftwareId(new URL(JENKINS_URL));

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.407", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

    @Test
    public void should_recognize_jenkins_instance_with_https() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        SoftwareId softwareId = jenkinsPlugin.getSoftwareId(new URL("https://builds.apache.org"));

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.413", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

    @Test(expected = IncompatibleSoftwareException.class)
    public void should_not_fail_if_url_is_not_manageable() throws Exception {
        JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
        String url = "http://www.google.fr";
        jenkinsPlugin.getSoftwareId(new URL(url));
    }

}