(ns clojure.play
  (:require [com.badlogic.gdx.audio.sound :as sound]))

(defn f [& args]
  (apply sound/f args))
