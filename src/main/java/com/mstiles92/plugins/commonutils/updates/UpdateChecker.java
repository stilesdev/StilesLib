/**
 * This document is a part of the source code and related artifacts for CommonUtils, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/CommonUtils
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

package com.mstiles92.plugins.commonutils.updates;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * This class is used to check for plugin updates posted to BukkitDev. It will post a notification in the console when
 * a new version is found, and provides methods for plugins to check if a new version has been found for their own use.
 */
public class UpdateChecker implements Runnable {
    private String slug;
    private Logger logger;
    private String currentVersion;
    private boolean updateAvailable = false;
    private String latestVersion;

    /**
     * The main constructor for this class.
     *
     * @param slug the slug of the plugin's page on BukkitDev
     * @param logger the logger for the plugin
     * @param currentVersion the current running version of the plugin
     */
    public UpdateChecker(String slug, Logger logger, String currentVersion) {
        this.slug = slug;
        this.logger = logger;
        this.currentVersion = currentVersion;
    }

    /**
     * The task to be run by the Bukkit scheduler that finds the latest published version on BukkitDev.
     */
    @Override
    public void run() {
        try {
            URL url = new URL("http://api.bukget.org/3/plugins/bukkit/" + slug + "/latest");
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            InputStream stream = connection.getInputStream();
            JSONParser parser = new JSONParser();
            Object o = parser.parse(new InputStreamReader(stream));
            stream.close();

            JSONObject json = (JSONObject) o;

            latestVersion = (String) ((JSONObject) ((JSONArray) json.get("versions")).get(0)).get("version");

            updateAvailable = isNewerVersion(latestVersion);

            if (updateAvailable) {
                logger.info("Update available! New version: " + latestVersion);
                logger.info("More information available at http://dev.bukkit.org/bukkit-plugins/" + slug);
            }
        } catch (IOException | ParseException | ClassCastException e) {
            logger.info("Unable to check for updates. Will try again later. Error message: " + e.getMessage());
        }
    }

    /**
     * Provide simple natural order comparison for version numbers (ie. 2.9 is less than 2.10)
     *
     * @param newVersion the new version to be compared to the current version
     * @return true if the provided version is newer, false if it is not or if this is a SNAPSHOT build
     */
    private boolean isNewerVersion(String newVersion) {
        if (currentVersion.equals(newVersion) || currentVersion.contains("-SNAPSHOT")) {
            return false;
        }

        String[] currentSplit = currentVersion.split("\\.");
        String[] newSplit = newVersion.split("\\.");
        int newInt;
        int currentInt;

        for (int i = 0; i < currentSplit.length || i < newSplit.length; i++) {
            if (i > newSplit.length) {
                newInt = 0;
            } else {
                newInt = Integer.parseInt(newSplit[i]);
            }

            if (i > currentSplit.length) {
                currentInt = 0;
            } else {
                currentInt = Integer.parseInt(currentSplit[i]);
            }

            if (newInt != currentInt) {
                return (newInt > currentInt);
            }
        }

        return false;
    }

    /**
     * Check if an update has been found for the current plugin.
     *
     * @return true if there is an update available for the plugin
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Get the latest version found by this UpdateChecker class.
     *
     * @return the latest version found
     */
    public String getNewVersion() {
        return latestVersion;
    }
}
