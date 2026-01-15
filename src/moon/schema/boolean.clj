(ns moon.schema.boolean
  (:require [moon.schema :as schema])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defmethod schema/malli-form :s/boolean [_ _schemas]
  :boolean)

(defmethod schema/create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (assert (boolean? checked?))
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defmethod schema/value :s/boolean
  [_ widget _schemas]
  (CheckBox/.isChecked widget))
