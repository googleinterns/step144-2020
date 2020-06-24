// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.appengine.api.datastore.Key;

public class SiteUser {
    private String displayName;
    private String email;
    private Key key;
    private String imageID;
    private String currentPageID;

    public SiteUser (String displayName, String email){
        this.displayName = displayName;
        this.email = email;
    }

    public String getdisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public Key getKey(){
        return key;
    }

    public String getImageID() {
        return imageID;
    }

    public String getCurrentPageID() {
        return currentPageID;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public void setCurrentPageID(String currentPageID) {
        this.currentPageID = currentPageID;
    }
}
