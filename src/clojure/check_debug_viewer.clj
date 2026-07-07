(ns clojure.check-debug-viewer
  (:require [clojure.stage :as stage]
            [clojure.ctx-button-just-pressed :refer [button-just-pressed?]]
            [clojure.data-viewer-window :as data-viewer-window]))

(defn step
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (button-just-pressed? ctx (:open-debug-button controls))
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
