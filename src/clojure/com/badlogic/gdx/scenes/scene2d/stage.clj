(ns clojure.com.badlogic.gdx.scenes.scene2d.stage
  "A 2D scene graph containing hierarchies of [[clojure.com.badlogic.gdx.scenes.scene2d.actor]]. Stage handles the viewport and distributes input events.
  
  set viewport controls the coordinates used within the stage and sets up the camera used to convert between
  stage coordinates and screen coordinates.
  
  A stage must receive input events so it can distribute them to actors. This is typically done by passing the stage to
  [clojure.com.badlogic.gdx.input/set-input-processor!]. An input multiplexer may
  be used to handle input events before or after the stage does. If an actor handles an event by returning true from the input
  method, then the stage's input method will also return true, causing subsequent InputProcessors to not receive the event.
  
  The Stage and its constituents (like Actors and Listeners) are not thread-safe and should only be updated and queried from a
  single thread (presumably the main render thread). Methods should be reentrant, so you can update Actors and Stages from within
  callbacks and handlers."
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn create
  "Creates a stage with the specified viewport and batch. This can be used to specify an existing batch or to customize which
  batch implementation is used.
  
  - `batch`: Will not be disposed if [clojure.com.badlogic.gdx.utils.disposable/dispose!] is called, handle disposal yourself."
  [^Viewport viewport ^Batch batch]
  (Stage. viewport batch))

(defn add-actor!
  "Adds an actor to the root of the stage."
  [^Stage stage ^Actor actor]
  (.addActor stage actor))

(defn viewport [^Stage stage]
  (.getViewport stage))

(defn root
  "Returns the root group which holds all actors in the stage."
  [^Stage stage]
  (.getRoot stage))

(defn hit [^Stage stage x y]
  (.hit stage (float x) (float y) true))

(defn act!
  "Calls [act!] with [clojure.com.badlogic.gdx.graphics/delta-time], limited to a minimum of 30fps."
  [^Stage stage]
  (.act stage))

(defn draw! [^Stage stage]
  (.draw stage))
