(ns editor.ui.widget.boolean
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn value [widget]
  (.isChecked ^CheckBox widget))

(defn create [state checked?]
  (doto (CheckBox. "" (:skin @state))
    (.setChecked checked?)))
