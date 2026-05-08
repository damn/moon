(ns moon.application.render.check-debug-viewer
  (:require [moon.ui.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [com.badlogic.gdx.input :as input]))

(defn step
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
                        (actor/create
                         {:type :ui/data-viewer-window
                          :title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
