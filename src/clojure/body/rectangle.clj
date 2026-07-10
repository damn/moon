(ns clojure.body.rectangle
  (:require [gdl.math.rectangle :as rectangle]
            [com.badlogic.gdx.math.rectangle :as gdx-rectangle]))

(defn ->rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (gdx-rectangle/new x y width height)))
