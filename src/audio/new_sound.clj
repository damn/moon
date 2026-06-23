(ns audio.new-sound
  (:import (com.badlogic.gdx Audio)))

(defn f [^Audio audio file-handle]
  (.newSound audio file-handle))
