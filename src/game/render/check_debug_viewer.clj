(ns game.render.check-debug-viewer
  (:require [clojure.scene2d.stage :as stage]
            [clojure.app :as app]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/controls
           ctx/app
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? (app/input app) (:open-debug-button controls))
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
