(ns editor.widget-value.boolean
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn f
  [_ widget _schemas]
  (CheckBox/.isChecked widget))
