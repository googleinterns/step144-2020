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
 
public class RandomEvent {
    /** Random Events are events that appear to the player at random which causes a change in their current
    * game stage, as well as their game path.
    */

    private String name;
    /** Represents the title of the random event 
    */
    private String content;
    /** Represents what text content will be displayed once random event is loaded 
    */
    private Boolean willRedirect;
    /** Represents whether or not the player's random event will be changed 
    */
    private String redirectStageID;
    /** Represents the datastore id of the game stage the player is moved to. 
    */
 
    /** Creates a random event with the specified name and content
    * @param name The name of the random event 
    * @param content The text content tied to the random event
    */
    public RandomEvent (String name, String content){
        this.name = name;
        this.content = content;
    }
 
    /** Gets the random event's name
    * @return A string representing the title of the random event 
    */
    public String getName() {
        return name;
    }
    
    /** Gets the random event's content
    @return A string representing the text content of the random event 
    */
    public String getContent() {
        return content;
    }
 
    /** Gets the whether or not the player's game stage will be changed
    @return A boolean representing whether or not the player's game stage will be changed 
    */
    public Boolean getWillRedirect() {
        return willRedirect;
    }

    /** Gets the new game stage ID 
    @return A string representing the datastore id of the game stage the player is moved to.
    */
    public String getRedirectStageID () {
        return redirectStageID;
    }

    /** Sets the random event's name
    @param name A string representing the name of the random event 
    */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Sets the random event's content
    @param content A string representing the text content of the random event 
    */
    public void setContent(String content) {
        this.content = content;
    }

    /** Sets whether or not the player's game stage will be changed
    @param willRedirect A boolean representing whether or not the player's game stage will be changed 
    */
    public void setWillRedirect(Boolean willRedirect) {
        this.willRedirect = willRedirect;
    }
    
     /** Sets the new game stage ID
    @param redirectStageID A string representing the datastore id of the game stage the player is moved to. 
    */
    public void setRedirectStageID(String redirectStageID) {
        this.redirectStageID = redirectStageID;
    }
}
 
