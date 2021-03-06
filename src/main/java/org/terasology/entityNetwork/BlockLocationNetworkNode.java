/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.entityNetwork;

import org.terasology.math.geom.Vector3i;

public class BlockLocationNetworkNode extends NetworkNode {
    public final Vector3i location;
    int maximumGridDistance = 1;

    public BlockLocationNetworkNode(String networkId, boolean isLeaf, Vector3i location) {
        super(networkId, isLeaf);
        this.location = location;
    }

    public BlockLocationNetworkNode(String networkId, boolean isLeaf, int maximumGridDistance, Vector3i location) {
        this(networkId, isLeaf, location);
        this.maximumGridDistance = maximumGridDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockLocationNetworkNode)) return false;
        if (!super.equals(o)) return false;

        BlockLocationNetworkNode that = (BlockLocationNetworkNode) o;

        if (location != null ? !location.equals(that.location) : that.location != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return location.toString();
    }

    @Override
    public boolean isConnectedTo(NetworkNode networkNode) {
        if (networkNode == null || !(networkNode instanceof BlockLocationNetworkNode)) return false;
        BlockLocationNetworkNode locationNetworkNode = (BlockLocationNetworkNode) networkNode;
        return locationNetworkNode.location.gridDistance(location) <= maximumGridDistance;
    }
}
