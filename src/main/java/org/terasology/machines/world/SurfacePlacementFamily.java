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
package org.terasology.machines.world;

import com.google.common.collect.Maps;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.AbstractBlockFamily;
import org.terasology.world.block.family.SideDefinedBlockFamily;

import java.util.Locale;
import java.util.Map;

public class SurfacePlacementFamily extends AbstractBlockFamily implements SideDefinedBlockFamily {
    private Map<Side, Block> blocks = Maps.newEnumMap(Side.class);
    private Block archetype;

    public SurfacePlacementFamily(BlockUri uri, Block archetypeBlock, Map<Side, Block> blocks, Iterable<String> categories) {
        super(uri, categories);
        for (Side side : Side.values()) {
            Block block = blocks.get(side);
            if (block != null) {
                this.blocks.put(side, block);
                block.setBlockFamily(this);
                block.setUri(new BlockUri(uri, new Name(side.name())));
            }
        }

        if (!blocks.values().contains(archetypeBlock)) {
            archetypeBlock.setBlockFamily(this);
            archetypeBlock.setUri(new BlockUri(uri, new Name("archetype")));
        }
        archetype = archetypeBlock;
    }

    @Override
    public Block getBlockForPlacement(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, Vector3i location, Side attachmentSide, Side direction) {
        return blocks.get(attachmentSide);
    }

    @Override
    public Block getArchetypeBlock() {
        return archetype;
    }

    @Override
    public Block getBlockFor(BlockUri blockUri) {
        if (getURI().equals(blockUri.getFamilyUri())) {
            try {
                Side side = Side.valueOf(blockUri.getIdentifier().toString().toUpperCase(Locale.ENGLISH));
                return blocks.get(side);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.values();
    }

    @Override
    public Block getBlockForSide(Side side) {
        return blocks.get(side);
    }

    @Override
    public Side getSide(Block block) {
        for (Map.Entry<Side, Block> sideBlockEntry : blocks.entrySet()) {
            if (sideBlockEntry.getValue() == block) {
                return sideBlockEntry.getKey();
            }
        }

        return null;
    }
}
