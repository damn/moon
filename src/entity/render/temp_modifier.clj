(ns entity.render.temp-modifier
  (:require [game.entity :as entity]))

(defmethod entity/render :entity/temp-modifier
  [_ entity {:keys [ctx/colors]}]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    (:colors/temp-modifier colors)]])
