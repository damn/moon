(ns clojure.gdx.scene2d.ui.widget-group
  (:require [clojure.scene2d.group :as group]
            clojure.scene2d.ui.widget-group)
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(extend-type WidgetGroup
  clojure.scene2d.ui.widget-group/WidgetGroup
  (pack! [widget-group]
    (.pack widget-group))

  (set-opts! [widget-group opts]
    (when (contains? opts :widget-group/fill-parent?)
      (.setFillParent widget-group (:widget-group/fill-parent? opts)))
    (group/set-opts! widget-group opts)))
