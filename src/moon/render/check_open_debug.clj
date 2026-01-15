(ns moon.render.check-open-debug
  (:require [moon.input :as input]
            [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/world
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    ; (or mouseover-entity world-grid-cell)
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @((:world/grid world) (mapv int world-mouse-position)))]
      (ui/show-data-viewer! stage data skin)))
  ctx)
