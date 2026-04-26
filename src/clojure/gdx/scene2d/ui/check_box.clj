(ns clojure.gdx.scene2d.ui.check-box
  (:require [clojure.gdx.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defmethod actor/create :ui/check-box
  [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defn checked? [^CheckBox check-box]
  (.isChecked check-box))
