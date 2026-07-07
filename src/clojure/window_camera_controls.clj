(ns clojure.window-camera-controls
  (:require [clojure.group :as group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.key-just-pressed :as key-just-pressed?]
            [clojure.key-pressed :as key-pressed?]
            [clojure.set-visible]
            [clojure.visible]
            [clojure.zoom-speed :refer [zoom-speed]]))

(defn step
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed?/f input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed?/f input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed?/f input (:close-windows-key controls))
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(clojure.set-visible/f % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (clojure.set-visible/f inventory (not (clojure.visible/f inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (clojure.set-visible/f entity-info (not (clojure.visible/f entity-info)))))
  ctx)
