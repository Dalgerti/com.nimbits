/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SummaryPanel extends NavigationEventProvider {


    private static final int WIDTH = 350;
    private static final double MAX_VALUE = 31556926d;
    private static final int SECONDS_IN_HOUR = 3600;
    private FormData formdata;
    private VerticalPanel vp;

    private final Entity entity;

    public SummaryPanel(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);

        try {
            createForm();
            add(vp);
            doLayout();
        } catch (NimbitsException e) {
            FeedbackHelper.showError(e);
        }



    }


    private static ComboBox<SummaryTypeOption> summaryTypeOptionComboBox(final String title, final SummaryType selectedValue) {
        ComboBox<SummaryTypeOption> combo = new ComboBox<SummaryTypeOption>();

        List<SummaryTypeOption> ops = new ArrayList<SummaryTypeOption>(SummaryType.values().length);


        ops.add(new SummaryTypeOption(SummaryType.average));
        ops.add(new SummaryTypeOption(SummaryType.standardDeviation));
        ops.add(new SummaryTypeOption(SummaryType.min));
        ops.add(new SummaryTypeOption(SummaryType.max));
        ops.add(new SummaryTypeOption(SummaryType.skewness));
        ops.add(new SummaryTypeOption(SummaryType.sum));
        ops.add(new SummaryTypeOption(SummaryType.variance));




        ListStore<SummaryTypeOption> store = new ListStore<SummaryTypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        SummaryTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() throws NimbitsException {

        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        final TextField<String> summaryName = new TextField<String>();
        summaryName.setFieldLabel("Summary Name");
        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(1d);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Timespan (Seconds)");
        spinnerField.setMinValue(1d);
        spinnerField.setMaxValue(MAX_VALUE);





        try {
            SummaryType type;
            String target = null;

            if (entity.getEntityType().equals(EntityType.summary)) {
                Summary summary = (Summary)entity;
                summaryName.setValue(entity.getName().getValue());
                type = summary.getSummaryType();
                spinnerField.setValue(summary.getSummaryIntervalSeconds());
                target = summary.getTarget();
            }
            else {
                summaryName.setValue(entity.getName().getValue() + " Average");
                type = SummaryType.average;
                spinnerField.setValue(SECONDS_IN_HOUR);

            }

            final EntityName name = CommonFactoryLocator.getInstance().createName(summaryName.getValue(), EntityType.summary);


            // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();


            final ComboBox<SummaryTypeOption> typeCombo = summaryTypeOptionComboBox("Summary Type", type);








            final EntityCombo targetCombo = new EntityCombo(EntityType.point, target, UserMessages.MESSAGE_SELECT_POINT );
            targetCombo.setFieldLabel("Target");

            Button submit = new Button("Submit");
            Button cancel = new Button("Cancel");
            cancel.addSelectionListener(new CancelButtonEventSelectionListener());

            submit.addSelectionListener(new SubmitEventSelectionListener(typeCombo, spinnerField, targetCombo, name));


            Html h = new Html("<p>The summation process runs once an hour and can compute a summary value (such as an average) " +
                    "based on the interval you set here (i.e a setting of 8 will computer an 8 hour average every 8 hours) using the " +
                    "data recorded to the selected data point, storing the result in the select pre-existing target point.</p>");


            Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");





            vp.add(h);
            vp.add(pn);
            simple.add(summaryName, formdata);
            simple.add(typeCombo, formdata);

            simple.add(spinnerField, formdata);
            simple.add(targetCombo, formdata);
            LayoutContainer c = new LayoutContainer();
            HBoxLayout layout = new HBoxLayout();
            layout.setPadding(new Padding(5));
            layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
            layout.setPack(BoxLayout.BoxLayoutPack.END);
            c.setLayout(layout);
            cancel.setWidth(100);
            submit.setWidth(100);
            HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
            c.add(cancel, layoutData);
            c.add(submit, layoutData);



            vp.add(simple);
            vp.add(c);
        } catch (NimbitsException caught) {
            FeedbackHelper.showError(caught);
        }
    }


    private static class SummaryTypeOption extends BaseModelData {
        SummaryType type;


        SummaryTypeOption(SummaryType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public SummaryType getMethod() {
            return type;
        }
    }

    private class UpdateEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        UpdateEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
            box.close();
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(Entity result) {
            box.close();
            try {
                notifyEntityAddedListener(result);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final ComboBox<SummaryTypeOption> typeCombo;
        private final SpinnerField spinnerField;
        private final EntityCombo targetCombo;
        private final EntityName name;

        SubmitEventSelectionListener(ComboBox<SummaryTypeOption> typeCombo, SpinnerField spinnerField, EntityCombo targetCombo, EntityName name) {
            this.typeCombo = typeCombo;
            this.spinnerField = spinnerField;
            this.targetCombo = targetCombo;
            this.name = name;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            EntityServiceAsync service = GWT.create(EntityService.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Create Summary", "please wit...");
            box.show();

            SummaryType summaryType =   typeCombo.getValue().getMethod();

            Summary update;

            if (entity.getEntityType().equals(EntityType.summary)) {

                try {
                   Summary summary = (Summary)entity;
                    update = SummaryModelFactory.createSummary(entity,
                            summary.getSource(), summary.getTarget(), summaryType,
                            spinnerField.getValue().intValue() * 1000, new Date());
                    service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }
            else {
                try {
                    Entity en = EntityModelFactory.createEntity(name, "", EntityType.summary, ProtectionLevel.onlyMe, entity.getKey(), entity.getOwner());
                    update = SummaryModelFactory.createSummary(en,
                            entity.getKey(), targetCombo.getValue().getUUID() , summaryType,
                            spinnerField.getValue().intValue() * 1000, new Date());




                    if (update != null) {
                        update.setName(name);
                        service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));
                    }

                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }
        }
    }



    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }
}


