(ns clojure.gdx.graphics.colors
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]))

(defn put! [colors]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba))))
