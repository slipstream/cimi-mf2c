package com.sixsq.slipstream.statemachine;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
 * +=================================================================+
 * SlipStream Server (WAR)
 * =====
 * Copyright (C) 2013 SixSq Sarl (sixsq.com)
 * =====
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
 * -=================================================================-
 */

public enum States {
    Initializing,
    Provisioning,
    Executing,
    SendingReports,
    Ready,
    Finalizing,
    Done,
    Cancelled,
    Aborted,
    Unknown;

    public static List<States> completed() {
    	return Arrays.asList(States.Cancelled,
    						 States.Aborted,
    						 States.Unknown,
    						 States.Done);
    }

    public static List<States> active() {
        Set<States> states = new HashSet<States>(Arrays.asList(States.values()));
        states.removeAll(completed());
        return new ArrayList<States>(states);
    }

    public static List<States> transition() {
    	return Arrays.asList(Initializing,
    						 Provisioning,
    						 Executing,
    						 SendingReports,
    						 Finalizing,
    						 Unknown);
    }

    public static List<States> canTerminate() {
    	return Arrays.asList(Aborted,
    						 Done,
    						 Cancelled,
    						 Ready);
    }

}
