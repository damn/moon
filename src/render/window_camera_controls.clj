(ns render.window-camera-controls
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.group.get-children :as get-children]
            [input.key-just-pressed :as key-just-pressed?]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]))

(defn step
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport
           ctx/zoom-speed]
    :as ctx}]
  (when (key-pressed?/f input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed?/f input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed?/f input (:close-windows-key controls))
    (->> (find-actor/f (:stage/root stage) "moon.ui.windows")
         get-children/f
         (run! #(actor/set-visible! % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (find-actor/f (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/set-visible! inventory (not (actor/visible? inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (find-actor/f (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/set-visible! entity-info (not (actor/visible? entity-info)))))
  ctx)
