(ns moon.entity.mouseover
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [moon.faction :as faction]))

(def outline-alpha 0.4)
(def enemy-color    (color/float-bits [1 0 0 outline-alpha]))
(def friendly-color (color/float-bits [0 1 0 outline-alpha]))
(def neutral-color  (color/float-bits [1 1 1 outline-alpha]))
(def mouseover-ellipse-width 5)

(defn render
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/player-eid]}]
  (let [player @player-eid]
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
