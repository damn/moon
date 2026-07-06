(ns editor.map-widget-table.get-value
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.get-children :as get-children]
            [editor.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object/f) (get-children/f table))
              :let [[k _] (actor/get-user-object widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
