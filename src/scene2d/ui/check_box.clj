(ns scene2d.ui.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn create
  [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))
