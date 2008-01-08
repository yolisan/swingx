/*
 * $Id$
 *
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jdesktop.swingx;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.calendar.CalendarUtils;

/**
 * JXMonthView unit tests which are expected to pass and are part of a stable build.
 *  
 * This one does not use mocks but some methods 
 * of InteractiveTestCase. That's why the passing methods could not be moved
 * into JXMonthViewTest.
 * 
 * @author Jeanette Winzenburg
 */
public class JXMonthViewVisualTest extends InteractiveTestCase {
    @SuppressWarnings("all")
    private static final Logger LOG = Logger.getLogger(JXMonthViewVisualTest.class
            .getName());

    // Constants used internally; unit is milliseconds
    @SuppressWarnings("unused")
    private static final int ONE_MINUTE = 60*1000;
    @SuppressWarnings("unused")
    private static final int ONE_HOUR   = 60*ONE_MINUTE;
    @SuppressWarnings("unused")
    private static final int THREE_HOURS = 3 * ONE_HOUR;
    @SuppressWarnings("unused")
    private static final int ONE_DAY    = 24*ONE_HOUR;

    @SuppressWarnings("unused")
    private Calendar calendar;

    /**
     * #702-swingx: no days shown?
     * 
     * Not reproducible.
     */
    public void testBlankMonthViewOnAdd() {
       final JComponent comp = Box.createHorizontalBox();
       comp.add(new JXMonthView());
       final JXFrame frame = wrapInFrame(comp, "blank view on add");
       Action next = new AbstractActionExt("new monthView") {

           public void actionPerformed(ActionEvent e) {
               comp.add(new JXMonthView());
               frame.pack();
           }
           
       };
       addAction(frame, next);
       frame.pack();
       frame.setVisible(true);
    };

//----------------------
    
    /**
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: initial packed size - one month shown.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateInitial() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                long uiLast = monthView.getUI().getLastDisplayedDate();
                long viewLast = monthView.getLastDisplayedDate();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    
    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that lastDisplayed from monthView is same as lastDisplayed from ui.
     * 
     * Here: change the size of the view which allows the ui to display more
     * columns/rows.
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateSizeChanged() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                long uiLast = monthView.getUI().getLastDisplayedDate();
                long viewLast = monthView.getLastDisplayedDate();
                assertEquals(uiLast, viewLast);
            }
        });
    }
    

    /**
     * 
     * Issue #659-swingx: lastDisplayedDate must be synched.
     * 
     * test that ensureDateVisible works as doc'ed if multiple months shown: 
     * if the new date is in the
     * month following the last visible then the first must be set in a manner that
     * the date must be visible in the last month. 
     * 
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public void testLastDisplayedDateSizeChangedEnsureVisible() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            LOG.info("cannot run lastDisplayedDate - headless");
            return;
        }
        final JXMonthView monthView = new JXMonthView();
        final JXFrame frame = wrapInFrame(monthView, "");
        frame.setVisible(true);
        frame.setSize(frame.getWidth() * 3, frame.getHeight() * 2);
        // force a revalidate
        frame.invalidate();
        frame.validate();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(monthView.getFirstDisplayedDate());
                int firstMonth = calendar.get(Calendar.MONTH);
                long uiLast = monthView.getUI().getLastDisplayedDate();
                calendar.setTimeInMillis(uiLast);
                int lastMonth = calendar.get(Calendar.MONTH);
                // sanity: more than one month shown
                assertFalse(firstMonth == lastMonth);
                // first day of next month 
                calendar.add(Calendar.DATE, 1);
                // sanity
                int newLastMonth = calendar.get(Calendar.MONTH);
                assertFalse(lastMonth == newLastMonth);
                monthView.ensureDateVisible(calendar.getTimeInMillis());
                CalendarUtils.endOfMonth(calendar);
                long newUILast = monthView.getUI().getLastDisplayedDate();
                assertEquals(newUILast, monthView.getLastDisplayedDate());
                calendar.setTimeInMillis(newUILast);
//                LOG.info("first/last: " + new Date(monthView.getFirstDisplayedDate()) + 
//                        "/" + new Date(newUILast));
                assertEquals(newLastMonth, calendar.get(Calendar.MONTH));
            }
        });
    }
    


    @Override
    protected void setUp() throws Exception {
        calendar = Calendar.getInstance();
    }

  
}
