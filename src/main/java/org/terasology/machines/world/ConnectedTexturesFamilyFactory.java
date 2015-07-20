/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.machines.world;


import com.google.common.collect.Iterables;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.machines.components.ConnectedTextureComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.family.ConnectionCondition;
import org.terasology.world.block.family.RegisterBlockFamilyFactory;
import org.terasology.world.block.family.UpdatesWithNeighboursFamilyFactory;

@RegisterBlockFamilyFactory("Machines:connectedTexture")
public class ConnectedTexturesFamilyFactory extends UpdatesWithNeighboursFamilyFactory {
    public ConnectedTexturesFamilyFactory() {
        super(new ConnectedTexturesConnectionCondition(), (byte) 63);
    }

    private static class ConnectedTexturesConnectionCondition implements ConnectionCondition {
        @Override
        public boolean isConnectingTo(Vector3i blockLocation, Side connectSide, WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry) {
            EntityRef thisEntity = blockEntityRegistry.getBlockEntityAt(blockLocation);
            ConnectedTextureComponent thisCTC = thisEntity.getComponent(ConnectedTextureComponent.class);

            Vector3i thatLocation = new Vector3i(blockLocation);
            thatLocation.add(connectSide.getVector3i());
            EntityRef thatEntity = blockEntityRegistry.getExistingEntityAt(thatLocation);
            ConnectedTextureComponent thatCTC = thatEntity.getComponent(ConnectedTextureComponent.class);

            if (thisCTC == null || thatCTC == null) {
                return false;
            }

            if (thisCTC != null && thisCTC.connectsWith.size() == 0 && thatCTC != null && thatCTC.connectsWith.size() == 0) {
                // this connects to everything
                return true;
            }

            return Iterables.any(thisCTC.connectsWith, x -> thatCTC.connectsWith.contains(x));
        }
    }
}
