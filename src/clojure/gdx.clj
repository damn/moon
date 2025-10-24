(ns clojure.gdx
  (:import (com.badlogic.gdx Gdx)))

(defn state
  []
  {:audio    Gdx/audio
   :files    Gdx/files
   :graphics Gdx/graphics
   :input    Gdx/input})

(defn new-sound [file-handle]
  (.newSound Gdx/audio file-handle))
