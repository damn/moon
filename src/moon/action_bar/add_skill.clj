(ns moon.action-bar.add-skill
  (:require [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.group.add-actor :refer [add-actor!]]
            [scene2d.ui.button-group :as button-group]
            [scene2d.ui.image-button :as image-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [map.texture-region-drawable :as texture-region-drawable]
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
