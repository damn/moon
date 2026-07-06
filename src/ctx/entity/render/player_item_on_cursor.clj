(ns ctx.entity.render.player-item-on-cursor
  (:require [ctx.item-place-position :refer [item-place-position]]
            [ctx.mouseover-actor :refer [mouseover-actor]]
            [moon.textures :as textures]))

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
