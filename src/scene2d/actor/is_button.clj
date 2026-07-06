(ns scene2d.actor.is-button
  (:require [clojure.gdx.actor.get-parent :as get-parent]
            [com.badlogic.gdx.scenes.scene2d.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (get-parent/f actor)]
          (button-class? parent)))))
