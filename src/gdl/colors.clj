(ns gdl.colors
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.com.badlogic.gdx.graphics.colors :as colors]))

(defn put! [colors]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba))))
