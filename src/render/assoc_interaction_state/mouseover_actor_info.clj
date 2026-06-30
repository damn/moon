(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [clojure.gdx :as gdx]
            [scene2d.actor.is-button :as button?]
            [scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (gdx/get-parent actor)
                            (= "inventory-cell" (gdx/get-name (gdx/get-parent actor)))
                            (gdx/get-user-object (gdx/get-parent actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
