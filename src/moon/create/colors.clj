(ns moon.create.colors
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.com.badlogic.gdx.graphics.colors :as colors]))

(defn step [ctx colors]
  (doseq [[name rgba] colors]
    (colors/put! name (color/create rgba)))
  ctx)
