(ns clojure.com.badlogic.gdx.scenes.scene2d.utils.layout
  "Provides methods for an actor to participate in layout and to provide a minimum, preferred, and maximum size."
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn pack!
  "Sizes this actor to its preferred width and height, then calls validate.
  
  Generally this method should not be called in an actor's constructor because it calls layout, which means a
  subclass would have layout() called before the subclass' constructor. Instead, in constructors simply set the actor's size
  to get pref width and get pref height. This allows the actor to have a size at construction time for more
  convenient use with groups that do not layout their children."
  [^Layout layout]
  (.pack layout))
