/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.machines.processParts;

import com.google.common.collect.Lists;
import org.terasology.assets.ResourceUrn;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.events.BeforeItemPutInInventory;
import org.terasology.machines.ExtendedInventoryManager;
import org.terasology.machines.components.ProcessRequirementsProviderComponent;
import org.terasology.machines.components.ProcessRequirementsProviderFromWorkstationComponent;
import org.terasology.machines.events.RequirementUsedEvent;
import org.terasology.machines.ui.OverlapLayout;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.utilities.Assets;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.process.WorkstationInventoryUtils;
import org.terasology.workstation.processPart.ProcessEntityIsInvalidToStartEvent;
import org.terasology.workstation.processPart.ProcessEntityStartExecutionEvent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;
import org.terasology.workstation.processPart.metadata.ProcessEntityGetInputDescriptionEvent;
import org.terasology.workstation.ui.InventoryItem;

import java.util.List;

@RegisterSystem
public class RequirementInputProcessPartCommonSystem extends BaseComponentSystem {

    public static final String REQUIREMENTSINVENTORYCATEGORY = "REQUIREMENTS";
    public static final int TIMEBETWEENWIDGETSWITCH = 1500;

    @In
    InventoryManager inventoryManager;

    ///// Processing

    @ReceiveEvent
    public void validateToStartExecution(ProcessEntityIsInvalidToStartEvent event, EntityRef processEntity,
                                         RequirementInputComponent requirementInputComponent) {
        List<String> requirementsProvided = Lists.newArrayList();

        // get the requirements provided by the machine
        ProcessRequirementsProviderFromWorkstationComponent workstationProcessRequirements
                = event.getWorkstation().getComponent(ProcessRequirementsProviderFromWorkstationComponent.class);
        if (workstationProcessRequirements != null) {
            requirementsProvided.addAll(workstationProcessRequirements.requirements);
        }

        // get the requirements provided by items (tools)
        for (EntityRef item : ExtendedInventoryManager.iterateItems(inventoryManager, event.getWorkstation(), false, REQUIREMENTSINVENTORYCATEGORY)) {
            ProcessRequirementsProviderComponent processRequirementsProviderComponent = item.getComponent(ProcessRequirementsProviderComponent.class);
            if (processRequirementsProviderComponent != null) {
                requirementsProvided.addAll(processRequirementsProviderComponent.requirements);
            }
        }

        if (!requirementsProvided.containsAll(requirementInputComponent.requirements)) {
            event.consume();
        }
    }

    @ReceiveEvent
    public void startExecution(ProcessEntityStartExecutionEvent event, EntityRef processEntity,
                               RequirementInputComponent requirementInputComponent) {

        List<String> requirementsRequired = Lists.newArrayList(requirementInputComponent.requirements);

        // get the requirements provided by the machine
        ProcessRequirementsProviderFromWorkstationComponent workstationProcessRequirements
                = event.getWorkstation().getComponent(ProcessRequirementsProviderFromWorkstationComponent.class);
        if (workstationProcessRequirements != null) {
            if (requirementsRequired.removeAll(workstationProcessRequirements.requirements)) {
                event.getWorkstation().send(new RequirementUsedEvent(processEntity));
            }
        }

        // get the requirements provided by items (tools)
        for (EntityRef item : ExtendedInventoryManager.iterateItems(inventoryManager, event.getWorkstation(), false, REQUIREMENTSINVENTORYCATEGORY)) {
            ProcessRequirementsProviderComponent processRequirementsProviderComponent = item.getComponent(ProcessRequirementsProviderComponent.class);
            if (processRequirementsProviderComponent != null) {
                if (requirementsRequired.removeAll(processRequirementsProviderComponent.requirements)) {
                    item.send(new RequirementUsedEvent(processEntity));
                }
            }
        }
    }

    ///// Inventory

    @ReceiveEvent
    public void isValidInventoryItem(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                     RequirementInputComponent requirementInputComponent) {
        if (WorkstationInventoryUtils.getAssignedInputSlots(event.getWorkstation(), REQUIREMENTSINVENTORYCATEGORY).contains(event.getSlotNo())) {
            ProcessRequirementsProviderComponent requirementsProvider = event.getItem().getComponent(ProcessRequirementsProviderComponent.class);
            if (requirementsProvider == null
                    || !requirementsProvider.requirements.containsAll(requirementInputComponent.requirements)) {
                event.consume();
            }
        }
    }

    @ReceiveEvent
    public void preventNonRequirementProvidingItemsIntoRequirementSlot(BeforeItemPutInInventory event, EntityRef entity,
                                                                       InventoryComponent workstationInventory) {
        if (WorkstationInventoryUtils.getAssignedInputSlots(entity, REQUIREMENTSINVENTORYCATEGORY).contains(event.getSlot())) {
            ProcessRequirementsProviderComponent requirementsProvider = event.getItem().getComponent(ProcessRequirementsProviderComponent.class);
            if (requirementsProvider == null) {
                event.consume();
            }
        }
    }

    ///// Metadata

    @ReceiveEvent
    public void getInputDescriptions(ProcessEntityGetInputDescriptionEvent event, EntityRef processEntity,
                                     RequirementInputComponent requirementInputComponent) {
        List<ProcessPartDescription> descriptions = Lists.newLinkedList();
        for (String requirement : requirementInputComponent.requirements) {
            UIWidget widget = createWidgetForRequirement(requirement, CoreRegistry.get(EntityManager.class), CoreRegistry.get(Time.class));
            event.addInputDescription(new ProcessPartDescription(null, requirement, widget));
        }
    }

    private static UIWidget createWidgetForRequirement(String requirement, EntityManager entityManager, Time time) {
        List<Prefab> relatedPrefabs = Lists.newArrayList();
        for (ResourceUrn resourceUrn : Assets.list(Prefab.class)) {
            Prefab prefab = Assets.get(resourceUrn, Prefab.class).get();
            ProcessRequirementsProviderComponent requirementsProviderComponent = prefab.getComponent(ProcessRequirementsProviderComponent.class);
            if (prefab.hasComponent(ItemComponent.class) && requirementsProviderComponent != null && requirementsProviderComponent.requirements.contains(requirement)) {
                relatedPrefabs.add(prefab);
            }
        }

        OverlapLayout layout = new OverlapLayout();
        for (int i = 0; i < relatedPrefabs.size(); i++) {
            Prefab prefab = relatedPrefabs.get(i);
            final Integer visibleAtIndex = i;

            // create entity to display
            EntityBuilder entityBuilder = entityManager.newBuilder(prefab);
            entityBuilder.setPersistent(false);
            EntityRef entityRef = entityBuilder.build();

            // create the widget
            InventoryItem inventoryItem = new InventoryItem(entityRef);
            inventoryItem.bindVisible(new ReadOnlyBinding<Boolean>() {
                @Override
                public Boolean get() {
                    return Math.floor(time.getRealTimeInMs() / TIMEBETWEENWIDGETSWITCH) % relatedPrefabs.size() == visibleAtIndex;
                }
            });

            // clean up and add the widget
            entityRef.destroy();
            layout.addWidget(inventoryItem);
        }

        return layout;

    }
}
