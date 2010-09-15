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
package com.google.gwt.editor.client;

/**
 * Editors whose behavior changes based on the value being edited will implement
 * this interface.
 * 
 * @param <T> the type of composite object the editor can display
 */
public interface ValueAwareEditor<T> extends Editor<T>, HasEditorDelegate<T> {
  /**
   * Indicates that the Editor cycle is finished. This method will be called in
   * a depth-first order by the EditorDriver, so Editors do not generally need
   * to flush their sub-editors.
   */
  void flush();

  /**
   * Notifies the Editor that one or more value properties have changed. Not all
   * backing services support property-based notifications.
   */
  void onPropertyChange(String... paths);

  /**
   * Called by the EditorDriver to set the object the Editor is peered with
   * <p>
   * ValueAwareEditors should preferentially use sub-editors to alter the
   * properties of the object being edited. If this is not feasible, the value
   * provided to this method must not be mutated directly without calling
   * {@link EditorDelegate#ensureMutable()} to obtain a guaranteed-mutable
   * instance.
   * <p>
   */
  void setValue(T value);
}