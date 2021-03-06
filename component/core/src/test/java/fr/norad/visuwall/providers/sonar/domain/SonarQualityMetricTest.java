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
package fr.norad.visuwall.providers.sonar.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;

public class SonarQualityMetricTest {

    @Test
    public void should_build_hash_code() {
        SonarQualityMetric sonarQualityMetric2 = new SonarQualityMetric();
        sonarQualityMetric2.name = "name2";
        int hashCode1 = sonarQualityMetric2.hashCode();
        int hashCode2 = sonarQualityMetric2.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void should_test_equaliy_on_key_notsame_case() {
        SonarQualityMetric metric1 = new SonarQualityMetric();
        metric1.key = "key1";

        SonarQualityMetric metric2 = new SonarQualityMetric();
        metric2.key = "key2";

        assertNotSame(metric1, metric2);
    }

    @Test
    public void should_test_equaliy_on_key_same_case() {
        SonarQualityMetric metric1 = new SonarQualityMetric();
        metric1.key = "key1";

        SonarQualityMetric metric2 = new SonarQualityMetric();
        metric2.key = "key1";

        assertEquals(metric1, metric2);
    }
}
