(ns editor.map-widget-table.get-value
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [editor.widget-value :as widget-value])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object/f) (Group/.getChildren table))
              :let [[k _] (get-user-object/f widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
