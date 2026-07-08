(ns clojure.scene2d.actor.is-button
  (:require
            [clojure.scene2d.actor.get-parent]
            [clojure.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (clojure.scene2d.actor.get-parent/f actor)]
          (button-class? parent)))))
