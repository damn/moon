(ns com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.check-box :as check-box])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defmethod actor/create :ui/check-box
  [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(extend-type CheckBox
  check-box/CheckBox
  (checked? [this]
    (.isChecked this)))
