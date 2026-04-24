(ns clojure.gdx.colors
  (:require [clojure.gdx.graphics.color :as color]
            [clojure.gdx.graphics.colors :as colors]))

(defn put! [colors]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba))))
