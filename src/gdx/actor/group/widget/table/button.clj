(ns gdx.actor.group.widget.table.button
  (:require
            [com.badlogic.gdx.scenes.scene2d.ui.button :as button]))

(let [button-class? (fn [actor]
                      (some #(= button/class %) (supers (class actor))))]
  (defn is? [actor]
    (or (button-class? actor)
        (when-let [parent (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)]
          (button-class? parent)))))
