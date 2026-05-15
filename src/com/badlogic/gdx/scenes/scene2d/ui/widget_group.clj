(ns com.badlogic.gdx.scenes.scene2d.ui.widget-group
  (:require [moon.ui.group :as group]
            [moon.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(extend-type WidgetGroup
  widget-group/WidgetGroup
  (pack! [widget-group]
    (.pack widget-group))

  (set-opts! [widget-group opts]
    (when (contains? opts :widget-group/fill-parent?)
      (.setFillParent widget-group (:widget-group/fill-parent? opts)))
    (group/set-opts! widget-group opts)))
