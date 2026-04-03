(ns moon.ui-actors.action-bar
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.action-bar :as action-bar]
            [moon.actor :as actor]
            [moon.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create [_ctx]
  (doto (gdx-table/create)
    (table/set-cell-defaults! {:pad 2})
    (table/add-rows! [[{:actor (doto (horizontal-group/create)
                                 (horizontal-group/space! 2)
                                 (horizontal-group/pad! 2)
                                 (actor/set-name! "moon.ui.action-bar.horizontal-group")
                                 (actor/set-user-object! (doto (button-group/create)
                                                           (button-group/set-max-check-count! 1)
                                                           (button-group/set-min-check-count! 0))))
                        :expand? true
                        :bottom? true}]])
    (widget-group/set-fill-parent! true)
    (actor/set-name! "moon.ui.action-bar")))

(defn- get-data [action-bar]
  (let [group (group/find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (actor/user-object group)}))

(extend-type Table
  action-bar/ActionBar
  (selected-skill [action-bar]
    (when-let [skill-button (button-group/checked (:button-group (get-data action-bar)))]
      (actor/user-object skill-button)))

  (add-skill!
    [action-bar
     {:keys [skill-id
             texture-region
             tooltip-text]}
     skin]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (doto (image-button/create (doto (texture-region-drawable/create texture-region)
                                              (drawable/set-min-size! (* 2 (texture-region/width texture-region))
                                                                      (* 2 (texture-region/height texture-region)))))
                   (actor/add-listener! (text-tooltip/create tooltip-text skin))
                   (actor/set-user-object! skill-id))]
      (group/add-actor! horizontal-group button)
      (button-group/add! button-group button)
      nil))

  (remove-skill! [action-bar skill-id]
    (let [{:keys [horizontal-group button-group]} (get-data action-bar)
          button (get horizontal-group skill-id)]
      (actor/remove! button)
      (button-group/remove! button-group button)
      nil)))
