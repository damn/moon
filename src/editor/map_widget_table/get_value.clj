(ns editor.map-widget-table.get-value
  (:require [clojure.gdx.scene2d.group.children :refer [children]]
            [editor.widget :as widget])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? Actor/.getUserObject) (children table))
              :let [[k _] (.getUserObject widget)]]
          [k (widget/value (get schemas k) widget schemas)])))
