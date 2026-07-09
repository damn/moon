(ns clojure.moon.check-debug-viewer
  (:require [clojure.input-button-just-pressed :as button-just-pressed?]
            [clojure.ui.data-viewer-window :as data-viewer-window]
            [clojure.stage :as stage]))

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (button-just-pressed?/f input (:open-debug-button controls))
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
