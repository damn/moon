(ns moon.render.check-open-debug
  (:require [moon.input :as input]
            [moon.stage :as stage]
            [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    ; (or mouseover-entity world-grid-cell)
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        (ui/actor
                         {:type :ui/data-viewer-window
                          :title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
