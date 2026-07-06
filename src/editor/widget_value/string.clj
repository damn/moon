(ns editor.widget-value.string
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]))

(defn f
  [_ widget _schemas]
  (text-field/get-text widget))
