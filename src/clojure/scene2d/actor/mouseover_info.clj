(ns clojure.scene2d.actor.mouseover-info
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.scene2d.actor.is-button :as button?]
            [clojure.scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/getParent actor)
                            (= "inventory-cell" (actor/getName (actor/getParent actor)))
                            (actor/getUserObject (actor/getParent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
