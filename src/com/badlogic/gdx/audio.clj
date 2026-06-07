(ns com.badlogic.gdx.audio
  (:import (com.badlogic.gdx Audio)))

(defn new-sound [^Audio audio file-handle]
  (.newSound audio file-handle))
