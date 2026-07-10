(ns clojure.ui.action-bar.get-data
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/getUserObject group)}))
