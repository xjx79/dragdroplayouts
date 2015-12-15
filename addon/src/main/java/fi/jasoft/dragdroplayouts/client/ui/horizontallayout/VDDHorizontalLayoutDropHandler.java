/*
 * Copyright 2015 John Ahlroos
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package fi.jasoft.dragdroplayouts.client.ui.horizontallayout;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.dd.VAcceptCallback;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.orderedlayout.Slot;

import fi.jasoft.dragdroplayouts.client.ui.VDDAbstractDropHandler;

public class VDDHorizontalLayoutDropHandler
        extends VDDAbstractDropHandler<VDDHorizontalLayout> {

    public VDDHorizontalLayoutDropHandler(ComponentConnector connector) {
        super(connector);
    }

    @Override
    protected void dragAccepted(VDragEvent drag) {
        dragOver(drag);
    }

    @Override
    public boolean drop(VDragEvent drag) {

        // Un-emphasis any selections
        getLayout().emphasis(null, null);

        // Update the details
        Widget slot = getSlot(drag.getElementOver(), drag.getCurrentGwtEvent());
        getLayout().updateDragDetails(slot, drag);

        return getLayout().postDropHook(drag) && super.drop(drag);
    }

    private Slot getSlot(Element e, NativeEvent event) {
        Slot slot = null;
        if (getLayout().getElement() == e) {
            // Most likely between components, use the closes one in that case
            slot = findSlotHorizontally(12, event);
        } else {
            slot = WidgetUtil.findWidget(e, Slot.class);
        }
        return slot;
    }

    private Slot findSlotAtPosition(int clientX, int clientY,
            NativeEvent event) {
        com.google.gwt.dom.client.Element elementUnderMouse = WidgetUtil
                .getElementFromPoint(clientX, clientY);
        if (getLayout().getElement() != elementUnderMouse) {
            return getSlot(DOM.asOld(elementUnderMouse), event);
        }
        return null;
    }

    private Slot findSlotHorizontally(int spacerSize, NativeEvent event) {
        int counter = 0;
        Slot slotLeft, slotRight;
        int clientX = event.getClientX();
        int clientY = event.getClientY();
        while (counter < spacerSize) {
            counter++;
            slotRight = findSlotAtPosition(clientX + counter, clientY, event);
            slotLeft = findSlotAtPosition(clientX - counter, clientY, event);
            if (slotRight != null) {
                return slotRight;
            }
            if (slotLeft != null) {
                return slotLeft;
            }
        }
        return null;
    }

    @Override
    public void dragOver(VDragEvent drag) {

        // Remove any emphasis
        getLayout().emphasis(null, null);

        Slot slot = getSlot(drag.getElementOver(), drag.getCurrentGwtEvent());

        if (slot != null) {
            getLayout().updateDragDetails(slot, drag);
        } else {
            getLayout().updateDragDetails(getLayout(), drag);
        }

        getLayout().postOverHook(drag);

        // Validate the drop
        validate(new VAcceptCallback() {
            public void accepted(VDragEvent event) {
                Slot slot = getSlot(event.getElementOver(),
                        event.getCurrentGwtEvent());
                if (slot != null) {
                    getLayout().emphasis(slot, event);
                } else {
                    getLayout().emphasis(getLayout(), event);
                }
            }
        }, drag);
    }

    @Override
    public void dragEnter(VDragEvent drag) {
        super.dragEnter(drag);
        Slot slot = getSlot(drag.getElementOver(), drag.getCurrentGwtEvent());
        if (slot != null) {
            getLayout().updateDragDetails(slot, drag);
        } else {
            getLayout().updateDragDetails(getLayout(), drag);
        }

        getLayout().postEnterHook(drag);
    }

    @Override
    public void dragLeave(VDragEvent drag) {
        getLayout().deEmphasis();
        getLayout().postLeaveHook(drag);
    }

}
