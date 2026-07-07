(ns clojure.render-string-effect
  (:require [clojure.world-unit-scale :as world-unit-scale]))

(defn f
  [{:keys [text]} entity _ctx]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 world-unit-scale/world-unit-scale))
                  :scale 2
                  :up? true}]]))
