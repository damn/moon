; TODO use protocols for actor/camera/group/input (ctx protocol?) stage/etc. ?
; viewport ...
; => tests.
; InputMultiplex?
(ns moon.render.window-camera-controls
  (:require [clojure.scene2d.group :as group]
            [clojure.viewport :as viewport]
            [clojure.scene2d.actor :as actor]
            [clojure.camera :as camera]
            [moon.input :as input]
            [moon.stage :as stage]))

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
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (input/key-just-pressed? input (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
