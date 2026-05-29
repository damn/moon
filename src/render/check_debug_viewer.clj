(ns render.check-debug-viewer
  (:require [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]
            [game.ctx :as ctx]))

(defn step
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (ctx/button-just-pressed? ctx (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
