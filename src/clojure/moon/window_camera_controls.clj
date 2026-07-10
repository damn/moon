(ns clojure.moon.window-camera-controls
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(def zoom-speed 0.025)

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-in controls)))
    (inc-zoom! (viewport/getCamera world-viewport) zoom-speed))

  (when (input/isKeyPressed input (input-keys/key-to-value (:zoom-out controls)))
    (inc-zoom! (viewport/getCamera world-viewport) (- zoom-speed)))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:close-windows-key controls)))
    (->> (group/findActor (:stage/root stage) "moon.ui.windows")
         group/getChildren
         (run! #(actor/setVisible % false))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-inventory controls)))
    (let [inventory (group/findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/setVisible inventory (not (actor/isVisible inventory)))))

  (when (input/isKeyJustPressed input (input-keys/key-to-value (:toggle-entity-info controls)))
    (let [entity-info (group/findActor (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/setVisible entity-info (not (actor/isVisible entity-info)))))
  ctx)
