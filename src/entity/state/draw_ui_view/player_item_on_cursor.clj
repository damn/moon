(ns entity.state.draw-ui-view.player-item-on-cursor
  (:require [game.ctx.mouse-position :refer [mouse-position]]
            [game.state :as state]
            [gdx.stage :as stage]
            [moon.textures :as textures]))

(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]
          :as ctx}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))
