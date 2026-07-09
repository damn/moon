(ns com.badlogic.gdx.audio.sound
  (:import
           (com.badlogic.gdx.audio Sound)
           ))

(defn f [sound]
  (Sound/.play sound))
