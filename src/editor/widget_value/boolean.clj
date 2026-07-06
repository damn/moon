(ns editor.widget-value.boolean
  (:require [com.badlogic.gdx.scenes.scene2d.ui.checkbox :as checkbox]))

(defn f
  [_ widget _schemas]
  (checkbox/checked? widget))
