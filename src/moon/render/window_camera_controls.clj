(ns moon.render.window-camera-controls
  (:require [moon.input :as input]
            [moon.graphics.camera :as camera]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(def zoom-speed 0.025)

; also does >1 thing, and stage inputlistener do ?
(defn do!
  [{:keys [ctx/input
           ctx/graphics
           ctx/stage]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (camera/inc-zoom! (Viewport/.getCamera (:graphics/world-viewport graphics)) zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (camera/inc-zoom! (Viewport/.getCamera (:graphics/world-viewport graphics)) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (ui/close-all-windows! stage))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (ui/toggle-inventory-visible! stage))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (ui/toggle-entity-info-window! stage))
  ctx)
