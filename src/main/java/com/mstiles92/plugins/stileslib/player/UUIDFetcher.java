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

import com.mstiles92.plugins.stileslib.util.BasicHttpClient;
import org.apache.commons.lang.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class UUIDFetcher {
    // Limit set by Mojang's API
    private static final int PROFILES_PER_REQUEST = 100;

    private List<String> usernames;

    public UUIDFetcher(String... usernames) {
        Validate.notNull(usernames);
        Validate.notEmpty(usernames);

        for (String username : usernames) {
            Validate.notNull(username);
            Validate.notEmpty(username);
        }

        this.usernames = Arrays.asList(usernames);
    }

    public Map<String, UUID> execute() {
        Map<String, UUID> results = new HashMap<>();
        try {
            BasicHttpClient client = new BasicHttpClient(new URL("https://api.mojang.com/profiles/minecraft"));
            client.addHeader("Content-Type", "application/json");

            int requests = (int) Math.ceil((double) usernames.size() / PROFILES_PER_REQUEST);

            for (int i = 0; i < requests; i++) {
                int start = i * PROFILES_PER_REQUEST;
                int end = Math.min((i + 1) * PROFILES_PER_REQUEST, usernames.size());
                client.setBody(JSONArray.toJSONString(usernames.subList(start, end)));

                String response = client.post();

                JSONArray responseJson = (JSONArray) new JSONParser().parse(response);

                for (Object o : responseJson) {
                    JSONObject profile = (JSONObject) o;

                    String id = (String) profile.get("id");
                    String name = (String) profile.get("name");

                    results.put(name, getUUID(id));
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private UUID getUUID(String rawId) {
        return UUID.fromString(rawId.substring(0, 8) + "-" + rawId.substring(8, 12) + "-" + rawId.substring(12, 16) + "-" + rawId.substring(16, 20) + "-" + rawId.substring(20, 32));
    }
}
