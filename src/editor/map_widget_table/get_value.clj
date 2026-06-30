(ns editor.map-widget-table.get-value
  (:require [clojure.gdx :as gdx]
            [editor.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? gdx/get-user-object) (gdx/get-children table))
              :let [[k _] (gdx/get-user-object widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
