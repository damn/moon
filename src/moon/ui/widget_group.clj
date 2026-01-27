(ns moon.ui.widget-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn set-opts! [^WidgetGroup widget-group {:keys [fill-parent? pack?] :as opts}]
  (when fill-parent?
    (.setFillParent widget-group fill-parent?)) ; TODO from Widget?
  (when pack?
    (.pack widget-group))
  widget-group)
