(ns moon.action-bar.get-data
  (:require [clojure.gdx.actor.get-user-object :as get-user-object])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (Group/.findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (get-user-object/f group)}))
