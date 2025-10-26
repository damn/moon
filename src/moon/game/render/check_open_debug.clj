(ns moon.game.render.check-open-debug
  (:require [moon.input :as input]
            [moon.ui :as ui]))

(defn step
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    (let [data (or (and (:world/mouseover-eid world) @(:world/mouseover-eid world))
                   @((:world/grid world) (mapv int (:graphics/world-mouse-position graphics))))]
      (ui/show-data-viewer! stage data)))
  ctx)
