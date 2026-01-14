(ns moon.ui-actors.action-bar
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.image-button :as image-button])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup
                                               HorizontalGroup
                                               Table)))

(defn- get-data [action-bar]
  (let [group (.findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject group)}))

(defn create [_ctx]
  (table/create
   {:rows [[{:actor (doto (doto (HorizontalGroup.)
                            (.space 2)
                            (.pad 2))
                      (.setName "moon.ui.action-bar.horizontal-group")
                      (.setUserObject (doto (ButtonGroup.)
                                        (.setMaxCheckCount 1)
                                        (.setMinCheckCount 0))))
             :expand? true
             :bottom? true}]]
    :actor/name "moon.ui.action-bar"
    :cell-defaults {:pad 2}
    :fill-parent? true}))

(extend-type Table
  action-bar/ActionBar
  (selected-skill [action-bar]
    (when-let [skill-button (ButtonGroup/.getChecked (:button-group (get-data action-bar)))]
      (.getUserObject skill-button)))

  (add-skill!
    [action-bar
     {:keys [skill-id
             texture-region
             tooltip-text]}
     skin]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (image-button/create
                  {:actor/user-object skill-id
                   :drawable/texture-region texture-region
                   :drawable/scale 2
                   :tooltip tooltip-text
                   :skin skin})]
      (.addActor horizontal-group button)
      (ButtonGroup/.add button-group ^Button button)
      nil))

  (remove-skill! [action-bar skill-id]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (get horizontal-group skill-id)]
      (.remove button)
      (ButtonGroup/.remove button-group ^Button button)
      nil)))
