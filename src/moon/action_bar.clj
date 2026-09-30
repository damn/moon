(ns moon.action-bar
  (:require [gdx.texture-region :as texture-region]
            [gdx.actor.group :as group]
            [gdx.button-group :as button-group]
            [gdx.actor.group.widget.horizontal-group :as horizontal-group]
            [gdx.actor.group.widget.table :as table]
            [gdx.tooltip.text :as text-tooltip]
            [gdx.layout :as layout]
            [gdx.drawable.texture-region :as texture-region-drawable])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create []
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/create
                                       {:space 2
                                        :pad 2})
                                  (.setName "moon.ui.action-bar.horizontal-group")
                                  (.setUserObject (button-group/create
                                                           {:max-check-count 1
                                                            :min-check-count 0})))
                         :expand? true
                         :bottom? true}]]})
    (layout/set-fill-parent! true)
    (.setName "moon.ui.action-bar")))

(defn- get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor group)}))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data action-bar)
        button (doto (ImageButton.
                      (doto (texture-region-drawable/create texture-region)
                        (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                                                               (* scale (texture-region/get-region-height texture-region)))))
                 (.addListener (text-tooltip/create tooltip-text skin))
                 (.setUserObject skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (.remove ^com.badlogic.gdx.scenes.scene2d.Actor button)
    (button-group/remove! button-group button)
    nil))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data action-bar)))]
    (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor skill-button)))
