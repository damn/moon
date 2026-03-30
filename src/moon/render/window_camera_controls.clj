; TODO use protocols for actor/camera/group/input (ctx protocol?) stage/etc. ?
; viewport ...
; => tests.
; InputMultiplex?
(ns moon.render.window-camera-controls
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.actor :as actor]
            [moon.camera :as camera]
            [moon.input :as input])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group
                                            Stage)))

(def zoom-speed 0.025) ; TODO FIXME pull out

; TODO Stage handlers somehow?
(defn do!
  [{:keys [ctx/controls
           ctx/input
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in controls))
    (camera/inc-zoom! (viewport/camera world-viewport) zoom-speed))

  (when (input/key-pressed? input (:zoom-out controls))
    (camera/inc-zoom! (viewport/camera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key controls))
    (->> (-> stage
             Stage/.getRoot
             (Group/.findActor "moon.ui.windows"))
         Group/.getChildren
         (run! #(Actor/.setVisible % false))))

  (when (input/key-just-pressed? input (:toggle-inventory controls))
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info controls))
    (-> stage
        Stage/.getRoot
        (Group/.findActor "moon.ui.windows")
        (Group/.findActor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
