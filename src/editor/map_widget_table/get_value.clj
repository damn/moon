(ns editor.map-widget-table.get-value
  (:require [clojure.gdx.scene2d.actor.user-object :refer [actor-user-object]]
            [gdx.scenes.scene2d.group :as group]
            [editor.widget :as widget]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor-user-object) (group/children table))
              :let [[k _] (actor-user-object widget)]]
          [k (widget/value (get schemas k) widget schemas)])))
