(ns game.entity.mouseover
  (:require [moon.entity :as entity]
            [moon.faction :as faction]))

(def mouseover-ellipse-width 5)

(defmethod entity/render :entity/mouseover?
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/colors
           ctx/player-eid]}]
  (let [player @player-eid]
    [[:draw/with-line-width mouseover-ellipse-width
      [[:draw/ellipse
        (:body/position body)
        (/ (:body/width  body) 2)
        (/ (:body/height body) 2)
        (cond (= faction (faction/enemy (:entity/faction player)))
              (:colors/enemy-color colors)
              (= faction (:entity/faction player))
              (:colors/friendly-color colors)
              :else
              (:colors/neutral-color colors))]]]]))
