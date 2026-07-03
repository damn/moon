(ns render.assoc-interaction-state.mouseover-actor-info
  (:require [clojure.gdx.actor.get-name :as get-name]
            [clojure.gdx.actor.get-parent :as get-parent]
            [clojure.gdx.actor.get-user-object :as get-user-object]
            [scene2d.actor.is-button :as button?]
            [scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (get-parent/f actor)
                            (= "inventory-cell" (get-name/f (get-parent/f actor)))
                            (get-user-object/f (get-parent/f actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
