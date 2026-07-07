(ns clojure.mouseover-actor-info
  (:require
            [clojure.get-name]
            [clojure.get-parent]
            [clojure.get-user-object]
            [clojure.is-button :as button?]
            [clojure.is-window-title-bar :as window-title-bar?]))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (clojure.get-parent/f actor)
                            (= "inventory-cell" (clojure.get-name/f (clojure.get-parent/f actor)))
                            (clojure.get-user-object/f (clojure.get-parent/f actor)))]
    (cond
     inventory-slot
     [:mouseover-actor/inventory-cell inventory-slot]

     (window-title-bar?/f actor)
     [:mouseover-actor/window-title-bar]

     (button?/f actor)
     [:mouseover-actor/button]

     :else
     [:mouseover-actor/unspecified])))
