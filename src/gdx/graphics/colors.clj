(ns gdx.graphics.colors
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]))

(defn put! [colors-map]
  (doseq [[name color] colors-map]
    (colors/put! name (color/create color))))
