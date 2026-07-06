(ns ctx.handle-input.player-item-on-cursor
  (:require [ctx.button-just-pressed :refer [button-just-pressed?]]
            [ctx.mouseover-actor :refer [mouseover-actor]]))

(defn f
  [eid ctx]
  (when (and (button-just-pressed? ctx :input.buttons/left)
             (not (mouseover-actor ctx)))
    [[:tx/event eid :drop-item]]))
