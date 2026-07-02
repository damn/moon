(ns moon.action-bar.add-skill
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.action-bar.get-data :as get-data])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Group)
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
                        (.setMinSize (* scale (.getRegionWidth texture-region))
                                     (* scale (.getRegionHeight texture-region)))))
                 (add-listener/f (text-tooltip/create tooltip-text skin))
                 (set-user-object/f skill-id))]
    (Group/.addActor horizontal-group button)
    (ButtonGroup/.add button-group button)
    nil))
