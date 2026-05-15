(ns com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defn create [{:keys [^Skin skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(def checked? CheckBox/.isChecked)
