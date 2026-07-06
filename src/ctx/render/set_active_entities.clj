(ns ctx.render.set-active-entities
  (:require [moon.content-grid.active-entities :as active-entities]))

(defn step
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))
