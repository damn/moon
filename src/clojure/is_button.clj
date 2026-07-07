(ns clojure.is-button
  (:require [clojure.actor :as actor]
            [clojure.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (actor/get-parent actor)]
          (button-class? parent)))))
