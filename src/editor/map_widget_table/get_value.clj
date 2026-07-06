(ns editor.map-widget-table.get-value
  (:require
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [editor.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object/f) (group/get-children table))
              :let [[k _] (actor/get-user-object widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
