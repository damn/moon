(ns clojure.play
  (:require [com.badlogic.gdx.audio.sound :as sound]))

(defn f [sound]
  (sound/play! sound))
