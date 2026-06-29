(ns moon.action-bar.add-skill
  (:require [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup
                                               ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f
  [action-bar
   {:keys [skill-id
           ^TextureRegion texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. texture-region)
                        (set-min-size!/f (* scale (.getRegionWidth texture-region))
                                         (* scale (.getRegionHeight texture-region)))))
                 (Actor/.addListener (text-tooltip/create tooltip-text skin))
                 (Actor/.setUserObject skill-id))]
    (Group/.addActor horizontal-group button)
    (ButtonGroup/.add button-group button)
    nil))
