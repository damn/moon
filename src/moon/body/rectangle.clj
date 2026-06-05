(ns moon.body.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn ->rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (Rectangle. x y width height)))
