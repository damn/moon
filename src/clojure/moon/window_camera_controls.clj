(ns clojure.moon.window-camera-controls
  (:require [clojure.actor.set-visible]
            [clojure.actor.visible]
            [clojure.group :as group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.key-pressed :refer [f] :rename {f key-pressed?}]
            [clojure.zoom-speed :refer [zoom-speed]]))

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed? input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed? input (:close-windows-key controls))
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(clojure.actor.set-visible/f % false))))

  (when (key-just-pressed? input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (clojure.actor.set-visible/f inventory (not (clojure.actor.visible/f inventory)))))

  (when (key-just-pressed? input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (clojure.actor.set-visible/f entity-info (not (clojure.actor.visible/f entity-info)))))
  ctx)
