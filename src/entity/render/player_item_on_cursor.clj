(ns entity.render.player-item-on-cursor
  (:require [game.ctx :as ctx]
            [game.entity :as entity]
            [gdx.stage :as stage]
            [moon.textures :as textures]))

(defmethod entity/render :player-item-on-cursor
  [[_k {:keys [item]}]
   entity
   {:keys [ctx/stage
           ctx/textures]
    :as ctx}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (ctx/mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (ctx/item-place-position ctx entity)
      {:center? true}]]))
