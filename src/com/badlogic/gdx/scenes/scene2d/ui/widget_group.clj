(ns com.badlogic.gdx.scenes.scene2d.ui.widget-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn pack! [^WidgetGroup widget-group]
  (.pack widget-group))

(defn fill-parent! [^WidgetGroup widget-group fill-parent?]
  (.setFillParent widget-group fill-parent?))
