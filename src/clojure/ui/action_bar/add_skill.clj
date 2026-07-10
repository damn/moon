(ns clojure.ui.action-bar.add-skill
  (:require
            [gdl.scenes.scene2d.actor :as actor] [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.graphics.texture :as texture]
            [clojure.ui.button-group :as button-group]
            [clojure.scene2d.group :as group]
            [gdl.scenes.scene2d.ui.image-button :as image-button]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.ui.action-bar.get-data :as get-data]))

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
                        (texture-region-drawable/set-min-size! (* scale (texture-region/getRegionWidth texture-region))
                                        (* scale (texture-region/getRegionHeight texture-region)))))
                 (actor/add-listener (text-tooltip/create tooltip-text skin))
                 (actor/set-user-object skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))
