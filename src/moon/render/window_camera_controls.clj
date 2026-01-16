(ns moon.render.window-camera-controls
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [moon.input :as input]
            [moon.graphics.camera :as camera])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(def zoom-speed 0.025)

; also does >1 thing, and stage inputlistener do ?
(defn do!
  [{:keys [ctx/input
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (camera/inc-zoom! (Viewport/.getCamera world-viewport) zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (camera/inc-zoom! (Viewport/.getCamera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (->> (-> stage
             .getRoot
             (.findActor "moon.ui.windows"))
         .getChildren
         (run! #(.setVisible % false))))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (-> stage
        .getRoot
        (.findActor "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (-> stage
        .getRoot
        (.findActor "moon.ui.windows")
        (.findActor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
