/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.guvnor.guided.dtable.client.wizard.pages.cells;

import org.kie.guvnor.commons.ui.client.resources.WizardResources;
import org.drools.guvnor.models.guided.dtable.shared.model.Pattern52;

/**
 * A cell to display a Fact Pattern on the Action Set Field page. Additional
 * validation is performed on the Pattern's fields to determine whether the cell
 * should be rendered as valid or invalid
 */
public class ActionSetFieldPatternCell extends PatternCell {

    protected String getCssStyleName( final Pattern52 p ) {
        if ( !validator.isPatternBindingUnique( p ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        if ( !validator.arePatternActionSetFieldsValid( p ) ) {
            return WizardResources.INSTANCE.css().wizardDTableValidationError();
        }
        return "";
    }

}
