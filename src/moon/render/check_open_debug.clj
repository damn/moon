(ns moon.render.check-open-debug
  (:require [moon.input :as input]
            [clojure.scene2d.stage :as stage]))

(defn do!
  [{:keys [ctx/controls
           ctx/input
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        ((get (:ctx/actor-fns ctx) :ui/data-viewer-window)
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
