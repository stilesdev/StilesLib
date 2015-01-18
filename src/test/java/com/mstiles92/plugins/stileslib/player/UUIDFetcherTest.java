/*
 * This document is a part of the source code and related artifacts for StilesLib, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/StilesLib
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the license.
 */

package com.mstiles92.plugins.stileslib.player;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class UUIDFetcherTest {
    private Map<String, UUID> knownValues = new HashMap<>();

    public UUIDFetcherTest() {
        knownValues.put("mstiles92", UUID.fromString("17e09889-a71d-406c-a88c-bde3655ccd76"));
        knownValues.put("Nightlock_", UUID.fromString("273267b1-e6ff-4551-8636-6050ed71f017"));
        knownValues.put("BebopVox", UUID.fromString("6835219e-d8c1-44be-8780-16fe069f9725"));
        knownValues.put("direwolf20", UUID.fromString("bbb87dbe-690f-4205-bdc5-72ffb8ebc29d"));
        knownValues.put("Pahimar", UUID.fromString("0192723f-b3dc-495a-959f-52c53fa63bff"));
        knownValues.put("mollstam", UUID.fromString("f8cdb683-9e90-43ee-a819-39f85d9c5d69"));
    }

    @Test
    public void constructor_nullProvided_throwsException() {
        try {
            UUIDFetcher fetcher = new UUIDFetcher((String[]) null);
        } catch (IllegalArgumentException e) {
            return;
        }

        fail("UUIDFetcher constructed with null did not throw an IllegalArgumentException.");
    }

    @Test
    public void constructor_emptyStringProvided_throwsException() {
        try {
            UUIDFetcher fetcher = new UUIDFetcher("");
        } catch (IllegalArgumentException e) {
            return;
        }

        fail("UUIDFetcher constructed with empty string did not throw an IllegalArgumentException.");
    }

    @Test
    public void execute_existingNameProvided_returnsCorrectId() {
        String username = "mstiles92";
        UUID expected = knownValues.get(username);
        UUIDFetcher fetcher = new UUIDFetcher(username);
        Map<String, UUID> results = fetcher.execute();

        assertNotNull("UUIDFetcher.execute() returned null", results);
        assertEquals("Length of results was not equal to 1", 1, results.size());

        UUID result = results.get("mstiles92");

        assertNotNull("No result found for \"mstiles92\"", result);
        assertEquals("UUID retrieved was not equal to expected", expected, result);
    }

    @Test
    public void execute_multipleExistingNamesProvided_returnsCorrectIds() {
        List<String> usernames = new ArrayList<>(knownValues.keySet());
        UUIDFetcher fetcher = new UUIDFetcher(usernames.toArray(new String[usernames.size()]));
        Map<String, UUID> results = fetcher.execute();

        assertNotNull("UUIDFetcher.execute() returned null", results);
        assertEquals("Length of results was not equal to length of request", knownValues.size(), results.size());

        for (Map.Entry<String, UUID> entry : knownValues.entrySet()) {
            UUID expected = entry.getValue();
            UUID actual = results.get(entry.getKey());

            assertNotNull("No result found for \"" + entry.getKey() + "\"", actual);
            assertEquals("UUID retrieved was not equal to expected for \"" + entry.getKey() + "\"", expected, actual);
        }
    }

    @Test
    public void execute_nonExistingNameProvided_returnsEmptyMap() {
        UUIDFetcher fetcher = new UUIDFetcher("thisnameistoolongtoexist");
        Map<String, UUID> results = fetcher.execute();

        assertNotNull("UUIDFetcher.execute() returned null", results);
        assertEquals("Non-existing name request did not return an empty map", 0, results.size());
    }

    @Test
    public void execute_mixedExistingNamesProvided_returnsCorrectNumResults() {
        List<String> usernames = new ArrayList<>(knownValues.keySet());
        usernames.add("thisnameistoolongtoexist");
        UUIDFetcher fetcher = new UUIDFetcher(usernames.toArray(new String[usernames.size()]));
        Map<String, UUID> results = fetcher.execute();

        assertNotNull("UUIDFetcher.execute() returned null", results);
        assertEquals("Length of results was not equal to number of valid requested names", knownValues.size(), results.size());
    }
}
