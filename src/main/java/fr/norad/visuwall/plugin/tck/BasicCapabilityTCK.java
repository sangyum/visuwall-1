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
package fr.norad.visuwall.plugin.tck;

public interface BasicCapabilityTCK {

    // listSoftwareProjectIds
    void should_find_all_projects_ids();

    // getDescription
    void should_find_description_of_a_project() throws Exception;

    // getMavenId
    void should_get_maven_id() throws Exception;

    // getMavenId
    void should_get_name_of_a_project() throws Exception;

    // identify
    void should_identify_a_project() throws Exception;

    // isProjectDisabled
    void should_get_a_disabled_project() throws Exception;
}
