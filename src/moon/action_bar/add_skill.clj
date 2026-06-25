(ns moon.action-bar.add-skill
  (:require [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.group.add-actor :refer [add-actor!]]
            [scene2d.ui.button-group.add :as add]
            [scene2d.ui.image-button :as image-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [texture-region.get-region-height :refer [get-region-height]]
            [texture-region.get-region-width :refer [get-region-width]]
            [moon.action-bar.get-data :as get-data]))

(defn f
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (get-data/f action-bar)
        button (doto (image-button/create
                      (doto (texture-region-drawable/f texture-region)
                        (set-min-size!/f (* scale (get-region-width texture-region))
                                         (* scale (get-region-height texture-region)))))
                 (add-listener! (text-tooltip/create tooltip-text skin))
                 (set-user-object! skill-id))]
    (add-actor! horizontal-group button)
    (add/f! button-group button)
    nil))
