(ns editor.create-widget.boolean
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Skin)))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))
