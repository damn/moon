(ns clojure.moon.window-camera-controls
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.input.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.input.key-pressed :refer [f] :rename {f key-pressed?}]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(def zoom-speed 0.025)

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? input (:zoom-in controls))
    (inc-zoom! (viewport/getCamera world-viewport) zoom-speed))

  (when (key-pressed? input (:zoom-out controls))
    (inc-zoom! (viewport/getCamera world-viewport) (- zoom-speed)))

  (when (key-just-pressed? input (:close-windows-key controls))
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(actor/setVisible % false))))

  (when (key-just-pressed? input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/setVisible inventory (not (actor/isVisible inventory)))))

  (when (key-just-pressed? input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/setVisible entity-info (not (actor/isVisible entity-info)))))
  ctx)
