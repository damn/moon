(ns entity.render.string-effect
  (:require [game.entity :as entity]
            [game.ctx :as ctx]))

(defmethod entity/render :entity/string-effect
  [[_k {:keys [text]}] entity ctx]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 (ctx/world-unit-scale ctx)))
                  :scale 2
                  :up? true}]]))
