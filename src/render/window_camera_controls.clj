(ns render.window-camera-controls
  (:require [gdx.application :as app]
            [clojure.input :as input]
            [gdx.graphics.orthographic-camera :as camera]
            [game.constants :refer [zoom-speed]]
            [clojure.scene2d.group.find-actor :refer [find-actor]]
            [clojure.scene2d.actor.set-visible :refer [set-visible!]]
            [clojure.scene2d.actor.toggle-visible :refer [toggle-visible!]]
            [clojure.scene2d.group.children :refer [children]]))

(defn step
  [{:keys [ctx/app
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [input (app/input app)]
    (when (input/key-pressed? input (:zoom-in controls))
      (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

    (when (input/key-pressed? input (:zoom-out controls))
      (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

    (when (input/key-just-pressed? input (:close-windows-key controls))
      (->> (find-actor (:stage/root stage) "moon.ui.windows")
           children
           (run! #(set-visible! % false))))

    (when (input/key-just-pressed? input (:toggle-inventory controls))
      (-> stage
          :stage/root
          (find-actor "moon.ui.windows.inventory")
          toggle-visible!))

    (when (input/key-just-pressed? input (:toggle-entity-info controls))
      (-> stage
          :stage/root
          (find-actor "moon.ui.windows.entity-info")
          toggle-visible!)))
  ctx)
