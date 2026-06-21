(ns moon.action-bar.add-skill
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.actor.set-user-object :refer [set-user-object!]]
            [clojure.group.add-actor :refer [add-actor!]]
            [clojure.ui.button-group :as button-group]
            [clojure.ui.image-button :as image-button]
            [clojure.ui.text-tooltip :as text-tooltip]
            [clojure.map.texture-region-drawable :as texture-region-drawable]
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
