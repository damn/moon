(ns clojure.scene2d.actor.is-button
  (:require
            [gdl.scenes.scene2d.actor :as actor]
            [clojure.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (actor/get-parent actor)]
          (button-class? parent)))))
