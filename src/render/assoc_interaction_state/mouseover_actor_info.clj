(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.get-name :refer [get-name]]
            [gdl.get-parent :refer [get-parent]]
            [gdl.is-button :as button?]
            [gdl.is-window-title-bar :as window-title-bar?]))

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
