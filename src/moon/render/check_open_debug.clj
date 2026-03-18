(ns moon.render.check-open-debug
  (:require [moon.data-viewer-window :as data-viewer-window]
            [moon.input :as input])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (Stage/.addActor stage
                       (data-viewer-window/create
                        {:title "Data View"
                         :data data
                         :width 500
                         :height 500
                         :skin skin}))))
  ctx)
