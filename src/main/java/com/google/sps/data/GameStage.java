// Copyright 2020 Google LLC
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
 
public class GameStage {
    private String name;
    private String id;
    private String content;
    private Key quizKey;
 
    public GameStage (String name, String content){
        this.name = name;
        this.content = content;
    }
 
    public String getName() {
        return name;
    }
 
    public String getID() {
        return id;
    }
 
    public String getContent() {
        return content;
    }
 
    public Key getQuizKey() {
        return quizKey;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setID(String id) {
        this.id = id;
    }
 
    public void setContent(String content) { 
        this.content = content;
    }
 
    public void setQuizKey(Key quizKey) {
        this.quizKey = quizKey;
    }
}
 
