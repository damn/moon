(ns moon.action-bar.add-skill
  (:require [clojure.gdx :as gdx]
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
        button (doto (gdx/image-button
                      (doto (gdx/texture-region-drawable texture-region)
                        (gdx/texture-region-drawable-set-min-size!
                         (* scale (gdx/texture-region-get-region-width texture-region))
                         (* scale (gdx/texture-region-get-region-height texture-region)))))
                 (gdx/add-listener! (text-tooltip/create tooltip-text skin))
                 (gdx/set-user-object! skill-id))]
    (gdx/add-actor! horizontal-group button)
    (gdx/button-group-add! button-group button)
    nil))
