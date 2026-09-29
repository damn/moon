(ns com.badlogic.gdx.audio.sound
  (:import (com.badlogic.gdx.audio Sound)))

(defn play [sound]
  (.play ^Sound sound))
