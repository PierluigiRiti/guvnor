/*
 * Copyright 2012 JBoss Inc
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

package org.kie.guvnor.guided.rule.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.shared.rule.CompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromAccumulateCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromCollectCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.FromCompositeFactPattern;
import org.drools.guvnor.models.commons.shared.rule.IFactPattern;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.kie.guvnor.commons.ui.client.resources.i18n.HumanReadableConstants;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.kie.guvnor.guided.rule.client.editor.RuleModeller;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;
import org.kie.guvnor.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.uberfire.client.common.ClickableLabel;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.DirtyableHorizontalPane;
import org.uberfire.client.common.DirtyableVerticalPane;
import org.uberfire.client.common.FormStylePopup;

/**
 * This represents a top level CE, like an OR, NOT, EXIST etc... Contains a list
 * of FactPatterns.
 */
public class CompositeFactPatternWidget extends RuleModellerWidget {

    protected final PackageDataModelOracle completions;
    protected DirtyableFlexTable layout;

    protected CompositeFactPattern pattern;

    protected boolean readOnly;

    protected boolean isFactTypeKnown;

    public CompositeFactPatternWidget( RuleModeller modeller,
                                       EventBus eventBus,
                                       CompositeFactPattern pattern,
                                       Boolean readOnly ) {
        super( modeller,
               eventBus );
        this.completions = modeller.getSuggestionCompletions();
        this.pattern = pattern;

        this.layout = new DirtyableFlexTable();
        this.layout.setStyleName( "model-builderInner-Background" );

        if ( readOnly != null ) {
            this.readOnly = readOnly;
            this.isFactTypeKnown = true;
        } else {
            this.readOnly = false;
            this.isFactTypeKnown = true;
            if ( this.pattern != null && this.pattern.getPatterns() != null ) {
                IFactPattern[] patterns = this.pattern.getPatterns();
                for ( int i = 0; i < patterns.length; i++ ) {
                    IFactPattern p = patterns[ i ];

                    //for empty FROM / ACCUMULATE / COLLECT patterns
                    if ( p.getFactType() == null ) {
                        continue;
                    }

                    if ( !completions.isFactTypeRecognized( p.getFactType() ) ) {
                        this.readOnly = true;
                        this.isFactTypeKnown = false;
                        break;
                    }
                }
            }
        }

        if ( this.readOnly ) {
            layout.addStyleName( "editor-disabled-widget" );
        }

        doLayout();
        initWidget( layout );
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

    private HTML spacerWidget() {
        HTML h = new HTML( "&nbsp;" );
        h.setHeight( "2px" );
        return h;
    }

    /**
     * Wraps a RuleModellerWidget with an icon to delete the pattern
     */
    private Widget wrapLHSWidget( final CompositeFactPattern model,
                                  int i,
                                  RuleModellerWidget w ) {
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();

        final Image remove = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
        remove.setTitle( Constants.INSTANCE.RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt() );
        final int idx = i;
        remove.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                if ( Window.confirm( Constants.INSTANCE.RemoveThisEntireConditionQ() ) ) {
                    if ( pattern.removeFactPattern( idx ) ) {
                        getModeller().refreshWidget();
                    }
                }
            }
        } );

        horiz.setWidth( "100%" );
        w.setWidth( "100%" );

        horiz.add( w );
        if ( !( getModeller().lockLHS() || w.isReadOnly() ) ) {
            horiz.add( remove );
        }

        return horiz;
    }

    protected void doLayout() {
        this.layout.setWidget( 0,
                               0,
                               getCompositeLabel() );
        this.layout.getFlexCellFormatter().setColSpan( 0,
                                                       0,
                                                       2 );

        //this.layout.getFlexCellFormatter().setWidth(0, 0, "15%");
        this.layout.setWidget( 1,
                               0,
                               new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );

        if ( this.pattern.getPatterns() != null ) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            IFactPattern[] facts = pattern.getPatterns();
            for ( int i = 0; i < facts.length; i++ ) {
                RuleModellerWidget widget = this.getModeller().getWidgetFactory().getWidget( this.getModeller(),
                                                                                             this.getEventBus(),
                                                                                             facts[ i ],
                                                                                             this.readOnly );
                widget.addOnModifiedCommand( new Command() {
                    public void execute() {
                        setModified( true );
                    }
                } );

                //Wrap widget so the Fact pattern can be deleted
                vert.add( wrapLHSWidget( pattern,
                                         i,
                                         widget ) );
                vert.add( spacerWidget() );
            }
            this.layout.setWidget( 1,
                                   1,
                                   vert );
        }
    }

    protected Widget getCompositeLabel() {
        ClickHandler click = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget w = (Widget) event.getSource();
                showFactTypeSelector( w );
            }
        };
        String lbl = HumanReadable.getCEDisplayName(pattern.getType());

        if ( pattern.getPatterns() == null || pattern.getPatterns().length == 0 ) {
            lbl += " <font color='red'>" + Constants.INSTANCE.clickToAddPatterns() + "</font>";
        }

        return new ClickableLabel( lbl + ":",
                                   click,
                                   !this.readOnly );
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector( final Widget w ) {
        final ListBox box = new ListBox();
        PackageDataModelOracle completions = this.getModeller().getSuggestionCompletions();
        String[] facts = completions.getFactTypes();

        box.addItem( Constants.INSTANCE.Choose() );
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[ i ] );
        }
        box.setSelectedIndex( 0 );

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle( Constants.INSTANCE.NewFactPattern() );
        popup.addAttribute( Constants.INSTANCE.chooseFactType(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                pattern.addFactPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        final Button fromBtn = new Button( HumanReadableConstants.INSTANCE.From() );
        final Button fromAccumulateBtn = new Button( HumanReadableConstants.INSTANCE.FromAccumulate() );
        final Button fromCollectBtn = new Button( HumanReadableConstants.INSTANCE.FromCollect() );
        ClickHandler btnsClickHandler = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget sender = (Widget) event.getSource();
                if ( sender == fromBtn ) {
                    pattern.addFactPattern(
                            new FromCompositeFactPattern() );
                } else if ( sender == fromAccumulateBtn ) {
                    pattern.addFactPattern(
                            new FromAccumulateCompositeFactPattern() );
                } else if ( sender == fromCollectBtn ) {
                    pattern.addFactPattern(
                            new FromCollectCompositeFactPattern() );
                } else {
                    throw new IllegalArgumentException( "Unknown sender: "
                                                                + sender );
                }

                setModified( true );
                getModeller().refreshWidget();
                popup.hide();

            }
        };

        fromBtn.addClickHandler( btnsClickHandler );
        fromAccumulateBtn.addClickHandler( btnsClickHandler );
        fromCollectBtn.addClickHandler( btnsClickHandler );
        popup.addAttribute( "",
                            fromBtn );
        popup.addAttribute( "",
                            fromAccumulateBtn );
        popup.addAttribute( "",
                            fromCollectBtn );

        popup.show();
    }

}
