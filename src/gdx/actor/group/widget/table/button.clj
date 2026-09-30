(ns gdx.actor.group.widget.table.button
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button)))

(let [button-class? (fn [actor]
                      (some #(= Button %) (supers (class actor))))]
  (defn is? [actor]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))
