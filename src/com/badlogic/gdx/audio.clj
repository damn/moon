(ns com.badlogic.gdx.audio
  (:import (com.badlogic.gdx Audio)))

(defn newSound [audio file-handle]
  (.newSound ^Audio audio file-handle))
