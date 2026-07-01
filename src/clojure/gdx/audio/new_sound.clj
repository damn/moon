(ns clojure.gdx.audio.new-sound
  (:import (com.badlogic.gdx Audio)))

(defn f [audio file-handle]
  (Audio/.newSound audio file-handle))
