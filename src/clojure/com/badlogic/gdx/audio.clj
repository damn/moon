(ns clojure.com.badlogic.gdx.audio
  (:import (com.badlogic.gdx Audio)
           (com.badlogic.gdx.files FileHandle)))

(defn new-sound [^Audio audio ^FileHandle file-handle]
  (.newSound audio file-handle))
