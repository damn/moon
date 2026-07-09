(ns clojure.gdx.audio
  (:require [com.badlogic.gdx.audio :as audio]))

(defn new-sound [& args]
  (apply audio/new-sound args))
