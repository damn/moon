(ns clojure.actor.is-button
  (:require
            [clojure.actor.get-parent]
            [clojure.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (clojure.actor.get-parent/f actor)]
          (button-class? parent)))))
