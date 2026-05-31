(ns entity.render.animation
  (:require [clojure.animation :as animation]
            [game.entity :as entity]))

(defmethod entity/render :entity/animation
  [[_k animation] entity ctx]
  (entity/render [:entity/image (animation/current-frame animation)]
                 entity
                 ctx))
