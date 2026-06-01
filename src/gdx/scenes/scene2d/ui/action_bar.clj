(ns gdx.scenes.scene2d.ui.action-bar
  (:require [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [clojure.gdx.scene2d.ui.button-group :as button-group]
            [gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create []
  (table/create
   {:table/cell-defaults {:pad 2}
    :table/rows [[{:actor (horizontal-group/create
                           {:space 2
                            :pad 2
                            :actor/name "moon.ui.action-bar.horizontal-group"
                            :actor/user-object (button-group/create
                                                {:max-check-count 1
                                                 :min-check-count 0})})
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
                {:drawable (texture-region-drawable/create*
                            {:drawable/texture-region texture-region
                             :drawable/scale 2})
                 :actor/listeners {:listener/text-tooltip [tooltip-text skin]}
                 :actor/user-object skill-id})]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill! [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))
