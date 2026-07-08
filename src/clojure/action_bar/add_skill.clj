(ns clojure.action-bar.add-skill
  (:require
            [clojure.add-listener]
            [clojure.actor.set-user-object] [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.texture :as texture]
            [clojure.button-group :as button-group]
            [clojure.group :as group]
            [clojure.image-button :as image-button]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.action-bar.get-data :as get-data]))

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
                 (clojure.add-listener/f (text-tooltip/create tooltip-text skin))
                 (clojure.actor.set-user-object/f skill-id))]
    (group/add-actor! horizontal-group button)
    (button-group/add! button-group button)
    nil))
