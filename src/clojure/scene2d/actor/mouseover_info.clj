(ns clojure.scene2d.actor.mouseover-info
  (:require
            [gdl.actor :as actor]
            [clojure.scene2d.actor.is-button :as button?]
            [clojure.scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/get-parent actor)
                            (= "inventory-cell" (actor/get-name (actor/get-parent actor)))
                            (actor/get-user-object (actor/get-parent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
