(ns gdx.scenes.scene2d.ui.action-bar
  (:require [clojure.gdx.scene2d.actor :refer [remove!
                                               get-user-object
                                               set-name!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actor!]]
            [clojure.gdx.scene2d.ui.button-group :as button-group]
            [clojure.gdx.scene2d.ui.widget-group.set-fill-parent :refer [set-fill-parent!]]
            [clojure.gdx.scene2d.ui.horizontal-group :as horizontal-group]
            [gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create []
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/create
                                       {:space 2
                                        :pad 2})
                                  (set-name! "moon.ui.action-bar.horizontal-group")
                                  (set-user-object! (button-group/create
                                                     {:max-check-count 1
                                                      :min-check-count 0})))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent! true)
    (set-name! "moon.ui.action-bar")))

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
                      {:drawable (texture-region-drawable/create*
                                  {:drawable/texture-region texture-region
                                   :drawable/scale 2})})
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
