(ns editor.widget-value.one-to-many
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.get-children :as get-children]))

(defn f
  [_  widget _schemas]
  (->> (get-children/f widget)
       (keep get-user-object/f)
       set))
