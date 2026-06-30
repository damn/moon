(ns editor.map-widget-table.get-value
  (:require [clojure.gdx :as gdx]
            [editor.widget-value :as widget-value])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? Actor/.getUserObject) (gdx/get-children table))
              :let [[k _] (Actor/.getUserObject widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
