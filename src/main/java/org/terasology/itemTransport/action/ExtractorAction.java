/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.itemTransport.action;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.itempipes.controllers.PipeSystem;
import org.terasology.logic.delay.DelayManager;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ExtractorAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    PipeSystem pipeSystem;
    @In
    EntityManager entityManager;
    @In
    private DelayManager delayManager;
    
    public void onExtract(){
    
    }
    
    @Override
    public void update(float delta) {
    
    }
}
