(ns editor.widget-value.one-to-many
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f
  [_  widget _schemas]
  (->> (group/get-children widget)
       (keep actor/get-user-object)
       set))
