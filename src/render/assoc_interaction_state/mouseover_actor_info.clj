(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.actor.get-name :refer [get-name]]
            [com.badlogic.gdx.scenes.scene2d.actor.get-parent :refer [get-parent]]
            [gdx.scenes.scene2d.ui :as ui]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (get-parent actor)
                            (= "inventory-cell" (get-name (get-parent actor)))
                            (get-user-object (get-parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (ui/window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (ui/button? actor)           [:mouseover-actor/button]
     :else                     [:mouseover-actor/unspecified])))
