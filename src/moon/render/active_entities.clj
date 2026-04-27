(ns moon.render.active-entities
  (:require [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (content-grid/active-entities content-grid @player-eid)))
