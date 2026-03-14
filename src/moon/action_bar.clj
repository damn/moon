(ns moon.action-bar
  (:require [moon.image-button :as image-button]
            [moon.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup
                                               HorizontalGroup
                                               Table)))

(defn create []
  (doto (table/create
         {:rows [[{:actor (doto (doto (HorizontalGroup.)
                                  (.space 2)
                                  (.pad 2))
                            (.setName "moon.ui.action-bar.horizontal-group")
                            (.setUserObject (doto (ButtonGroup.)
                                              (.setMaxCheckCount 1)
                                              (.setMinCheckCount 0))))
                   :expand? true
                   :bottom? true}]]
          :cell-defaults {:pad 2}})
    (.setFillParent true)
    (.setName "moon.ui.action-bar")))

(defn- get-data [^Table action-bar]
  (let [group (.findActor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (Actor/.getUserObject group)}))

(defn selected-skill [action-bar]
  (when-let [skill-button (ButtonGroup/.getChecked (:button-group (get-data action-bar)))]
    (.getUserObject skill-button)))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (doto (image-button/create
                      {:drawable/texture-region texture-region
                       :drawable/scale 2
                       :tooltip tooltip-text
                       :skin skin})
                 (.setUserObject skill-id))]
    (Group/.addActor horizontal-group button)
    (ButtonGroup/.add button-group ^Button button)
    nil))

(defn remove-skill! [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (Actor/.remove button)
    (ButtonGroup/.remove button-group ^Button button)
    nil))
