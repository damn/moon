(ns clojure.set-active-entities
  (:require [clojure.active-entities :as active-entities]))

(defn step
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))
