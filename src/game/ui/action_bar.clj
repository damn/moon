(ns game.ui.action-bar
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [moon.ui.action-bar :as action-bar])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup)))

(defmethod actor/create :ui/action-bar [_]
  (actor/create
   {:type :ui/table
    :table/cell-defaults {:pad 2}
    :table/rows [[{:actor (actor/create
                           {:type :ui/horizontal-group ; TODO maybe 'horizontal-button-group'
                            :space 2
                            :pad 2
                            :actor/name "moon.ui.action-bar.horizontal-group"
                            :actor/user-object (doto (ButtonGroup.)
                                                 (.setMaxCheckCount 1)
                                                 (.setMinCheckCount 0))})
                   :expand? true
                   :bottom? true}]]
    :actor/name "moon.ui.action-bar"
    :widget-group/fill-parent? true}))

(defn- get-data [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/user-object group)}))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Table
  action-bar/ActionBar
  (selected-skill [action-bar]
    (when-let [skill-button (ButtonGroup/.getChecked (:button-group (get-data action-bar)))]
      (actor/user-object skill-button)))

  (add-skill!
    [action-bar
     {:keys [skill-id
             texture-region
             tooltip-text]}
     skin]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (actor/create
                  {:type :ui/image-button
                   :drawable {:drawable/texture-region texture-region
                              :drawable/scale 2}
                   :actor/listeners {:listener/text-tooltip [tooltip-text skin]}
                   :actor/user-object skill-id})]
      (group/add-actor! horizontal-group button)
      (ButtonGroup/.add button-group ^Button button)
      nil))

  (remove-skill! [action-bar skill-id]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (get horizontal-group skill-id)]
      (actor/remove! button)
      (ButtonGroup/.remove button-group ^Button button)
      nil)))
