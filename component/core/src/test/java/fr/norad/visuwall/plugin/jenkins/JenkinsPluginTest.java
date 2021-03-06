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
package fr.norad.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;

public class JenkinsPluginTest {

    JenkinsPlugin jenkinsPlugin;
    private Map<String, String> properties;

    @Before
    public void init() {
        jenkinsPlugin = new JenkinsPlugin();
    }

    @Test(expected = NullPointerException.class)
    public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance()
            throws SoftwareNotFoundException {
        jenkinsPlugin.getSoftwareId(null, properties);
    }

    @Test
    public void should_create_valid_plugin() {
        assertEquals(JenkinsConnection.class, jenkinsPlugin.getConnectionClass());
        assertEquals("Jenkins plugin", jenkinsPlugin.getName());
        assertEquals(1.0f, jenkinsPlugin.getVersion(), 0);
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_throw_exception_when_software_url_cant_be_reached() throws Exception {
        URL url = new URL("http://notfound");
        jenkinsPlugin.getSoftwareId(url, properties);
    }
}
