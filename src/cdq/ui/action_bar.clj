(ns cdq.ui.action-bar
  (:require [moon.scene2d.actor :as actor]
            [moon.scene2d.group :as group]
            [moon.scene2d.ui.button-group :as button-group]
            [moon.scene2d.ui.horizontal-group :as horizontal-group]
            [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [cdq.ui.image-button :as image-button]))

(defmethod stage/build :actor/action-bar [_]
  (table/create
   {:rows [[{:actor (doto (horizontal-group/create {:pad 2
                                                    :space 2})
                      (actor/set-name! "cdq.ui.action-bar.horizontal-group")
                      (actor/set-user-object! (button-group/create
                                               {:min-check-count 0
                                                :max-check-count 1})))
             :expand? true
             :bottom? true}]]
    :actor/name "cdq.ui.action-bar"
    :cell-defaults {:pad 2}
    :fill-parent? true}))

(defn- get-data [action-bar]
  (let [group (group/find-actor action-bar "cdq.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/user-object group)}))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/checked (:button-group (get-data action-bar)))]
    (actor/user-object skill-button)))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (image-button/create
                {:actor/user-object skill-id
                 :drawable/texture-region texture-region
                 :drawable/scale 2
                 :tooltip tooltip-text})]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill! [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))
