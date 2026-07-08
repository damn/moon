(ns clojure.actor.mouseover-info
  (:require
            [clojure.actor.get-name]
            [clojure.actor.get-parent]
            [clojure.actor.get-user-object]
            [clojure.actor.is-button :as button?]
            [clojure.actor.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (clojure.actor.get-parent/f actor)
                            (= "inventory-cell" (clojure.actor.get-name/f (clojure.actor.get-parent/f actor)))
                            (clojure.actor.get-user-object/f (clojure.actor.get-parent/f actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
