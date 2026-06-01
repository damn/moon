(ns clojure.gdx.scene2d.ui.widget-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn pack! [^WidgetGroup widget-group]
  (.pack widget-group))

(defn set-fill-parent! [^WidgetGroup widget-group bool]
  (.setFillParent widget-group bool))
