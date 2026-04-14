(ns moon.ui.widget-group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [moon.ui.group :as group]))

(defn set-opts! [widget-group opts]
  (when (contains? opts :widget-group/fill-parent?)
    (widget-group/set-fill-parent! widget-group (:widget-group/fill-parent? opts)))
  (group/set-opts! widget-group opts))
