(ns moon.schema.boolean
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defn malli-form [_ _schemas]
  :boolean)

(defn create
  [_ checked? {:keys [ctx/skin]}]
  (assert (boolean? checked?))
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(defn value
  [_ widget _schemas]
  (CheckBox/.isChecked widget))
