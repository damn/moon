(ns moon.ui.action-bar
  (:require [moon.ui.actor :as actor]
            [moon.ui.button-group :as button-group]
            [moon.ui.group :as group]
            [moon.ui.horizontal-group :as horizontal-group]
            [moon.ui.image-button :as image-button]
            [moon.ui.table :as table]))

(defn create []
  (table/create
   {:rows [[{:actor (doto (horizontal-group/create {:pad 2
                                                    :space 2})
                      (actor/set-name! "moon.ui.action-bar.horizontal-group")
                      (actor/set-user-object! (button-group/create
                                               {:min-check-count 0
                                                :max-check-count 1})))
             :expand? true
             :bottom? true}]]
    :actor/name "moon.ui.action-bar"
    :cell-defaults {:pad 2}
    :fill-parent? true}))

(defn- get-data [action-bar]
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/user-object group)}))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/checked (:button-group (get-data action-bar)))]
    (actor/user-object skill-button)))

(defn add-skill!
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
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill! [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))
