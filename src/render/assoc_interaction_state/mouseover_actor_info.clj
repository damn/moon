(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [gdl.actor.get-user-object :refer [get-user-object]]
            [gdl.actor.get-name :refer [get-name]]
            [gdl.actor.get-parent :refer [get-parent]]
            [gdl.actor.is-button :as button?]
            [gdl.actor.is-window-title-bar :as window-title-bar?]))

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
