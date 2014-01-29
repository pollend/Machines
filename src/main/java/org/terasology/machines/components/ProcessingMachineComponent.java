/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.machines.components;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.Owns;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.world.block.ForceBlockActive;

/*
    Currently not intended for manual addition to an entity.
    Automatically created and added to the machine when a MachineDefinitionComponent is added.
    More thoughts on this is required.
 */
@ForceBlockActive
public class ProcessingMachineComponent implements Component {
    @Owns
    public EntityRef inputEntity;
    // mirrors the outputEntity and allows it to be persisted in case it is not the block itself
    @Owns
    public EntityRef ownedOutputEntity;
    public EntityRef outputEntity;
    public boolean automaticProcessing;

}