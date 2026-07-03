(ns scene2d.actor.is-button
  (:require [clojure.gdx.actor.get-parent :as get-parent]
            [clojure.gdx.button.class :as button-class]))

(let [button-class? (fn [actor]
                      (some #(= button-class/v %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (get-parent/f actor)]
          (button-class? parent)))))
