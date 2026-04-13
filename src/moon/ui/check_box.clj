(ns moon.ui.check-box
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn create [{:keys [skin checked?]}]
  (doto (check-box/create "" skin)
    (check-box/set-checked! checked?)))
