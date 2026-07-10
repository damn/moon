(ns clojure.ui.action-bar.get-data
  (:require
            [gdl.scenes.scene2d.actor :as actor] [clojure.scene2d.group :as group]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/get-user-object group)}))
