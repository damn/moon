(ns moon.audio
  (:import (com.badlogic.gdx.audio Sound)))

(defn sound-names [sounds]
  (map first sounds))

(defn play! [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (Sound/.play (get sounds sound-name)))
