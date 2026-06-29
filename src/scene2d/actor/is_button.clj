(ns scene2d.actor.is-button
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button)))

(let [button-class? (fn [actor]
                      (some #(= Button %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (when-let [parent (Actor/.getParent actor)]
          (button-class? parent)))))
