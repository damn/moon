(ns editor.map-widget-table.get-value
  (:require [editor.widget-value :as widget-value])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? Actor/.getUserObject) (Group/.getChildren table))
              :let [[k _] (Actor/.getUserObject widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
