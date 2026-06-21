(ns moon.action-bar.add-skill
  (:require [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]
            [com.badlogic.gdx.scenes.scene2d.group.add-actor :refer [add-actor!]]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.action-bar.get-data :as get-data]))

(defn f
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [{:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (doto (image-button/create
                      (texture-region-drawable/create*
                       {:drawable/texture-region texture-region
                        :drawable/scale 2}))
                 (add-listener! (text-tooltip/create tooltip-text skin))
                 (set-user-object! skill-id))]
    (add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))
