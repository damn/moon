(ns clojure.actor.is-button
  (:require [clojure.actor.get-parent :refer [get-parent]]
            [clojure.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (and (get-parent actor)
             (button-class? (get-parent actor))))))
