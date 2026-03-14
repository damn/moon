(ns moon.render.window-camera-controls
  (:require [moon.camera :as camera]
            [moon.input :as input]
            [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group
                                            Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def zoom-speed 0.025)

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
             Stage/.getRoot
             (Group/.findActor "moon.ui.windows"))
         Group/.getChildren
         (run! #(Actor/.setVisible % false))))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
