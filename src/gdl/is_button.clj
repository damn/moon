(ns gdl.is-button
  (:require [gdl.get-parent :refer [get-parent]]
            [gdl.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (and (get-parent actor)
             (button-class? (get-parent actor))))))
