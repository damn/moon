(ns moon.action-bar.add-skill
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.group.add-actor :as add-actor]
            [clojure.gdx.button-group.add :as button-group-add]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [scene2d.ui.text-tooltip :as text-tooltip]
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
                      (doto (new-texture-region-drawable/f texture-region)
                        (set-min-size/f (* scale (get-region-width/f texture-region))
                                        (* scale (get-region-height/f texture-region)))))
                 (add-listener/f (text-tooltip/create tooltip-text skin))
                 (set-user-object/f skill-id))]
    (add-actor/f horizontal-group button)
    (button-group-add/f button-group button)
    nil))
