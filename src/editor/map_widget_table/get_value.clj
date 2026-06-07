(ns editor.map-widget-table.get-value
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.group.children :refer [children]]
            [editor.widget :as widget]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object) (children table))
              :let [[k _] (get-user-object widget)]]
          [k (widget/value (get schemas k) widget schemas)])))
