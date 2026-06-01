(ns entity.render.player-item-on-cursor
  (:require [game.ctx.item-place-position :refer [item-place-position]]
            [game.ctx.mouse-position :refer [mouse-position]]
            [gdx.stage :as stage]
            [moon.textures :as textures]))

(defn f
  [{:keys [item]}
   entity
   {:keys [ctx/stage
           ctx/textures]
    :as ctx}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (item-place-position ctx entity)
      {:center? true}]]))
