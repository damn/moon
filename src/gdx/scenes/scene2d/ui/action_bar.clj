(ns gdx.scenes.scene2d.ui.action-bar
  (:require [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.actor.remove :refer [remove!]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]
            [com.badlogic.gdx.scenes.scene2d.group.add-actor :refer [add-actor!]]
            [com.badlogic.gdx.scenes.scene2d.group.find-actor :refer [find-actor]]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn- get-data [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (get-user-object group)}))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/checked (:button-group (get-data action-bar)))]
    (get-user-object skill-button)))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (doto (image-button/create
                      (texture-region-drawable/create*
                       {:drawable/texture-region texture-region
                        :drawable/scale 2}))
                 (add-listener! (text-tooltip/create tooltip-text skin))
                 (set-user-object! skill-id))]
    (add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill! [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (remove! button)
    (button-group/remove! button-group button)
    nil))
