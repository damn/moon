(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [scene2d.actor.is-button :as button?]
            [scene2d.actor.is-window-title-bar :as window-title-bar?])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn mouseover-actor-info [^Actor actor]
  (let [inventory-slot (and (.getParent actor)
                            (= "inventory-cell" (.getName (.getParent actor)))
                            (.getUserObject (.getParent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
