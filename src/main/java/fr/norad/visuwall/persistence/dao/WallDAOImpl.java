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
package fr.norad.visuwall.persistence.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.exception.NotFoundException;
import fr.norad.visuwall.persistence.entity.SoftwareAccess;
import fr.norad.visuwall.persistence.entity.Wall;

@Repository
@Transactional
public class WallDAOImpl implements WallDAO {

    private static final Logger LOG = LoggerFactory.getLogger(WallDAOImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Wall update(Wall wall) {
        Wall persistWall = entityManager.merge(wall);
        return persistWall;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Wall> getWalls() {
        Query query = entityManager.createNamedQuery(Wall.QUERY_WALLS);
        List<Wall> resultList = query.getResultList();

        for (Wall wall : resultList) {
            // TODO replace with lazy load with extended entityManager or eager request 
            for (SoftwareAccess softwareInfo : wall.getSoftwareAccesses()) {
                List<SoftwareProjectId> projectNames = softwareInfo.getProjectIds();
                for (SoftwareProjectId softwareProjectId : projectNames) {

                }
                List<String> projectViews = softwareInfo.getViewNames();
                for (String string : projectViews) {

                }
                
                Map<String, String> properties = softwareInfo.getProperties();
                for (String string : properties.keySet()) {

                }                
            }
        }
        return resultList;
    }

    @Override
    public Wall find(String wallName) throws NotFoundException {
        Query query = entityManager.createNamedQuery(Wall.QUERY_WALLBYNAME);
        query.setParameter(Wall.QUERY_PARAM_NAME, wallName);
        Wall wall = (Wall) query.getSingleResult();

        // TODO replace with lazy load with extended entityManager or eager request 
        for (SoftwareAccess softwareInfo : wall.getSoftwareAccesses()) {
            softwareInfo.getProjectIds();
            softwareInfo.getViewNames();
        }
        return wall;
    }

    @Override
    public void deleteWall(String wallName) {
        Wall wall;
        try {
            wall = find(wallName);
            entityManager.remove(wall);
        } catch (NotFoundException e) {
            LOG.warn("No wall found in the DB to delete : " + wallName);
        }
    }

}
