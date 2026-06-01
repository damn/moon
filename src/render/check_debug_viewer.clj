(ns render.check-debug-viewer
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]))

(defn step
  [{:keys [ctx/app
           ctx/controls
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
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
