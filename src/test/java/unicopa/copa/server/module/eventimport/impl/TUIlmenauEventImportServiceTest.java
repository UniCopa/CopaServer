/*
 * Copyright (C) 2013 UniCoPA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package unicopa.copa.server.module.eventimport.impl;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Felix Wiemuth
 */
public class TUIlmenauEventImportServiceTest {
    TUIlmenauEventImportService service;
    
    public TUIlmenauEventImportServiceTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        service = new TUIlmenauEventImportService(new FileInputStream("eventImport.properties"));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getSnapshot method, of class TUIlmenauEventImportService.
     */
    @Test
    public void testGetSnapshotTest() throws Exception {
        service.getSnapshotTest();
    }
}