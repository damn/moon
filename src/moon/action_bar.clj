(ns moon.action-bar
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.texture :as texture]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.button :as button]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.table :as moon-table]))

(defn create []
  (doto (table/new)
    (moon-table/set-opts! {:table/cell-defaults {:pad 2}
                           :table/rows [[{:actor (doto (horizontal-group/new)
                                                   (horizontal-group/space 2)
                                                   (horizontal-group/pad 2)
                                                   (actor/set-name! "moon.ui.action-bar.horizontal-group")
                                                   (actor/set-user-object! (doto (button-group/new)
                                                                          (button-group/setMaxCheckCount 1)
                                                                          (button-group/setMinCheckCount 0))))
                                          :expand? true
                                          :bottom? true}]]})
    (layout/setFillParent true)
    (actor/set-name! "moon.ui.action-bar")))

(defn- get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/get-user-object group)}))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data action-bar)
        button (doto (image-button/new
                      (doto (texture-region-drawable/new texture-region)
                        (texture-region-drawable/setMinSize (* scale (texture-region/getRegionWidth texture-region))
                                        (* scale (texture-region/getRegionHeight texture-region)))))
                 (actor/add-listener! (text-tooltip/new tooltip-text skin))
                 (actor/set-user-object! skill-id))]
    (group/addActor horizontal-group button)
    (button-group/add button-group button)
    nil))

(defn remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove button-group button)
    nil))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/getChecked (:button-group (get-data action-bar)))]
    (actor/get-user-object skill-button)))
