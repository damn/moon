(ns clojure.com.badlogic.gdx.scenes.scene2d.event
  "The base class for all events.
  
  By default an event will \"bubble\" up through an actor's parent's handlers (see set bubbles).
  
  An actor's capture listeners can stop an event to prevent child actors from seeing it.
  
  An Event may be marked as \"handled\" which will end its propagation outside of the Stage (see handle). The default
  fire will mark events handled if an event listener returns true.
  
  A cancelled event will be stopped and handled. Additionally, many actors will undo the side-effects of a canceled event. (See
  cancel.)"
  (:import (com.badlogic.gdx.scenes.scene2d Event)))

(defn stage
  "The stage for the actor the event was fired on."
  [^Event event]
  (.getStage event))
