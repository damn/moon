(ns clojure.gdx.scenes.scene2d.ui.widget-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [moon.ui.group :as group]
            [moon.ui.widget-group]))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
  moon.ui.widget-group/WidgetGroup
  (pack! [widget-group]
    (widget-group/pack! widget-group))

  (set-opts! [widget-group opts]
    (when (contains? opts :widget-group/fill-parent?)
      (widget-group/set-fill-parent! widget-group (:widget-group/fill-parent? opts)))
    (group/set-opts! widget-group opts)))
