(ns sound.play
  (:import (com.badlogic.gdx.audio Sound)))

(defn f! [^Sound sound]
  (.play sound))
