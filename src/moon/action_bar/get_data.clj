(ns moon.action-bar.get-data
  (:require [clojure.gdx :as gdx]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (gdx/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (gdx/get-user-object group)}))
