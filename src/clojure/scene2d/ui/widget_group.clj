(ns clojure.scene2d.ui.widget-group
  (:require [clojure.gdx.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui WidgetGroup)))

(defn pack! [^WidgetGroup widget-group]
  (.pack widget-group))

(defn set-opts! [^WidgetGroup widget-group opts]
  (when (contains? opts :widget-group/fill-parent?)
    (.setFillParent widget-group (:widget-group/fill-parent? opts)))
  (group/set-opts! widget-group opts))
