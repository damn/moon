(ns gdl.scene2d.ui.button-group ; TODO NOT AN ACTOR !
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn create [{:keys [max-check-count min-check-count]}]
  (doto (button-group/create)
    (button-group/set-max-check-count! max-check-count)
    (button-group/set-min-check-count! min-check-count)))

(def checked button-group/checked)
(def add! button-group/add!)
(def remove! button-group/remove!)
