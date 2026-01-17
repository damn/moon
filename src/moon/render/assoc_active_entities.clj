(ns moon.render.assoc-active-entities
  (:require [moon.world.content-grid :as content-grid]))

(defn do!
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (content-grid/active-entities content-grid @player-eid)))
