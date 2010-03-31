/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.expenses.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.sample.expenses.gen.ExpenseRequestFactoryImpl;
import com.google.gwt.sample.expenses.shared.EmployeeKey;
import com.google.gwt.sample.expenses.shared.ReportChanged;
import com.google.gwt.sample.expenses.shared.ReportKey;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.valuestore.client.ValueStoreJsonImpl;
import com.google.gwt.valuestore.shared.DeltaValueStore;
import com.google.gwt.valuestore.shared.Property;
import com.google.gwt.valuestore.shared.Values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * <p>
 * This app is a mess right now, but it will become the showcase example of a
 * custom app written to RequestFactory
 */
public class Expenses implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final HandlerManager eventBus = new HandlerManager(null);
    final ValueStoreJsonImpl valueStore = new ValueStoreJsonImpl(eventBus);
    final ExpenseRequestFactoryImpl requestFactory = new ExpenseRequestFactoryImpl(
        valueStore);

    RootLayoutPanel root = RootLayoutPanel.get();

    final Shell shell = new Shell();
    final EmployeeList employees = new EmployeeList(shell.users);

    root.add(shell);

    shell.setListener(new Shell.Listener() {
      public void setFirstPurpose(String purpose) {
        DeltaValueStore deltaValueStore = requestFactory.getValueStore().spawnDeltaView();
        Values<ReportKey> report = shell.getValues().get(0);
        deltaValueStore.set(report.getKey().getPurpose(), report, purpose);
        requestFactory.syncRequest(deltaValueStore).fire();
      }
    });

    employees.setListener(new EmployeeList.Listener() {
      public void onEmployeeSelected(Values<EmployeeKey> e) {
        requestFactory.reportRequest().findReportsByEmployee(
            e.getRef(EmployeeKey.get().getId())).forProperties(
            getReportColumns()).to(shell).fire();
      }
    });

    eventBus.addHandler(ReportChanged.TYPE, shell);

    requestFactory.employeeRequest().findAllEmployees().forProperties(
        getEmployeeMenuProperties()).to(employees).fire();
  }

  private Collection<Property<EmployeeKey, ?>> getEmployeeMenuProperties() {
    final EmployeeKey key = EmployeeKey.get();
    List<Property<EmployeeKey, ?>> columns = new ArrayList<Property<EmployeeKey, ?>>();
    columns.add(key.getDisplayName());
    columns.add(key.getUserName());
    return columns;
  }

  private Collection<Property<ReportKey, ?>> getReportColumns() {
    final ReportKey key = ReportKey.get();
    List<Property<ReportKey, ?>> columns = new ArrayList<Property<ReportKey, ?>>();
    columns.add(key.getCreated());
    columns.add(key.getPurpose());
    return columns;
  }
}