(ns gdx.scenes.scene2d.ui.widget-group
  (:require [clojure.gdx.scene2d.ui.widget-group :as widget-group]
            [gdx.scenes.scene2d.group :as group]))

(defn pack! [widget-group]
  (widget-group/pack! widget-group))

(defn set-opts! [widget-group opts]
  (when (contains? opts :widget-group/fill-parent?)
    (widget-group/set-fill-parent! widget-group (:widget-group/fill-parent? opts)))
  (group/set-opts! widget-group opts))
