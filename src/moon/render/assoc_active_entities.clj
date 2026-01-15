(ns moon.render.assoc-active-entities
  (:require [moon.world :as world]))

(defn do!
  [{:keys [ctx/world]
    :as ctx}]
  (update ctx :ctx/world world/cache-active-entities))
