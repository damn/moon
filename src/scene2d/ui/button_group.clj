(ns scene2d.ui.button-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn create [{:keys [max-check-count
                      min-check-count]}]
  (doto (button-group/create)
    (button-group/set-max-check-count! max-check-count)
    (button-group/set-min-check-count! min-check-count)))
