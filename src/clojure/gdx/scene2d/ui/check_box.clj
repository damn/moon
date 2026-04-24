(ns clojure.gdx.scene2d.ui.check-box
  (:require clojure.scene2d.ui.check-box)
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defn create [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(extend-type CheckBox
  clojure.scene2d.ui.check-box/CheckBox
  (checked? [check-box]
    (.isChecked check-box)))
