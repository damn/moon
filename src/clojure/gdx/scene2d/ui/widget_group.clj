(ns clojure.gdx.scene2d.ui.widget-group
  (:require [clojure.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn pack! [^WidgetGroup widget-group]
  (.pack widget-group))

(defn set-opts! [widget-group opts]
  (when (contains? opts :widget-group/fill-parent?)
    (WidgetGroup/.setFillParent widget-group (:widget-group/fill-parent? opts)))
  (group/set-opts! widget-group opts))
