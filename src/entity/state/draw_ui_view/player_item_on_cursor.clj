(ns entity.state.draw-ui-view.player-item-on-cursor
  (:require [game.ctx.mouseover-actor :refer [mouseover-actor]]
            [moon.textures :as textures]))

(defn f
  [_ eid {:keys [ctx/textures
                 ctx/ui-mouse-position]
          :as ctx}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (mouseover-actor ctx)
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))
