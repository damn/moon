(ns moon.entity.mouseover
  (:require [moon.entity :as entity]
            [moon.faction :as faction]))

(def outline-alpha 0.4)
(def enemy-color [1 0 0 outline-alpha])
(def friendly-color [0 1 0 outline-alpha])
(def neutral-color [1 1 1 outline-alpha])
(def mouseover-ellipse-width 5)

(defmethod entity/render :entity/mouseover?
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/world]}]
  (let [player @(:world/player-eid world)]
    [[:draw/with-line-width mouseover-ellipse-width
      [[:draw/ellipse
        (:body/position body)
        (/ (:body/width  body) 2)
        (/ (:body/height body) 2)
        (cond (= faction (faction/enemy (:entity/faction player)))
              enemy-color
              (= faction (:entity/faction player))
              friendly-color
              :else
              neutral-color)]]]]))
