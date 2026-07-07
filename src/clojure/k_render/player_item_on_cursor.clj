(ns clojure.k-render.player-item-on-cursor
  (:require [clojure.item-place-position :refer [item-place-position]]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.moon-textures :as textures]))

(defn f
  [{:keys [item]}
   entity
   {:keys [ctx/textures]
    :as ctx}]
  (when-not (mouseover-actor ctx)
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (item-place-position ctx entity)
      {:center? true}]]))
