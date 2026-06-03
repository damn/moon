(ns render.check-debug-viewer
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]
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
      (add-actor! stage
                  (data-viewer-window/create
                   {:title "Data View"
                    :data data
                    :width 500
                    :height 500
                    :skin skin}))))
  ctx)
