(ns moon.application.render.check-debug-viewer
  (:require [moon.stage :as stage]
            [moon.app :as app]))

(defn step
  [{:keys [ctx/controls
           ctx/app
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (app/button-just-pressed? app (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        {:type :ui/data-viewer-window
                         :title "Data View"
                         :data data
                         :width 500
                         :height 500
                         :skin skin})))
  ctx)
