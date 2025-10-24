(ns clojure.vis-ui.check-box
  (:import (com.kotcrab.vis.ui.widget VisCheckBox)))

; com.badlogic.gdx.scenes.scene2d.ui.CheckBox
; CheckBox(String text, CheckBox.CheckBoxStyle style)
; CheckBox(String text, Skin skin)
; CheckBox(String text, Skin skin, String styleName)

; isChecked comes from 'Button'
(def checked? VisCheckBox/.isChecked)

(defn create [text]
  (VisCheckBox. (str text)))
