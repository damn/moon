(ns moon.action-bar.add-skill
  (:require [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup
                                               ImageButton)))

(defn f
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (doto (ImageButton.
                      (doto (texture-region-drawable/f texture-region)
                        (set-min-size!/f (* scale (TextureRegion/.getRegionWidth texture-region))
                                         (* scale (TextureRegion/.getRegionHeight texture-region)))))
                 (Actor/.addListener (text-tooltip/create tooltip-text skin))
                 (Actor/.setUserObject skill-id))]
    (Group/.addActor horizontal-group button)
    (ButtonGroup/.add button-group button)
    nil))
