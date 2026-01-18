(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group
  (:require [moon.ui.group :as group]
            [moon.ui.widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(extend-type WidgetGroup
  moon.ui.widget-group/WidgetGroup
  (set-opts! [widget-group {:keys [fill-parent? pack?] :as opts}]
    (when fill-parent?
      (.setFillParent widget-group fill-parent?)) ; TODO from Widget?
    (when pack?
      (.pack widget-group))
    (group/set-opts! widget-group opts)))
