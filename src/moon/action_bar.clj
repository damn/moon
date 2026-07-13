(ns moon.action-bar
  (:require [gdx.graphics.g2d.texture-region :as texture-region]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui.button-group :as button-group]
            [gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [gdx.scenes.scene2d.utils.layout :as layout]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create []
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/create
                                       {:space 2
                                        :pad 2})
                                  (actor/set-name! "moon.ui.action-bar.horizontal-group")
                                  (actor/set-user-object! (button-group/create
                                                           {:max-check-count 1
                                                            :min-check-count 0})))
                         :expand? true
                         :bottom? true}]]})
    (layout/set-fill-parent! true)
    (actor/set-name! "moon.ui.action-bar")))

(defn- get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
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
        button (doto (image-button/create
                      (doto (texture-region-drawable/create texture-region)
                        (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                                                               (* scale (texture-region/get-region-height texture-region)))))
                 (actor/add-listener! (text-tooltip/create tooltip-text skin))
                 (actor/set-user-object! skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))

(defn remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (get-data action-bar)
        button (get horizontal-group skill-id)]
    (actor/remove! button)
    (button-group/remove! button-group button)
    nil))

(defn selected-skill [action-bar]
  (when-let [skill-button (button-group/get-checked (:button-group (get-data action-bar)))]
    (actor/get-user-object skill-button)))
