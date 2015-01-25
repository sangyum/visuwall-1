/*
 * $Id: StrutsPreparerFactory.java 603355 2007-12-11 20:48:27Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.struts.tiles2.preparer;

import org.apache.struts.action.Action;
import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.BasicPreparerFactory;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.util.ClassUtil;

/**
 * Factory used to instantiate preparers in a Struts 1 / Tiles 2 environment.
 *
 * @version $Rev: 603355 $ $Date: 2007-12-11 21:48:27 +0100 (Mar, 11 déc 2007) $
 */
public class StrutsPreparerFactory extends BasicPreparerFactory {

    /** {@inheritDoc} */
    protected ViewPreparer createPreparer(String name) throws TilesException {
        ViewPreparer retValue;

        if (name.startsWith("/")) {
            retValue = new UrlPreparer(name);
        } else {
            Object instance = ClassUtil.instantiate(name, true);
            if (instance instanceof Action) {
                retValue = new ActionPreparer((Action) instance);
            } else {
                retValue = super.createPreparer(name);
            }
        }

        return retValue;
    }

}
