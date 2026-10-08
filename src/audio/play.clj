(ns audio.play
  (:import (com.badlogic.gdx.audio Sound)))

(defn play-sound! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))
