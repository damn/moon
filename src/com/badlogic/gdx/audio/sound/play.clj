(ns com.badlogic.gdx.audio.sound.play
  (:import (com.badlogic.gdx.audio Sound)))

(defn play! [^Sound sound]
  (.play sound))
