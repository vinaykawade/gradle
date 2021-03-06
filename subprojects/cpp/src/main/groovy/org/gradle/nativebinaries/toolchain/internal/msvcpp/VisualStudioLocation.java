/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativebinaries.toolchain.internal.msvcpp;

import java.io.File;

public class VisualStudioLocation {
    private final File installDir;
    private final File visualStudioDir;
    private final File windowsSdkDir;

    public static VisualStudioLocation findByCandidate(File candidate) {
        return new VisualStudioLocation(candidate);
    }

    public static VisualStudioLocation findDefault() {
        return new VisualStudioLocation();
    }

    private VisualStudioLocation(File candidate) {
        installDir = candidate;
        visualStudioDir = locateVisualStudio(candidate);
        windowsSdkDir = locateWindowsSdk(visualStudioDir);
    }

    private VisualStudioLocation() {
        installDir = null;
        visualStudioDir = locateDefaultVisualStudio();
        windowsSdkDir = locateWindowsSdk(visualStudioDir);
    }

    private File locateVisualStudio(File candidate) {
        while (candidate != null) {
            if (isVisualStudio(candidate)) {
                return candidate;
            }
            candidate = candidate.getParentFile();
        }
        return null;
    }

    private File locateDefaultVisualStudio() {
        // TODO:DAZ Iterate over all local drives?
        String[] candidateLocations = new String[] {
                "C:/Program Files (x86)/Microsoft Visual Studio 10.0",
                "C:/Program Files/Microsoft Visual Studio 10.0",
        };
        for (String candidateLocation : candidateLocations) {
            File candidate = new File(candidateLocation);
            if (isVisualStudio(new File(candidateLocation))) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isVisualStudio(File candidate) {
        return new File(candidate, "VC/bin/cl.exe").isFile();
    }

    private File locateWindowsSdk(File visualStudioDir) {
        if (visualStudioDir == null) {
            return null;
        }
        // Locate Windows SDK relative to VisualStudio install, as well as in standard locations
        // TODO:DAZ Iterate over all local drives?
        String visualStudioPrograms = visualStudioDir.getParent();
        String[] candidateLocations = new String[] {
                visualStudioPrograms + "/Microsoft SDKs/Windows/v7.1",
                "C:/Program Files (x86)/Microsoft SDKs/Windows/v7.1",
                "C:/Program Files/Microsoft SDKs/Windows/v7.1",
                visualStudioPrograms + "/Microsoft SDKs/Windows/v7.0A",
                "C:/Program Files (x86)/Microsoft SDKs/Windows/v7.0A",
                "C:/Program Files/Microsoft SDKs/Windows/v7.0A",
        };
        for (String candidateLocation : candidateLocations) {
            File candidate = new File(candidateLocation);
            if (isWindowsSdk(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isWindowsSdk(File candidate) {
        return new File(candidate, "bin/rc.exe").isFile();
    }

    public File getInstallDir() {
        return installDir;
    }

    public File getVisualStudioDir() {
        return visualStudioDir;
    }

    public File getWindowsSdkDir() {
        return windowsSdkDir;
    }

    public boolean isFound() {
        return visualStudioDir != null;
    }
}
