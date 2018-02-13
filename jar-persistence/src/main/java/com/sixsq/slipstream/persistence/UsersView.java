package com.sixsq.slipstream.persistence;

import com.google.gson.Gson;
import com.sixsq.slipstream.user.UserView;

import java.util.ArrayList;
import java.util.List;

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
public class UsersView {

    private static final Gson gson = new Gson();

    private List<UserView> users = new ArrayList<>();

    public List<UserView> getUsers() {
        return users;
    }

    public static UsersView fromJson(String jsonRecords) {
        return gson.fromJson(jsonRecords, UsersView.class);
    }


}
