(ns moon.game.render.window-camera-controls
  (:require [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.ui :as ui]))

(def zoom-speed 0.025)

(defn step
  [{:keys [ctx/input
           ctx/graphics
           ctx/stage]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (graphics/change-zoom! graphics zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (graphics/change-zoom! graphics (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (ui/close-all-windows! stage))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (ui/toggle-inventory-visible! stage))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (ui/toggle-entity-info-window! stage))
  ctx)
