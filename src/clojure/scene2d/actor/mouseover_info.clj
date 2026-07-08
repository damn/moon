(ns clojure.scene2d.actor.mouseover-info
  (:require
            [clojure.scene2d.actor.get-name]
            [clojure.scene2d.actor.get-parent]
            [clojure.scene2d.actor.get-user-object]
            [clojure.scene2d.actor.is-button :as button?]
            [clojure.scene2d.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (clojure.scene2d.actor.get-parent/f actor)
                            (= "inventory-cell" (clojure.scene2d.actor.get-name/f (clojure.scene2d.actor.get-parent/f actor)))
                            (clojure.scene2d.actor.get-user-object/f (clojure.scene2d.actor.get-parent/f actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
