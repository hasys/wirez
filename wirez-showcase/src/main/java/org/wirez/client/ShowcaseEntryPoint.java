/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wirez.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import javax.inject.Inject;

import static org.uberfire.workbench.model.menu.MenuFactory.newTopLevelMenu;

/**
 * GWT's entry-point for wirez showcase.
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private ActivityManager activityManager;
    
    @AfterInitialization
    public void startApp() {
        setupMenu();
        hideLoadingPopup();

        // Default perspective.
        placeManager.goTo( new DefaultPlaceRequest( "HomePerspective" ) );
    }

    private void setupMenu() {
        final Menus menus =
                newTopLevelMenu("Home").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("HomePerspective"));
                    }
                }).endMenu().
                newTopLevelMenu("Wirez").respondsWith(new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo(new DefaultPlaceRequest("WirezPerspective"));
                    }
                }).endMenu()
                .build();
        menubar.addMenus(menus);
    }

    // Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}