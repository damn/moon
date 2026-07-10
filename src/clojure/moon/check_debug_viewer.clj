(ns clojure.moon.check-debug-viewer
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.buttons :as input-buttons]
            [clojure.ui.data-viewer-window :as data-viewer-window]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/isButtonJustPressed input (input-buttons/key-to-value (:open-debug-button controls)))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/addActor stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)
