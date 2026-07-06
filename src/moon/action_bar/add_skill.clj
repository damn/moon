(ns moon.action-bar.add-skill
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [moon.action-bar.get-data :as get-data]))

(defn f
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (doto (image-button/new
                      (doto (texture-region-drawable/new texture-region)
                        (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                                        (* scale (texture-region/get-region-height texture-region)))))
                 (actor/add-listener! (text-tooltip/create tooltip-text skin))
                 (actor/set-user-object! skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))
