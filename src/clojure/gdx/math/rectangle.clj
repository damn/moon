(ns clojure.gdx.math.rectangle
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]))

(defn create [x y width height]
  (rectangle/new x y width height))
