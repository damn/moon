(ns clojure.gdx.sound.play!
  (:import (com.badlogic.gdx.audio Sound)))

(defn f [sound]
  (Sound/.play sound))
