(ns clojure.gdx.audio
  (:import (com.badlogic.gdx Audio)))

(defn new-sound [audio file-handle]
  (Audio/.newSound audio file-handle))
