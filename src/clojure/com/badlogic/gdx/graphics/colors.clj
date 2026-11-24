(ns clojure.com.badlogic.gdx.graphics.colors
  "A general purpose class containing named colors that can be changed at will. For example, the markup language defined by the
  bitmap font cache class uses this class to retrieve colors and the user can define his own colors."
  (:import (com.badlogic.gdx.graphics Color Colors)))

(defn put
  "Convenience method to add a color with its name.
  
  - `name`: the name of the color
  
  - `color`: the color
  
  Returns the previous color associated with name, or null if there was no mapping for name."
  [^String name ^Color color]
  (Colors/put name color))
