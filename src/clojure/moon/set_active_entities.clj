(ns clojure.moon.set-active-entities
  (:require [clojure.content-grid.active-entities :as active-entities]))

(defn f
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))
