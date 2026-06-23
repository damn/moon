(ns scene2d.actor.is-button
  (:require [scene2d.actor.get-parent :refer [get-parent]]
            [gdl.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (and (get-parent actor)
             (button-class? (get-parent actor))))))
