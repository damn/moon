(ns moon.ui.widget-group
  (:require [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

; TODO Widget
(defn set-fill-parent! [^WidgetGroup widget-group fill-parent?]
  (.setFillParent widget-group fill-parent?))

(defn pack! [^WidgetGroup widget-group]
  (.pack widget-group))

(defn set-opts!
  [widget-group {:keys [fill-parent? pack?] :as opts}]
  (when fill-parent?
    (set-fill-parent! widget-group fill-parent?))
  (when pack?
    (pack! widget-group))
  (group/set-opts! widget-group opts))
