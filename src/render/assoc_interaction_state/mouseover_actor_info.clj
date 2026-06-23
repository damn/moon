(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.actor.get-name :refer [get-name]]
            [scene2d.actor.get-parent :refer [get-parent]]
            [scene2d.actor.is-button :as button?]
            [scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (get-parent actor)
                            (= "inventory-cell" (get-name (get-parent actor)))
                            (get-user-object (get-parent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
