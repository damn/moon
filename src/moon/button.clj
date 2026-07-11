(ns moon.button
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn is? [actor]
    (or (button-class? actor)
        (when-let [parent (actor/getParent actor)]
          (button-class? parent)))))
